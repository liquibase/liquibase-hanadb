# helper script for local docker hana testing


docker-compose up
sleep 10
while true; do 
  STARTING_CONTAINERS=`docker ps --filter "name=HXETravisCI" --format "{{.Names}} {{.Status}}" | grep 'health: starting' | wc -l`;
  echo "Waiting for $STARTING_CONTAINERS HANA container(s) to finish startup";
  if [ $STARTING_CONTAINERS -ne 1 ]; then
	break; 
  fi;
  sleep 5; 
done
docker ps -a
docker exec HXETravisCI bash -l -c "hdbsql -u SYSTEM -p L1qu1base_test -i 90 -d HXE 'CREATE USER LIQUIBASE_TEST PASSWORD L1qu1base_test NO FORCE_FIRST_PASSWORD_CHANGE'"
  

