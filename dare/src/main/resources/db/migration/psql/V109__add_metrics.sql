CREATE TABLE metrics (
    id bigint NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    update_count integer NOT NULL,
    count bigint,
    duration_unit character varying(255),
    hour integer NOT NULL,
    m1 numeric(19,2),
    m15 numeric(19,2),
    m5 numeric(19,2),
    max numeric(19,2),
    mean numeric(19,2),
    mean_rate numeric(19,2),
    median numeric(19,2),
    millis integer NOT NULL,
    min numeric(19,2),
    minute integer NOT NULL,
    name character varying(255) NOT NULL,
    p75 numeric(19,2),
    p95 numeric(19,2),
    p98 numeric(19,2),
    p99 numeric(19,2),
    p999 numeric(19,2),
    rate_unit character varying(255),
    second integer NOT NULL,
    std_dev numeric(19,2),
    metric_timestamp timestamp without time zone NOT NULL,
    type character varying(255) NOT NULL,
    value character varying(255)
);

ALTER TABLE ONLY metrics
    ADD CONSTRAINT metrics_pkey PRIMARY KEY (id);

create index if not exists metrics_name on metrics(name);
