#!/bin/sh
set -e

echo "Environment variables:" | tee -a /srv/app-data/entrypoint.log
echo "+ ======================================================= +" | tee -a /srv/app-data/entrypoint.log
printenv | tee -a /srv/app-data/entrypoint.log
echo "+ ======================================================= +" | tee -a /srv/app-data/entrypoint.log

if [ -e "$ENTRYPOINT_BEFORE" ]; then
  echo "The script $ENTRYPOINT_BEFORE is available. Running it now..." | tee -a /srv/app-data/entrypoint.log
  . "$ENTRYPOINT_BEFORE"| tee -a /srv/app-data/entrypoint.log
else
  echo "The script $ENTRYPOINT_BEFORE does not exist." | tee -a /srv/app-data/entrypoint.log
fi

# Read the environment variable and set the desired parameter
if [ "$WILDFLY_FULL" = "true" ]; then
  echo "Starting Wildfly Full" | tee -a /srv/app-data/entrypoint.log
  exec /usr/local/bin/supervisord -c /etc/supervisord-full.conf
else
  echo "Starting Wildfly" | tee -a /srv/app-data/entrypoint.log
  exec /usr/local/bin/supervisord -c /etc/supervisord.conf
fi

if [ -e "$ENTRYPOINT_AFTER" ]; then
  echo "The script $ENTRYPOINT_AFTER is available. Running it now..."| tee -a /srv/app-data/entrypoint.log
  "$ENTRYPOINT_AFTER"| tee -a /srv/app-data/entrypoint.log
else
  echo "The script $ENTRYPOINT_AFTER does not exist." | tee -a /srv/app-data/entrypoint.log
fi