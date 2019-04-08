FROM openjdk:8-slim

# Oooo KWAK!
MAINTAINER "MrWakeCZ"

# Add basic files
COPY ./build/libs/Sussi.jar /srv/Sussi.jar

WORKDIR /srv

# Basic run
ENTRYPOINT ["java", "-jar", "Sussi.jar"]