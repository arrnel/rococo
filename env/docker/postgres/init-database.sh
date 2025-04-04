#!/bin/bash

set -e
set -u

function create_database() {

	DB_EXISTS=$(psql -U $POSTGRES_USER -d postgres -tAc "SELECT 1 FROM pg_database WHERE datname='$1'" 2>/dev/null)
  if [ $? -eq 0 ] && [ "$DB_EXISTS" = "1" ]; then
    echo "Database [$1] already exists."
    psql -U $POSTGRES_USER -d $1 -c "DROP SCHEMA IF EXISTS public CASCADE;"
  else
    createdb -U $POSTGRES_USER $1
    psql -U $POSTGRES_USER -d $1 -c "DROP SCHEMA IF EXISTS public CASCADE;"
    echo "Database [$1] has been created and dropped schema [public]"
  fi

}

if [ -n "$CREATE_DATABASES" ]; then
	echo "Multiple database creation requested: $CREATE_DATABASES"
	for db in $(echo $CREATE_DATABASES | tr ',' ' '); do
		create_database $db
	done
	echo "Multiple databases created"
fi
