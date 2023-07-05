#!/bin/bash
#

#CONFIG="-Djgroups.bind_addr=172.16.2.222 -Djava.net.preferIPv4Stack=true"
CONFIG="-Djava.net.preferIPv4Stack=true"

LIBS=./jgroups-3.6.4.Final.jar:./
export CLASSPATH=$CLASSPATH:$LIBS

#javac -cp $LIBS *.java

java $CONFIG -cp $LIBS Tbank
