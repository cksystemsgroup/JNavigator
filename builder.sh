#!/bin/sh -x

mvn clean install -Dmaven.test.skip

SCP=scp; SSH=ssh; HOST="ckrainer@ckgroup-2";
if [ "$(hostname)" = "ckgroup-2" ]; then SCP=cp; SSH="sh -e"; HOST=""; fi

scp	javiator-controlterminal/target/javiator-controlterminal-1.1-SNAPSHOT-linux.tar.gz	\
	javiator-mockjaviator/target/javiator-mockjaviator-1.1-SNAPSHOT.tar.gz	\
	jnavigator-jcontrol/target/jnavigator-jcontrol-1.1-SNAPSHOT-linux.tar.gz	\
	jnavigator-lab/target/jnavigator-lab-1.1-SNAPSHOT.tar.gz \
	jnavigator-ui/target/jnavigator-ui-1.1-SNAPSHOT.tar.gz	\
	ckrainer@ckgroup-2:~/dist-new

$SSH $HOST "cd ~/dist-new && ./inst.sh"
