# 커뮤니티, 뉴스 웹 크롤러 개발

## 1. 목적
: 사용자들에게 다양한 즐길거리( 커뮤니티, 뉴스 )를 제공해주기 위한 목적.

## 2. 환경

- GCP( Google Cloud Platform )

- PC : N1 ( CPU : 1 vCPU, 메모리 : 3.75GB )

  선정 이유 : 기존 AWS t3.medium을 쓸 경우, 1시간당 70원 나옴. 그러나 추후 더 많은 데이터를 가져올 수 있음. ( 더 많아진 가동 시간에 의해서 비용적 증가 우려. )
  따라서, 직접 GCP의 저가형 인스턴스에 크롤러 관리하기로 결정함.

## 3. 아키텍처

<img width="940" height="529" alt="image" src="https://github.com/user-attachments/assets/3e883bee-70b3-4044-bcf6-80ddd72147a3" />
