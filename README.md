#뿌리기 API

## 개발 환경

* Java 11
* Spring Boot 2.3.5
* gradle
* h2
* redis
* swagger 확인
	- http://localhost:8080/swagger-ui.html

## 개발

* 예외처리
  1. Advice 를 활용한 Response body 처리
  2. 로직 예외처리를 위해 api 에서 사용할 Exception 설계
* Sping mvc 에 기초하여 controller, service, repository 분리
* JPA
  1. 뿌리기와 받을 것, 사용자와 채팅방에 대하여 일대다 양방향
* Lock
  1. 멀티 인스턴스 서버 환경에서의 운영을 염두에 두고 애플리케이션의 lock만으로는 한계가 있을 것이라 판단
  2. 받기 에 대하여 Lock 의 필요성을 감지
  3. distributed lock 에 대해 알게되어 구현
* Test
  1. 단순히 getter, setter 에 대한 검증은 제외
  2. 예외 케이스들을 나열하여 절차지향적으로 검증하는 방식을 취함
  3. mock 에 대하여 void 와 같은 mock 함수들에 대해서는 수행 횟수를 검증

# 참고
  1. 사전에 데이터 등록에 대하여 준비를 하지 못하여 api batch 방식을 사용, /prepare api 를 마련
  > test 에 필요한채팅방 (방 id, name : 1, 2, 3), 사용자 (사용자 id : 1, 2, 3, 4, 5, 6, 7, 8, 9)

    채팅방 1 : 사용자 1, 2, 3
    채팅방 2 : 사용자 4, 5, 6
    채팅방 3 : 사용자 7, 8, 9
    
