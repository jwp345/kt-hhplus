# 항해 플러스 3기 프로젝트 기록소
## 콘서트 예약 서비스
### 활용 기술
+ Spring Boot 3.2.0
+ kotlin 1.9.20(jvm 17)
+ JPA
+ MariaDB
+ Redis(redisson)
+ AWS (ECS, ECR, Fargate - CI, CD/ CloudWatch, Lambda - Logging)
+ K6 (부하 테스트 툴)
+ Kotest, Junit5

### ERD
![erd](https://github.com/jwp345/kt-hhplus/assets/35333297/f911977a-9e07-4756-a7aa-7f9d5b4eb245)

### Slack Webhook Url을 이용한 알림 전송
![람다를 이용한 슬랙 error알람 보내기](https://github.com/jwp345/kt-hhplus/assets/35333297/748ab016-d41f-43d7-a8d3-71b33f0208dd)

### 작업 내용
+ DDL 작성
+ API 설계 (Swagger API Docs 제공)
+ 서비스 로직 개발 (유저 토큰 발급 API, 예약 가능 날짜 / 좌석 API, 좌석 예약 요청 API, 잔액 충전 / 조회 API, 결제 API) 
+ 동시성 이슈 / 데이터 일관성 보장을 고려한 서비스 개발
  + Booking 테이블 낙관적 Lock 활용
+ 비동기로 성능 향상
  + 결재 이력적재 하기 위해 커밋된 이후 메시지 발행을 위한 TransactionalEventListener 활용
  + 유효 토큰 만료를 위해 EventListener 활용
+ 커스텀 예외 생성 및 처리와 글로벌 예외 처리 핸들러 생성
+ 요청, 응답 로깅을 위한 Filter 생성 및 커스텀 
+ k6 부하 테스트 후, 성능을 위해 BookingDate 컬럼의 날짜 데이터 타입을 String -> DateTime 형식으로 변경 후 인덱스 생성으로 성능 개선


#### 할 일들 List up
  + 그래들 빌드 시 코틀린 컴파일러가 추가로 돌아가서 메모리 많이 소요되는데 그래들 튜닝 필요
  + redis로 캐싱 개선할 부분이 있을지?
  + 밸리데이션 어노테이션으로 빼기
  + 테스트 코드 redis 초기화 방안 고민하기
  + 추후 도커 이미지 등을 위해 테스트 컨네이너 등을 사용하여 로컬이 아닌 테스트를 위한 환경 구성하기
  + 예약 이력도 추후 적재 할 지 고민
  + 추후 모니터링 추가 추적을 위해 sleuth, zipkin / 스카우터, pinpoint 공부 필요
  + v1:
    + redis를 대기열 구현할 때만 사용하며, 예약정보는 rdb에서 한번에 관리하고 낙관적락을 사용하여 동시성 이슈를 관리하며 따로 캐시는 안함
    + 예약정보는 redis로 관리를 안하는데 그 이유는 예약정보를 관리하려면 분산락을 사용하여야 하는데, 예약 정보를 조회하는데 락 획득 시도를 하는데 드는 비용이 더 들것같아서임
  + v1의 한계 : Redis 혹은 nosql 없이 대기열 구현이 불가 만약 rdb로 구현을 해야한다면? 자료구조에 종속적임 + sortedSet으로 정렬 비용 발생
  + V2: 아직 미구현, 구현 예정 중
    + 자료구조를 안쓰는 방식으로 구현 
    + 방안) 대기 순서가 아닌 대기 시간을 부여한다. 부하테스트로 얼마만큼의 요청이 가용가능한지 일단 알고 들어온 순서대로 사이클을 구현한다
    + 현재 들어온 시간에 미리 정해둔 가중치(현재 사이클 수 * 수용할 수 있는 최대 인원 / 분당 처리량)를 더하여 유저에게 입장 시간을 부여한다.
      + 장점: 유저가 나갈 때 한 유저를 들어오게 안해도 되어 읽고 쓰기 등 부하도 줄임. 그리고 큐를 위해 nosql을 사용할 필요도 없어짐.

#### 주요 고민들
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
      + hibernate tsId를 사용(id와 같이 사용하려 했으나 tsID를 사용할 경우 같이 못쓰는 문제가 있고 의미가 있을까싶어서 tsId만 사용하기로 결정)
  + 기존 lettuce 라이브러리 대신 redisson 사용
    + redisson 을 이용하여 키 만료 이벤트 발신 및 수신을 위해 hash set 구조를 map 구조로 변경하여 사용(이벤트 리스너가 편리함)
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
  

#### 알게 된 점
  + given(ticketRepository.getLockAndReserveMap().mapCache.contains(any(ConcertInfo::class.java)))
    .willReturn(true) -> 이렇게 junt + bdd로 테스트 할 경우 mapCache가 null을 반환해 테스트가 안되지만,
  + every { ticketRepository.getLockAndReserveMap().mapCache.contains(any()) } returns false -> mockk로 테스트 할 경우는 된다
  + 결론 : mockk가 좀 더 파워풀하다.
  + publishEvent()메소드를 사용하여 이벤트를 전송할 때 data class 말고 List<>() 같이 단순 래핑해서 보낼 시 제대로 메시지 리스너가 동작하지 않는다.
  + Redisson 에서 hashset, hashmap 자료구조에서 TTL을 사용할 시 벌어지는 일
    + 내부적으로 ScoredSortedSet이 생성되고 내부 스케줄러가 돌면서 해당 Score(만료 시간)순서대로 차례대로 제거한다.

참조: https://www.baeldung.com/java-generating-time-based-uuids
<br>https://ssdragon.tistory.com/162
<br>https://github.com/redisson/redisson/wiki/7.-distributed-collections
<br>https://stackoverflow.com/questions/4594229/mysql-integer-vs-datetime-index