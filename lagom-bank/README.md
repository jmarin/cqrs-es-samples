# lagom-bank

A simple bank account with the following requirements: 

- Users can open a bank account with starting non-negative balance
- Bank does not allow overdraft (negative balance)
- Transfers between accounts incur a flat transaction fee of $0.25
- Minimum amount of money to transfer is $5 (i.e. $5.25 with fee)
- Bank wants to know, "in real time", how much money it has made out of transaction fees. 

## Running locally

```shell
sbt runAll
```

The following endpoints are available:

* Create Account

`curl -XPOST -d '{"accountId": "123", "initialBalance": 0}' http://localhost:9000/accounts`

* Get Accounts

`curl -XGET http://localhost:9000/accounts`

* Make a deposit to account

`curl -XPUT -d '{"amount": 50}' http://localhost:9000/accounts/123/deposit`

* Withdraw from account

`curl -XPUT -d '{"amount": 50}' http://localhost:9000/accounts/123/withdraw`

* Transfer funds from account

`curl -XPUT -d '{"to": "456", "amount": 5}' http://localhost:9000/accounts/123/transfer`

* Get Account details

`curl -XGET http://localhost:9000/accounts/123`

This will start the Lagom service in development mode. This includes embedded `Cassandra` and `Kafka` instances. It uses `H2` as an embedded relational database for the read side. 

## Running in Production Mode (Minikube)

In production mode, this service requires `Cassandra`, `Kafka` and `Postgres`. These can all be deployed to Minikube by following the instructions below. 

### Postgres

Deploy `Postgres` to `minikube` as follows: 

```shell
kubectl apply -f k8s/postgres/postgres-pv.yaml
kubectl apply -f k8s/postgres/postgres-pvc.yaml
kubectl apply -f k8s/postgres/postgres-secrets.yaml
kubectl apply -f k8s/postgres/postgres-deployment.yaml
kubectl apply -f k8s/postgres/postgres-service.yaml
```

Add the `Lagom` schema as follows:

- First, forward port from running `postgres` pod

```shell
kubectl port-forward postgres-deployment-777779fd7b-nqkc7 5432:5432 (change pod name as needed)
```

Connect to `postgres` in your localhost and create the Lagom schema for the read side:

```shell
psql -h localhost -U postgres
```

```sql
CREATE TABLE read_side_offsets (
  read_side_id VARCHAR(255), tag VARCHAR(255),
  sequence_offset bigint, time_uuid_offset char(36),
  PRIMARY KEY (read_side_id, tag)
);
```

### Cassandra

Deploy `Cassandra` cluster to `minikube` as follows: 

`kubectl apply -f k8s/cassandra/cassandra-statefulset.yaml`

`kubectl apply -f k8s/cassandra/cassandra-service.yaml`

### Kafka

You can deploy a `Kafka` cluster to Minikube by using the Strimzi operator. Follow the instructions [here](https://strimzi.io/quickstarts/minikube/)

### Deploying the application to Kubernetes

First, add the necessary permissions for the Lagom service to form a cluster:

`kubectl apply -f k8s/lagom-bank/lagom-bank-rbac.yaml`

For the application to run, we will need to create a secret for Play (it's a good idea to configure this, even though this example doesn't use it directly):

`kubectl create secret generic lagom-bank-service-secret --from-literal=secret="$(openssl rand -base64 48)"`

Build the application and create the docker image:

* Configure your docker environment to use Minikube: `eval $(minikube docker-env)`
* Build the project, and publish the docker image locally: `sbt docker:publishLocal` 

And deploy the `lagom-bank` application:

`kubectl apply -f k8s/lagom-bank/lagom-bank-deployment.yaml`

`kubectl apply -f k8s/lagom-bank/lagom-bank-service.yaml`

