# kt-hhplus
## 항해 플러스 3기 프로젝트 기록소
+ 할 일들 List up
  + 결제 시 중복 결제를 방지하려면 어떻게 해야할까? 낙관적 락 사용할 수 있을까?
  + Factory를 통해 주고 있는 redis 들 repository 와 도메인 들로 리팩토링
  + 트랜잭셔널 어노테이션 및 bdd junit을 활용한 테스트 추가
  + 스프링 시큐리티 JWT 구성
  + 결제 완료 시 대기열 토큰을 만료 시킨다
  + 예약 이력도 쌓을지 고민


+ 주요 고민들
  + 동시성 문제를 위해 어떤 방법을 사용할 것인가?
    + Atomic Value or Synchronized : 인스턴스 확장될 경우 동시성 보장이 안됨
    + db의 낙관적락, 비관적 락
  + 보안을 위해 uuid를 유저를 구분하기 위한 주로 파라미터로 사용하기로 결정
    + 문제점
      + mysql(mairaDB)의 클러스터드 인덱스는 B-트리 구조로 되어 있어 항상 정렬된 상태를 유지하여 무작위 값을 인덱스로 배치하게 되면 데이터를 추가할 때마다 구조를 재배치해야하므로 성능에 영향을 미침.
      + 따라서, 첫번째 컴포넌트로 timestamp 기반 UUID를 생성하여 성능을 최적화 시킬 것임
      + UUID v1, v6, v7이 시간 기반 UUID이나, 128비트로 용량 소모가 큼
    + 해결 방안
      + hibernate tsid를 사용
  + lock 대기시간
    + http keep-alive 시간보다 짧게 가져가자
    + 5초보다 짧게 4초까지 락 대기 시간 설정


참조: https://www.baeldung.com/java-generating-time-based-uuids,
https://ssdragon.tistory.com/162