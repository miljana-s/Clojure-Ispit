FROM openjdk:8-alpine

COPY target/uberjar/healthcenter.jar /healthcenter/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/healthcenter/app.jar"]
