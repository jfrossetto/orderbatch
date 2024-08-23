FROM amazoncorretto:21-alpine

RUN mkdir /apps

WORKDIR /apps

ARG JAR_FILE=./build/libs/*.jar
COPY ${JAR_FILE} ./orderbatch.jar
COPY ./build/resources/main/application.properties ./application.properties
COPY ./build/resources/main/log4j2.xml ./log4j2.xml

CMD java -DapplicationProperties=application.properties \
         -DappLogDir=logs \
         -Dlog4j.configurationFile=log4j2.xml \
         -Dfile.encoding=UTF-8 \
         -DinstanceName=orderbatch \
         -Xms128m \
         -Xmx256m \
         -XX:+UseG1GC \
    -jar orderbatch.jar

