#!/bin/bash
set -e
set -u

echo "adding uuid-ossp to database ${POSTGRES_DB}"
psql -v ON_ERROR_STOP=1 --username "${POSTGRES_USER}" "${POSTGRES_DB}" <<-EOSQL
	    CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
EOSQL

function create_user_and_database() {
	local database=$1
	echo "  Creating user and database '$database' with uuid-ossp"
	psql -v ON_ERROR_STOP=1 --username "${POSTGRES_USER}" "${POSTGRES_DB}" <<-EOSQL
	    CREATE DATABASE $database;
	    GRANT ALL PRIVILEGES ON DATABASE $database TO $POSTGRES_USER;
	    \c $database;
	    CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
EOSQL
}

if [ -n "$POSTGRES_MULTIPLE_DATABASES" ]; then
	echo "Multiple database creation requested: $POSTGRES_MULTIPLE_DATABASES"
	for db in $(echo "$POSTGRES_MULTIPLE_DATABASES" | tr ',' ' '); do
		create_user_and_database "$db"
	done
	echo "Multiple databases created"
fi
