alter table exec_reports add column order_qty numeric(17,7);
update exec_reports set order_qty=0;
alter table exec_reports alter column order_qty SET NOT NULL;
