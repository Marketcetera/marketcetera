alter table reports add column transact_time timestamp without time zone;
update reports set transact_time=send_time;
alter table reports alter column transact_time SET NOT NULL;

alter table reports add column text character varying(255);

alter table exec_reports add column broker_order_id character varying(255);
update exec_reports set broker_order_id='unknown';
alter table exec_reports alter column broker_order_id SET NOT NULL;
