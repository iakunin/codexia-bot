kafka-get-topic-offset:
	docker exec -ti codexia-bot-kafka /opt/kafka/bin/kafka-run-class.sh kafka.tools.GetOffsetShell --broker-list localhost:9092 --topic demo-topic --time -1 --offsets 1

kafka-create-topic:
	docker exec -ti codexia-bot-kafka /opt/kafka/bin/kafka-topics.sh --create --topic demo-topic --replication-factor 1 --partitions 1 --zookeeper zookeeper:2181

kafka-delete-topic:
	docker exec -ti codexia-bot-kafka /opt/kafka/bin/kafka-topics.sh --delete --topic demo-topic --zookeeper zookeeper:2181

kafka-start-consumer:
	docker exec -ti codexia-bot-kafka /opt/kafka/bin/kafka-console-consumer.sh --topic demo-topic --bootstrap-server localhost:9092

build-jar:
	bash bin/gradle_in_docker.sh clean build

# TODO: there should be one place, where version is stored (now it's here and in build.gradle)
VERSION='0.0.5'
deploy: build-jar
	docker build -t iakunin/testtt:$(VERSION) . && \
	docker push iakunin/testtt:$(VERSION) && \
	kubectl set image deployment/backend-deployment backend=iakunin/testtt:$(VERSION)  --namespace=codexia-bot
