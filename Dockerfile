#FROM is the base image for which we will run our application
##FROM java:8

# Copy files and directories from the application
##COPY target/pizza_service*.jar /opt/

#set workdir
##WORKDIR /opt 
 
#Tell Docker what command to start
##CMD touch nohup.out; nohup java -jar pizza_service-*.jar & tail -f nohup.out

# Tell Docker we are going to use this port
##EXPOSE 8081


FROM openjdk:8-jre-alpine3.9
COPY target/*.jar /demo.jar
CMD ["java", "-jar", "/demo.jar"]
