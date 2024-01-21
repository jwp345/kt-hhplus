# kt-hhplus
## 항해 플러스 3기 프로젝트 기록소
+ 할 일들 List up
  + 핀포인트, 스카우터 등 모니터링 툴 사용
  + 그래들 빌드 시 코틀린 컴파일러가 돌아가서 메모리 많이 소요되는데 그래들 튜닝 필요
  + redis로 캐싱?
  + 밸리데이션 어노테이션으로 빼기
  + 일관성을 위해 booking date db 컬럼 LocalDateTime로 변경 하기
  + 테스트 코드 redis 초기화 방안 고민하기
  + 추후 도커 이미지 등을 위해 테스트 컨네이너 등을 사용하여 로컬이 아닌 테스트를 위한 환경 구성하기
  + 예약 이력도 추후 적재 할 지 고민
  + 유효 토큰 대기열들 일정 시간 후 만료를 어떻게 시켜야할지 스케줄러로 그냥 할 지 고민?
  + v1.5 예약 정보를 redisson 캐싱하는 방식으로 바꾸고, redis에서 먼저 조회한 애가 있으면 응답 보내거나 예외 보내서 db까지 조회가 안되도록 지금 event 발행하는 부분을 유효 토큰 대기열 ttl 만료시 pub-sub 방식으로?
  + v2는 만약 redis같은 nosql을 못쓴다고할 때, rdb로만 구현하고 싶을 경우 어떻게 할 건지? 그렇다면 만료시간을 정해두고 들어오게 하는 방식으로 변경
  + v1:
    + redis를 대기열 구현할 때만 사용하며, 예약정보는 rdb에서 한번에 관리하고 낙관적락을 사용하여 동시성 이슈를 관리하며 따로 캐시는 안함
    + 예약정보는 redis로 관리를 안하는데 그 이유는 예약정보를 관리하려면 분산락을 사용하여야 하는데, 예약 정보를 조회하는데 락 획득 시도를 하는데 드는 비용이 더 들것같아서임
  + v1.5 : 
    + redis를 예약정보와 대기열 구현 시 사용하며, 요청이 발생할 때마다 redis의 토큰 정보에 ttl을 늘려주는 형식을 사용하고,
    + pub-sub방식으로 redis ttl을 걸어 만료 시 대기열에서 가져오는 형식으로 구현 할 것임.
    + 예약 정보를 cache하여 cache miss 시 db 에서 예약 가능한 좌석인지 조회하는 아키텍처로 구성
  + v1, v1.5의 한계 : Redis 혹은 nosql 없이 대기열 구현이 불가 만약 rdb로 구현을 해야한다면? 자료구조에 종속적임

+ 주요 고민들
  + 동시성 문제를 위해 어떤 방법을 사용할 것인가?
    + Atomic Value or Synchronized : 인스턴스 확장될 경우 동시성 보장이 안됨
    + db의 낙관적락, 비관적 락 or 분산락
    + 예약 정보를 저장해 놓은 Booking 테이블에는 낙관적 락을 사용하여 동시성 문제 방지
    + 낙관적 락을 걸지 않을 경우, 1차 캐시에 저장되는 요소 때문에 고객의 잔고에는 영향이 없으나, 이력이 동시에 이뤄진 요청만큼 생성되는 문제가 발생하므로 낙관적 락으로 해결
  + 보안을 위해 uuid를 유저를 구분하기 위한 주로 파라미터로 사용하기로 결정
    + 문제점
      + mysql(mairaDB)의 클러스터드 인덱스는 B-트리 구조로 되어 있어 항상 정렬된 상태를 유지하여 무작위 값을 인덱스로 배치하게 되면 데이터를 추가할 때마다 구조를 재배치해야하므로 성능에 영향을 미침.
      + 따라서, 첫번째 컴포넌트로 timestamp 기반 UUID를 생성하여 성능을 최적화 시킬 것임
      + UUID v1, v6, v7이 시간 기반 UUID이나, 128비트로 용량 소모가 큼
    + 해결 방안
      + hibernate tsid를 사용(id와 같이 사용하려 했으나 tsID를 사용할 경우 같이 못쓰는 문제가 있고 의미가 있을까싶어서 tsID만 사용하기로 결정)
  + 기존 lettuce 라이브러리 대신 redisson 사용
    + 기존 스핀락 방식(lettuce) 대신 pub-sub 형태로 분산락을 구현하였기에 성능상 이점 때문에 채택하였으나.. 오버엔지니어링이란 사실을 알고 그냥 lettuce 쓸 걸 그랬음
  + 대기열 설계
    + 첫 설계: Redis에 ttl 설정 하여 시간 만료됐을 경우 이벤트 리스너 달아서 pub-sub 방식으로 대기열 Atomic Long의 수를 증가 시킨다.
      + 문제점 : 너무 빈번하게 이벤트가 발생하여 update가 일어나서 redis에서 처리하긴 부담스럽다.(Redis는 쓰기의 성능이 그리 좋지 않다.), 생성한 토큰을 서버에 저장 안해놓기에 유효성 검증하기가 까다롭다. 암호 알고리즘에 의존을 해야하는 상황이 올 수 있다.
      + 해결 방안: 레디스 큐로 전체 토큰을 저장해 놓은 후, 토큰으로 현재 처리할 수 있는 양을 정해 놓은 한계치까지만 hashset에 저장 시켜 입장 시킨다. 
      + -> hash set으로 현재 들어온 코인들을 저장해놓고 대기 후 요청 들어올 때마다 유효 토큰인지 검사 후 입장
      + -> 결제 시, hashSet에서 코인 삭제
      + 예약 정보는 ttl 걸지 말고 예약이 된 시간을 저장해놓은 후, 미결제했는데 시간 지났다면 배치프로세스를 돌면서 hashSet에서 코인 일괄 삭제
  + 결제를 구현할 때 결제 이력이 실패하면 결제 자체를 취소시키는 게 올바른 방향이 아닌 것 같은데 좋은 방법은?
    + 트랜잭션 분리와 결제 이력이 실패 해도 결제를 위한 트랜잭션에는 영향을 미치지 않도록 비동기 TransactionalEventListener를 사용하여 구현
  + redisson 라이브러리를 사용할 경우 종속적인 클래스 정보가 포함된 걸 어떻게 종속적이지 않게 바꿀 수 있을지 고민
    + https://zorba91.tistory.com/352 참고하여 codec 설정 변경
  + redisson 을 이용하여 키 만료 이벤트 발신 및 수신

+ 알게 된 점
  + given(ticketRepository.getLockAndReserveMap().mapCache.contains(any(ConcertInfo::class.java)))
    .willReturn(true) -> 이렇게 junt + bdd로 테스트 할 경우 mapCache가 null을 반환해 테스트가 안되지만,
  + every { ticketRepository.getLockAndReserveMap().mapCache.contains(any()) } returns false -> mockk로 테스트 할 경우는 된다
  + 결론 : mockk가 좀 더 파워풀하다.
  + publishEvent()메소드를 사용하여 이벤트를 전송할 때 data class 말고 List<>() 같이 단순 래핑해서 보낼 시 제대로 메시지 리스너가 동작하지 않는다.


참조: https://www.baeldung.com/java-generating-time-based-uuids
<br>https://ssdragon.tistory.com/162
<br>https://github.com/redisson/redisson/wiki/7.-distributed-collections