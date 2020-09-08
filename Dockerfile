# ---- STAGE 1 - build stage ----
FROM adoptopenjdk/openjdk11 as builder
WORKDIR /build
ADD . .
RUN ./mvnw -e clean package
# ---- STAGE 2 - final image stage ----
FROM adoptopenjdk/openjdk11:jre-11.0.8_10
ARG JAR_FILE=target/demo-0.0.1-SNAPSHOT.jar
COPY --from=builder /target/demo-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]