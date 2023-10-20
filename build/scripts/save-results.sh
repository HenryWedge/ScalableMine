cd ../results
kubectl cp $(kubectl get pod -l app=theodolite -o jsonpath="{.items[0].metadata.name}"):results . -c results-access
