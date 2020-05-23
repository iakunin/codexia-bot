FROM openjdk:11-jdk-slim

COPY build/libs/codexia-bot-*.jar /app.jar
COPY build/libs/dd-java-agent-*.jar /dd-java-agent.jar

WORKDIR /

ENTRYPOINT ["java", "-javaagent:/dd-java-agent.jar", "-jar", "/app.jar"]
