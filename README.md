# kt-hhplus
## 항해 플러스 3기 프로젝트 기록소
+ 할 일들 List up
  + 트랜잭셔널 어노테이션 및 bdd junit을 활용한 테스트 추가
  + 결제 시 중복 결제를 방지하려면 어떻게 해야할까? 낙관적 락?
  + 중복 토큰 생성 방지를 어떻게 처리할 것인가?(만약 사용자가 새로고침 등을 통해 토큰 생성 요청을 다시 했을 경우 초기화하고 재생성된 토큰을 주는 게 맞는것 같은데... 그럼 몇초동안만 토큰 데이터를 잡고 있어서 일정시간동안 중복생성만 방지한다?)
  + 만약 JWT로 구현할 경우, 적절한 토큰 만료 시간은 이벤트 기간일까요?
  + 레디스의 락 획득 시도 같은 경우는 재시도로직을 걸어놓았는데, db에서도 재시도 로직은 필요할까요? 필요하다면 락 획득을 해야하는 곳만?
  + save() 메소드를 선언하여 jpa 내에서 업데이트를 구현하는게 좋나 아니면 @Query로 직접 update문을 작성하는게 좋을까요?
  + 처리율 제한 위해 RateLimiter? 어떻게 구현할 것인가?
  + 예약 이력도 쌓을 것인가?
  + db 적재 실패나 redis 적재 실패 시 로그 남기기 등 로그 필요한 곳에 로깅하기


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

+ 알게 된 점
  + given(ticketRepository.getLockAndReserveMap().mapCache.contains(any(ConcertInfo::class.java)))
    .willReturn(true) -> 이렇게 junt + bdd로 테스트 할 경우 mapCache가 null을 반환해 테스트가 안되지만,
  + every { ticketRepository.getLockAndReserveMap().mapCache.contains(any()) } returns false -> mockk로 테스트 할 경우는 된다

참조: https://www.baeldung.com/java-generating-time-based-uuids,
https://ssdragon.tistory.com/162