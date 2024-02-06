#!/bin/bash

sed -i 's/<max-threads count="[0-9]*"/<max-threads count="100"/g' $JBOSS_HOME/standalone/configuration/standalone.xml
sed -i 's/<keepalive-time time="[0-9]*"/<keepalive-time time="100"/g' $JBOSS_HOME/standalone/configuration/standalone.xml

