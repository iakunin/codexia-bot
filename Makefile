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

upload-remote-db-to-local:
	PGPASSWORD="$(REMOTE_DB_PASSWORD)" pg_dump \
	--username=codexia-bot \
	--host=127.0.0.1 \
	--port=54323  \
	--dbname=codexia-bot \
	--schema=public \
	--data-only  \
	--table=codexia_project \
	--table=codexia_review \
	--table=codexia_review_notification \
	--table=github_repo \
	--table=github_repo_source \
	--table=github_repo_stat \
	| PGPASSWORD="codexia-bot" psql -h 127.0.0.1 -U codexia-bot -p 54322 codexia-bot

pdd:
	docker run \
	--tty \
	--interactive \
	--workdir=/main \
	--volume=${PWD}:/main \
	--rm iakunin/pdd:0.20.5 \
	pdd --source=/main --exclude=.idea/**/* --verbose --file=/dev/null
