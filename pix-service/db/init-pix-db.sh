#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname postgres -c "CREATE DATABASE pix;"
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname pix -f /docker-entrypoint-initdb.d/pix/schema.sql
