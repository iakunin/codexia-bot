include .env
export $(shell sed 's/=.*//' .env)

tag:
	bash bin/create_tag.sh -n1

build-jar:
	bash bin/gradle_in_docker.sh clean -Pversion=$(VERSION) build

sentry-create-release:
	sentry-cli releases new -p $(SENTRY_PROJECT) $(VERSION) && \
	sentry-cli releases set-commits $(VERSION) --auto

sentry-finalize-release:
	sentry-cli releases finalize $(VERSION)

docker-build-and-push-image:
	docker build -t eu.gcr.io/codexia-bot/backend:$(VERSION) . && \
	docker push eu.gcr.io/codexia-bot/backend:$(VERSION)

k8s-set-image:
	kubectl set image deployment/backend-deployment \
	backend=eu.gcr.io/codexia-bot/backend:$(VERSION) \
	--namespace=codexia-bot

sentry-deploy-release:
	sentry-cli releases deploys $(VERSION) new -e Production

deploy: sentry-create-release build-jar sentry-finalize-release docker-build-and-push-image k8s-set-image sentry-deploy-release

VERSION := $(shell git tag -l --sort=v:refname | tail -n 1)
