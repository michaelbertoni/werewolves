# build stage build the jar with all our resources
FROM adoptopenjdk:15-hotspot as build

WORKDIR /
ADD . .

RUN chmod +x /mvnw && ./mvnw -B package
RUN mv /target/*.jar /app.jar

# package stage
FROM adoptopenjdk:15-jre-hotspot

WORKDIR /
# copy only the built jar and nothing else
COPY --from=build /app.jar /

ENV JAVA_OPTS=-Dspring.profiles.active=production

EXPOSE 8080

ENTRYPOINT ["sh","-c","java -jar -Dspring.profiles.active=production /app.jar"]