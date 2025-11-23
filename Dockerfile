# 구체적인 버전을 아래와 같이 반드시 명시를
FROM openjdk:17.0.1-jdk-slim

RUN apt-get -y update
RUN apt -y install wget
RUN apt -y install unzip
RUN apt -y install curl


# google chrome 설치

RUN wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb
RUN apt-get -y update
RUN apt -y install ./google-chrome-stable_current_amd64.deb

# 작업 디렉토리 설정
WORKDIR /croller

# JAR 파일 복사
COPY crollerV1.jar .

# 애플리케이션 실행
CMD ["java", "-jar", "/croller/crollerV1.jar"]
