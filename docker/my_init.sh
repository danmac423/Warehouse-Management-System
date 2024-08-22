#!/bin/bash

# Sprawdź, czy plik postgresql.conf istnieje, zanim spróbujesz go zmodyfikować
if [ -f /data/postgres/postgresql.conf ]; then
    echo "shared_preload_libraries = 'pg_cron'" >> /data/postgres/postgresql.conf
    echo "cron.database_name = 'postgres'" >> /data/postgres/postgresql.conf
else
    echo "Plik postgresql.conf nie istnieje. Modyfikacja nie została przeprowadzona."
fi
