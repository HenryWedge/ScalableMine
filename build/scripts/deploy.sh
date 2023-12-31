read -p "Do you really want to deploy a new execution? Old execution will be deleted. Continue? (Y/N): " confirm && [[ $confirm == [yY] || $confirm == [yY][eE][sS] ]] || exit 1
kubectl delete -f ../theodolite/
kubectl apply -f ../theodolite/benchmark.yaml
kubectl apply -f ../theodolite/configmap.yaml
kubectl apply -f ../theodolite/execution.yaml
echo "Execution deployed. Listing all executions:"
kubectl logs -l app=theodolite -c theodolite -f
if [ -z $1 ]; then
  sleep 10
  echo "Sink Log:"
  kubectl logs -l app=sink -f
else
  kubectl logs -l app=theodolite -c theodolite -f
fi
