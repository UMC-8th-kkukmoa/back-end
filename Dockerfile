# JDK 17을 이미지로 생성
FROM openjdk:17-jdk-slim

# 컨테이너 안에서 작업할 디렉토리
WORKDIR /app

# JAR 복사
COPY build/libs/*SNAPSHOT.jar /app.jar

# 애플리케이션 포트 노출 (ALB/보안그룹과 일치)
EXPOSE 8080

# 스프링 프로파일(배포 시 .env나 CI에서 덮어쓰기)
ENV SPRING_PROFILES_ACTIVE=prod

# 컨테이너 헬스체크
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD curl -fsS http://localhost:8080/health || exit 1

# 컨테이너 시작 시 JAR 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]
