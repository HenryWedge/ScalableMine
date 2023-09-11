while [[ ! kubectl get executions | grep -q Finished ]]
do
  sleep 10
done
echo "Execution finished"
