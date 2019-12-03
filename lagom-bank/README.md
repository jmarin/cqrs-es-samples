# lagom-bank

A simple bank account with the following requirements: 

- Users can open a bank account with starting non-negative balance
- Bank does not allow overdraft (negative balance)
- Transfers between accounts incur a flat transaction fee of $0.25
- Minimum amount of money to transfer is $5 (i.e. $5.25 with fee)
- Bank wants to know, "in real time", how much money it has made out of transaction fees. 

## Running in Production Mode (Minikube)

### Cassandra

Deploy Cassandra cluster to minikube as follows: 

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

