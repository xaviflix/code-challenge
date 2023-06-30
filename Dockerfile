FROM eclipse-temurin:17
WORKDIR /app
COPY target/CodeChallenge-0.0.1.jar /app/CodeChallenge.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "CodeChallenge.jar"]