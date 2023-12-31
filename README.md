# kt-hhplus
## 항해 플러스 3기 프로젝트 기록소
+ 할 일들 List up
  + 결제 시 중복 결제를 방지하려면 어떻게 해야할까? 낙관적 락?
  + 밸리데이션, 락 등 어노테이션으로 빼던가 하기
  + 테스트 컨네이너 등을 사용하여 로컬이 아닌 테스트를 위한 환경 구성하기
  + 예약 이력도 쌓을 것인가?
  + db 적재 실패나 redis 적재 실패 시 로그 남기기 등 로그 필요한 곳에 로깅하기
  

+ 질문할 사항들
  + 레디스의 락 획득 시도 같은 경우는 재시도로직을 걸어놓았는데, db에서도 재시도 로직은 필요할까요? 필요하다면 락 획득을 해야하는 곳만?
    + -> 재시도 로직을 RedisTimeOutException에 걸어 놓을 경우, 불필요한 부하가 추가로 발생한다.(어차피 연결이 안된다면 다시 시도해도 안될 것이므로) 재시도 로직 제거
  + 업데이트 로직 처리할 때 save() 메소드를 선언하여 jpa 내에서 변경 감지로 업데이트를 구현하는게 좋나 아니면 @Query로 직접 update문을 작성하는게 좋을까요?
  + -> 긍정적 락 구현할 때 쓰는 거아니면 굳이 그럴 필요 없다.
  + 결제를 구현할 때 결제 이력이 실패하면 결제 자체를 취소시키는 게 올바른 방향이 아닌 것 같은데, 그렇다면 결제 이력이 실패했을 경우 로그로만 남기는 게 좋을까요?
  + -> 자동화를 위해 이벤트 방식으로 처리 하자 pub 방식으로 처리하여 재시도까지 자동화 하는 방향으로 


+ 주요 고민들
  + 동시성 문제를 위해 어떤 방법을 사용할 것인가?
    + Atomic Value or Synchronized : 인스턴스 확장될 경우 동시성 보장이 안됨
    + db의 낙관적락, 비관적 락 or 분산락
  + 보안을 위해 uuid를 유저를 구분하기 위한 주로 파라미터로 사용하기로 결정
    + 문제점
      + mysql(mairaDB)의 클러스터드 인덱스는 B-트리 구조로 되어 있어 항상 정렬된 상태를 유지하여 무작위 값을 인덱스로 배치하게 되면 데이터를 추가할 때마다 구조를 재배치해야하므로 성능에 영향을 미침.
      + 따라서, 첫번째 컴포넌트로 timestamp 기반 UUID를 생성하여 성능을 최적화 시킬 것임
      + UUID v1, v6, v7이 시간 기반 UUID이나, 128비트로 용량 소모가 큼
    + 해결 방안
      + hibernate tsid를 사용
  + lock 대기시간
    + http keep-alive 시간보다 짧게 가져가자
    + 보통 keep-alive 시간이 5초이므로 보다 짧게 4초까지 락 대기 시간 설정
  + 기존 lettuce 라이브러리 대신 redisson 사용
    + 기존 스핀락 방식 대신 pub-sub 형태로 분산락을 구현하였기에 성능상 이점 때문에 채택
  + 대기열 설계
    + 첫 설계: Redis에 ttl 설정 하여 시간 만료됐을 경우 이벤트 리스너 달아서 pub-sub 방식으로 대기열 Atomic Long의 수를 증가 시킨다.
      + 문제점 : 너무 빈번하게 이벤트가 발생하여 update가 일어나서 redis에서 처리하긴 부담스럽다.(Redis는 쓰기의 성능이 그리 좋지 않다.), 생성한 토큰을 서버에 저장 안해놓기에 유효성 검증하기가 까다롭다. 암호 알고리즘에 의존을 해야하는 상황이 올 수 있다.
      + 해결 방안: 큐로 전체 토큰을 저장해 놓은 후, 토큰으로 현재 처리할 수 있는 양을 정해 놓은 한계치까지만 hashset에 저장 시켜 입장 시킨다. 
      + -> hash set으로 현재 들어온 코인들을 저장해놓고 대기 후 요청 들어올 때마다 유효 토큰인지 검사 후 입장
      + -> 결제 시, hashSet에서 코인 삭제
      + 예약 정보는 ttl 걸지 말고 예약이 된 시간을 저장해놓은 후, 미결제했는데 시간 지났다면 배치프로세스를 돌면서 hashSet에서 코인 일괄 삭제

+ 알게 된 점
  + given(ticketRepository.getLockAndReserveMap().mapCache.contains(any(ConcertInfo::class.java)))
    .willReturn(true) -> 이렇게 junt + bdd로 테스트 할 경우 mapCache가 null을 반환해 테스트가 안되지만,
  + every { ticketRepository.getLockAndReserveMap().mapCache.contains(any()) } returns false -> mockk로 테스트 할 경우는 된다
  + 결론 : mockk가 좀 더 파워풀하다.


참조: https://www.baeldung.com/java-generating-time-based-uuids,
https://ssdragon.tistory.com/162
