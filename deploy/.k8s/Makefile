include ../../.env
export $(shell sed 's/=.*//' ../../.env)

install-backend:
	kubectl apply -f 01-namespace.yml && \
	kubectl create secret generic spring-datasource \
	--from-env-file backend/secret/spring-datasource.txt \
	--namespace=codexia-bot --dry-run=client -o yaml \
	| kubectl apply -f - --namespace=codexia-bot
	kubectl create secret generic app \
	--from-env-file backend/secret/app.txt \
	--namespace=codexia-bot --dry-run=client -o yaml \
	| kubectl apply -f - --namespace=codexia-bot && \
	kubectl apply -f backend/

delete-backend:
	kubectl delete -f backend/ && \
	kubectl delete secret spring-datasource --namespace=codexia-bot && \
	kubectl delete secret app --namespace=codexia-bot && \
	kubectl delete -f 01-namespace.yml

install-datadog:
	helm install datadog -f datadog/values.yaml \
	--set datadog.apiKey=$(DATADOG_API_KEY) datadog/datadog

upgrade-datadog:
	helm upgrade datadog -f datadog/values.yaml \
	--set datadog.apiKey=$(DATADOG_API_KEY) datadog/datadog

delete-datadog:
	helm uninstall datadog
