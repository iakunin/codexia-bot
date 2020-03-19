Kafka & zookeeper k8s manifests has been taken from here: 
https://github.com/Yolean/kubernetes-kafka


--- 


### Copy local DB to remote DB:

```
PGPASSWORD="codexia-bot" pg_dump -C -h 127.0.0.1 -p 54322  -U codexia-bot codexia-bot | \
PGPASSWORD="<remote-db-password>" psql -h 127.0.0.1 -U codexia-bot -p 54323 codexia-bot
```
