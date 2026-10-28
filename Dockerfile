
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY . .
RUN chmod +x mvnw && ./mvnw clean package -DskipTests
CMD ["sh", "-c", "java -jar target/email-spam-detection-0.0.1-SNAPSHOT.jar --server.port=${PORT:-8081}"]
