include .env
export $(shell sed 's/=.*//' .env)

.PHONY: bin build deploy gradle src var

VERSION := $(shell git tag -l --sort=v:refname | tail -n 1)

build:
	bash bin/gradle_in_docker.sh clean build --info --console=verbose

test:
	bash bin/gradle_in_docker.sh clean test --info

tag:
	git fetch --tags && \
	docker run \
	--tty \
	--interactive \
	--rm \
	--volume="${PWD}":/home \
	--workdir=/home \
	iakunin/git-version-manager:0.0.6 && \
	git push --tags

build-jar:
	bash bin/gradle_in_docker.sh clean -Pversion=$(VERSION) build

sentry-create-release:
	sentry-cli releases new -p $(SENTRY_PROJECT) $(VERSION) && \
	sentry-cli releases set-commits $(VERSION) --auto

sentry-finalize-release:
	sentry-cli releases finalize $(VERSION)

docker-build-and-push-image:
	docker build -t eu.gcr.io/codexia-bot/backend:$(VERSION) -f deploy/Dockerfile-app . && \
	docker push eu.gcr.io/codexia-bot/backend:$(VERSION)

k8s-set-image:
	kubectl set image deployment/backend-deployment \
	backend=eu.gcr.io/codexia-bot/backend:$(VERSION) \
	--namespace=codexia-bot

sentry-deploy-release:
	sentry-cli releases deploys $(VERSION) new -e Production

deploy: sentry-create-release build-jar sentry-finalize-release docker-build-and-push-image k8s-set-image sentry-deploy-release

upload-remote-db-to-local:
	PGPASSWORD="$(REMOTE_DB_PASSWORD)" pg_dump \
	--username=codexia-bot \
	--host=$(REMOTE_DB_HOST) \
	--port=5432  \
	--dbname=codexia-bot \
	--schema=public \
	--data-only  \
	--table=codexia_project \
	--table=codexia_review \
	--table=codexia_review_notification \
	--table=github_repo \
	--table=github_repo_source \
	--table=github_repo_stat \
	| PGPASSWORD="codexia-bot" psql --host=127.0.0.1 --username=codexia-bot --port=54322 codexia-bot

pdd:
	docker run \
	--tty \
	--interactive \
	--workdir=/main \
	--volume=${PWD}:/main \
	--rm iakunin/pdd:0.20.5 \
	pdd

migrate:
	bash bin/migrate.sh

prod-db:
	cloud_sql_proxy -instances=codexia-bot:europe-west3:codexia-bot-db=tcp:54323

show-slow-logs:
	aws lightsail get-relational-database-log-events \
	--relational-database-name Codexia-bot-DB \
	--log-stream-name postgresql \
	--start-time 1590240000 | grep "duration:"

update-pg-parameter:
	aws lightsail update-relational-database-parameters \
	--relational-database-name Codexia-bot-DB \
	--parameters "parameterName=log_min_duration_statement,parameterValue=-1,applyMethod=pending-reboot"

codeclimate:
	bash bin/codeclimate.sh
