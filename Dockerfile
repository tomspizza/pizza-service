#FROM is the base image for which we will run our application
FROM java:8

# Copy files and directories from the application
COPY target/pizza-service*.jar /opt/

#set workdir
WORKDIR /opt 
 
#Tell Docker what command to start
CMD touch nohup.out; nohup java -jar pizza-service-*.jar & tail -f nohup.out

# Tell Docker we are going to use this port
EXPOSE 8081
