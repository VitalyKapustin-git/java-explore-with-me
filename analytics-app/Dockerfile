FROM amazoncorretto:11
#COPY target/analytics.jar analytics.jar
COPY target/analytics-exec.jar analytics.jar
ENTRYPOINT ["java","-jar","/analytics.jar"]