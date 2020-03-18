# Email Delivery Microservice

A microservice to schedule the reliable delivery of emails

## Running locally

```shell
sbt reStart
```

## Service API

The following endpoints are available:



## Running in production mode (Minikube)

In production mode, this service requires `Cassandra`, `Kafka` and `Postgres`. These can all be deployed to Minikube by following the instructions below. 

### Cassandra

Deploy `Cassandra` cluster to `minikube` as follows: 

```shell
kubectl apply -f k8s/cassandra
```

### Kafka

You can deploy a `Kafka` cluster to Minikube by using the Strimzi operator. Follow the instructions [here](https://strimzi.io/quickstarts/minikube/)

### Deploying the application to Kubernetes

First, add the necessary permissions for the Email Delivery service to form a cluster:

`kubectl apply -f k8s/email-delivery/email-delivery-rbac.yaml`

For the application to run, we will need to create a secret for Play (it's a good idea to configure this, even though this example doesn't use it directly):

`kubectl create secret generic email-delivery-service-secret --from-literal=secret="$(openssl rand -base64 48)"`

Build the application and create the docker image:

* Configure your docker environment to use Minikube: `eval $(minikube docker-env)`
* Build the project, and publish the docker image locally: `sbt docker:publishLocal` 

And deploy the `email-delivery` application:

```shell
kubectl apply -f k8s/email-delivery
```