REMOTE = ged-test
BUNDLES = /home/nuxeo/nuxeo/nxserver/bundles 
LOCAL = ./target/*.jar
NXUSER = nuxeo
STOP = '~/nuxeo/bin/nuxeoctl stop'
START = '~/nuxeo/bin/nuxeoctl start'

deploy:
	mvn clean install -DskipTests && \
	scp ${LOCAL} ${NXUSER}@${REMOTE}:${BUNDLES} 
	ssh -l nuxeo ${REMOTE} 'source /etc/profile; /bin/bash /home/nuxeo/nuxeo/bin/nuxeoctl restart'

