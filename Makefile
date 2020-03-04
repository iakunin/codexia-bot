tag:
	bash bin/create_tag.sh -n1

build-jar:
	bash bin/gradle_in_docker.sh clean -Pversion=$(VERSION) build

deploy: build-jar
	docker build -t eu.gcr.io/codexia-bot/backend:$(VERSION) . && \
	docker push eu.gcr.io/codexia-bot/backend:$(VERSION) && \
	kubectl set image deployment/backend-deployment \
	backend=eu.gcr.io/codexia-bot/backend:$(VERSION) \
	--namespace=codexia-bot

VERSION := $(shell git tag -l --sort=v:refname | tail -n 1)
