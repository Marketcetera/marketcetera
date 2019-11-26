alter table exec_reports add column leaves_qty numeric(17,7);
update exec_reports set leaves_qty=0;
alter table exec_reports alter column leaves_qty SET NOT NULL;
