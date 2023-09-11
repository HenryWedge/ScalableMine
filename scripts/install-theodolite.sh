helm install theodolite theodolite/theodolite -f ../k8s/theodolite-config.yaml
sleep 5
kubectl get pods