# kt-hhplus
## 항해 플러스 3기 프로젝트 기록소

+ 의문점들
  + 적절한 락의 소유시간 및 재시도 정책
  + 재시도 로직 필요할지? 또한 보통 테스트할 때 프로덕션 코드에서 명시적 예외 발생 후 테스트 하던데 좋은 방법이 아닌 것 같다. 좋은 방법이 있는지?
  + 락 획득 로직도 테스트 코드를 짜야할까? 짠다면 순서 보장이 되면서 동시성 테스트가 가능한가?