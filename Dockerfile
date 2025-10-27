# 1. 빌드(Build) 스테이지
# Gradle과 Java 17을 사용하여 코드를 빌드합니다.
FROM gradle:8.5.0-jdk17-alpine AS builder

# 작업 디렉토리 설정
WORKDIR /app

# 소스코드 전체 복사
COPY . .

# gradlew 실행 권한 부여 및 빌드 (테스트 제외)
RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar -x test

# 2. 실행(Runtime) 스테이지
# 실제 서버에서 실행될 가벼운 이미지
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# 빌드 스테이지에서 생성된 JAR 파일만 복사
COPY --from=builder /app/build/libs/*SNAPSHOT.jar app.jar

# 8080 포트 노출
EXPOSE 8080

# 컨테이너 시작 시 실행될 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]