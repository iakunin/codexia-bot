kafka-get-topic-offset:
	docker exec -ti codexia-bot-kafka /opt/kafka/bin/kafka-run-class.sh kafka.tools.GetOffsetShell --broker-list localhost:9092 --topic demo-topic --time -1 --offsets 1

kafka-create-topic:
	docker exec -ti codexia-bot-kafka /opt/kafka/bin/kafka-topics.sh --create --topic demo-topic --replication-factor 1 --partitions 1 --zookeeper zookeeper:2181

kafka-delete-topic:
	docker exec -ti codexia-bot-kafka /opt/kafka/bin/kafka-topics.sh --delete --topic demo-topic --zookeeper zookeeper:2181

kafka-start-consumer:
	docker exec -ti codexia-bot-kafka /opt/kafka/bin/kafka-console-consumer.sh --topic demo-topic --bootstrap-server localhost:9092

tag:
	bash bin/create_tag.sh

build-jar:
	bash bin/gradle_in_docker.sh clean -Pversion=$(VERSION) build

deploy: build-jar
	docker build -t eu.gcr.io/codexia-bot/backend:$(VERSION) . && \
	docker push eu.gcr.io/codexia-bot/backend:$(VERSION) && \
	kubectl set image deployment/backend-deployment \
	backend=eu.gcr.io/codexia-bot/backend:$(VERSION) \
	--namespace=codexia-bot

VERSION := $(shell git tag -l --sort=v:refname | tail -n 1)
