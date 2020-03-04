# Running kContainer Scenarios

## Using the CLI

Start a pod with the CLI tools:

```shell
kubectl run cli --generator=run-pod/v1 --image=eventingapps/cli -- block
```

## Using the CLI to create refeer containers

Before you can create orders, you must create containers with the following command:

```shell
kubectl exec -it cli -- containers create 10
```
You can create more containers changing the number after `create`

You may check the available options for orders with:

```shell
kubectl exec -it cli -- containers
```

## Using the CLI to create orders

Create orders with the following command:

```shell
kubectl exec -it cli -- orders create 1
```

You can create more orders changing the number after `create`

You may check the available options for orders with:

```shell
kubectl exec -it cli -- orders
```

## Accessing the kContainer UI

If running in OpenShift 4 or above, you may expose the UI using an OpenShift route with the command:

```shell
oc expose svc/kc-ui-service
```

you can then get the URL to access the UI from:

```shell
oc get route kc-ui-service
```

then open that URL on your browser and login with user `eddie@email.com` and password `passw0rd`.

