export NO_DOCKER=true

# PROD config
#export DATABASE_USER=postgres
#export DATABASE_PASSWORD="q6yKIc%!0Iyas!7w2Pk4SdyI$*NSTFQeV$1pTW0z"
#export DATABASE_URL_VENDOR=postgresql
#export DATABASE_DRIVER=org.postgresql.Driver
#export DATABASE_VENDOR=psql
#export DATABASE_MIGRATION=true
#export DATABASE_MODE=validate
#export SLACK_URL=T0416JES3/B01DHRJP1DZ/S6hRlmgEAtICpMQCgoZQGxUC

# STAGING config
export DATABASE_USER=metc
export DATABASE_PASSWORD=pw4metc
export DATABASE_URL=jdbc:hsqldb:file:./data/metc-hsqldb-data
export DATABASE_DRIVER=org.hsqldb.jdbcDriver
export DATABASE_VENDOR=hsqldb
export DATABASE_MIGRATION=false
export DATABASE_MODE=create
export SLACK_URL=

export DATADOG_HOST=`hostname`
export DATADOG_SOURCE=metc_prod
export DATADOG_SERVICE=metc

export ALL_LOG_LEVEL=WARN
export METC_LOG_LEVEL=INFO
export METC_LOGBACK_APPENDERS=CONSOLE,FILE

export METC_SIXER_SKIP_CURRENT_POSITION_MIGRATION=true

export PATH=/opt/Marketcetera/jdk1.8.0_261/bin:${PATH}
