#!/bin/bash
java -jar schemaSpy_5.0.0.jar -dp ./postgresql-9.1-901.jdbc4.jar -host localhost -port 5432 -t pgsql -db ananya -s report -u postgres -p password -o schemaspy-output

