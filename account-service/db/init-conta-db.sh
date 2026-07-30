#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname postgres -c "CREATE DATABASE conta;"
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname conta -f /docker-entrypoint-initdb.d/conta/schema.sql
