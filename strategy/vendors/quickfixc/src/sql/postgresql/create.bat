dropdb -U %1 -q quickfix
createdb -U %1 quickfix
psql -U %1 -d quickfix -f postgresql.sql
