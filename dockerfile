FROM amazoncorretto:17-alpine-jdk
COPY ApplicationModule/target/*.jar ApplicationModule/app.jar
COPY ApplicationModule/audios ApplicationModule/audios
RUN apk update
RUN apk add ffmpeg
ENTRYPOINT ["java","-jar","ApplicationModule/app.jar"]