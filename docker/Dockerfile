FROM postgres:16

RUN apt-get update && apt-get -y install postgresql-16-cron

COPY my_init.sh /docker-entrypoint-initdb.d/my_init.sh


