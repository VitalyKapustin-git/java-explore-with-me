FROM amazoncorretto:11
RUN mvn clean package
COPY target/*.jar afisha.jar
ENTRYPOINT ["java","-jar","/afisha.jar"]