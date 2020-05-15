# helper script for local docker hana testing

export HXE_VERSION=2.00.036.00.20190223.1
export PASSWORD_FILE_CONTENT='{"master_password" : "L1qu1base_test"}'
export PASSWORD_FILE_NAME=passwords.json

mkdir HXE
chmod -R 777 HXE
echo $PASSWORD_FILE_CONTENT > $TRAVIS_BUILD_DIR/HXE/$PASSWORD_FILE_NAME
echo $PASSWORD_FILE_CONTENT > HXE/$PASSWORD_FILE_NAME
chmod 777 HXE/passwords.json

docker pull store/saplabs/hanaexpress:$HXE_VERSION

docker run -d -p 39013:39013 -p 39015:39015 -p 39017:39017 -p 39041-39045:39041-39045 -p 1128-1129:1128-1129 -p 59013-59014:59013-59014 -v /home/datical/HXE:/hana/mounts --ulimit nofile=1048576:1048576 --sysctl kernel.shmmax=1073741824 --sysctl net.ipv4.ip_local_port_range='40000 60999' --sysctl kernel.shmmni=524288 --sysctl kernel.shmall=8388608 --name HXETravisCI store/saplabs/hanaexpress:$HXE_VERSION --passwords-url file:///hana/mounts/$PASSWORD_FILE_NAME --agree-to-sap-license

while true; do 
  STARTING_CONTAINERS=`docker ps --filter "name=HXETravisCI" --format "{{.Names}} {{.Status}}" | grep 'health: starting' | wc -l`;
  echo "Waiting for $STARTING_CONTAINERS HANA container(s) to finish startup";
  if [ $STARTING_CONTAINERS -ne 1 ]; then
	break; 
  fi;
  sleep 5; 
done

docker exec HXETravisCI bash -l -c "hdbsql -u SYSTEM -p L1qu1base_test -i 90 -d HXE 'CREATE USER LIQUIBASE_TEST PASSWORD L1qu1base_test NO FORCE_FIRST_PASSWORD_CHANGE'"
