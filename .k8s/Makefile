apply-kafka:
	kubectl apply -f 00-namespace.yml && \
	kubectl apply -f kafka/ && \
	kubectl apply -f zookeeper/ && \
	kubectl apply -f yahoo-kafka-manager/

delete-kafka:
	kubectl apply -f yahoo-kafka-manager/ && \
	kubectl delete -f zookeeper/ && \
	kubectl delete -f kafka/ && \
	kubectl delete -f 00-namespace.yml

apply-backend:
	kubectl apply -f 01-namespace.yml && \
	kubectl create secret generic spring-datasource \
	--from-env-file backend/secret/spring-datasource.txt \
	--namespace=codexia-bot --dry-run -o yaml | kubectl apply -f - --namespace=codexia-bot
	kubectl create secret generic app \
	--from-env-file backend/secret/app.txt \
	--namespace=codexia-bot --dry-run -o yaml | kubectl apply -f - --namespace=codexia-bot && \
	kubectl apply -f backend/

delete-backend:
	kubectl delete -f backend/ && \
	kubectl delete secret spring-datasource --namespace=codexia-bot && \
	kubectl delete secret app --namespace=codexia-bot && \
	kubectl delete -f 01-namespace.yml
