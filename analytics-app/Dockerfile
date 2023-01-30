FROM amazoncorretto:11
RUN mvn clean package
COPY target/*.jar analytics.jar
ENTRYPOINT ["java","-jar","/analytics.jar"]