apply-all:
	kubectl apply -f 00-namespace.yml && \
	kubectl apply -f kafka/ && \
	kubectl apply -f zookeeper/


delete-all:
	kubectl delete -f zookeeper/ && \
	kubectl delete -f kafka/ && \
	kubectl delete -f 00-namespace.yml
