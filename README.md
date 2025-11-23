# 커뮤니티, 뉴스 웹 크롤러 개발

## 1. 목적
: 사용자들에게 다양한 즐길거리( 커뮤니티, 뉴스 )를 제공해주기 위한 목적.

## 2. 환경

- GCP( Google Cloud Platform ).

- 서울 리전.

- PC : N1 ( CPU : 1 vCPU, 메모리 : 3.75GB ).

  선정 이유 : 기존 AWS t3.medium을 쓸 경우, 1시간당 70원 나옴. 그러나 추후 더 많은 데이터를 가져올 수 있음. ( 이는 더 많은 가동 시간을 의미하므로, 비용 증가 우려. )
  따라서, GCP의 저가형 인스턴스에 크롤러를 직접 관리하기로 결정함.

## 3. 아키텍처

<img width="940" height="529" alt="image" src="https://github.com/user-attachments/assets/3e883bee-70b3-4044-bcf6-80ddd72147a3" />

: GCP에서 포트포워딩을 통해서 AWS의 RDS로 주입.

### [ 방법 ]

1. VM 시작 시, " aws ec2-instance-connect open-tunnel ... & " 명령어를 통해서 로컬의 3306과 aws의 3389를 백그라운드에서 포트 연결을 함. -> 쉘 스크립트로 (재)시작 시, 항상 실행되도록 수정 계획.
2. docker run --network=host ${images}를 통해서 호스트 네트워크를 사용. 참고로 jdbc url의 포트가 3306이기 때문에, AWS의 RDS와 연결이 가능하다.
3. 크롤링 시, 매번 데이터 주입 가능.

## 4. Trouble Shooting

1. java.concurrent.TimeoutException : 네트워크 연결 오류.
  : 해외에서 서울 리전으로 변경.

2. NoSuchElementException : element를 못찾는 이유.
  : 동일한 IP로 잦은 크롤링에 의해서, Block 페이지가 발생한 경우에 나타나는 현상. 따라서, Sentry.io로 pageSource를 통째로 보내서 확인함.

4. 크롤링 작업 중에 발생한 Error에 대해서, throw 대신 -> log와 Sentry.io로 에러 전송.
  : throw를 던질 경우, 나머지 정상적인 데이터를 못가져오고 중지됨. 따라서, 최대한 많은 정보를 가져와야 함.

## 5-1. 최적화 작업 1 - Throttling.
  : 1) "2vCPU -> 1vCPU" + 2) "4 GB -> 3.75 GB"로 다운그레이드 된 상황에서도 크롤러를 작동시켜야 함.
  따라서 네트워크 트래픽 몰림 및 스케줄러의 CPU 코어 독점 방지 목적으로, 매번 크롤링 작업 끝나면 Thread.sleep을 적용.

  효과
  : 의도적 성능 저하로 크롤링에 사용되는 시간은 늘어났지만, 다운그레이드된 환경에서도 크롤러가 안정적으로 작동함.

## 5-2. 최적화 작업 2 - 작업 큐 7개 -> 1개로 감소.
  : 기존 7개의 커뮤니티 각각에 대해서 @Scheduled를 등록함. 따라서, 7개의 @Scheduled 작업을 1vCPU로 처리하기에 상당히 오버헤드가 크다고 판단함.
  따라서, 1개의 @Scheduled 작업에 7개의 커뮤니티를 반복문으로 가져오도록 하되, 1개의 작업을 마친 후 Thread.sleep을 적용. 

### [ Sample Code ]

<img width="566" height="442" alt="스크린샷 2025-11-20 132450" src="https://github.com/user-attachments/assets/99c61705-f4a8-4109-b51a-1e9e801f363c" />

<img width="699" height="658" alt="스크린샷 2025-11-20 153806" src="https://github.com/user-attachments/assets/9c86e8bc-790e-48ac-8c20-e680fabe9ea2" />



