begin;
--
select 'trim start',clock_timestamp();
select now() as archive_start;
drop table if exists tmp_archive;
create table tmp_archive (archive timestamp without time zone);
insert into tmp_archive values (now()-interval '1 day');
select * from tmp_archive;

-- find orphaned data in reports/exec_reports/order_status
select 'reports/exec_reports/order_status start',clock_timestamp();
select count(*) from exec_reports;
select count(*) from reports;
select count(*) from order_status;

drop table if exists tmp_reports_id;
select id into tmp_reports_id from reports where transact_time < (select archive from tmp_archive);
select count(*) from tmp_reports_id;
delete from exec_reports where report_id in (select id from tmp_reports_id);
delete from order_status where report_id in (select id from tmp_reports_id);
delete from reports where id in (select id from tmp_reports_id);

select count(*) from exec_reports;
select count(*) from reports;
select count(*) from order_status;
select 'reports/exec_reports/order_status end',clock_timestamp();

-- find orphaned data in outgoing_messages
select 'outgoing_messages start',clock_timestamp();
select count(*) from outgoing_messages;
delete from outgoing_messages where last_updated < (select archive from tmp_archive);
select count(*) from outgoing_messages;
select 'outgoing_messages end',clock_timestamp();

-- exchange_trade_details
select 'exchange_trade start',clock_timestamp();
select count(*) from exchange_trade_details;
delete from exchange_trade_details where exec_time < (select archive from tmp_archive);
select count(*) from exchange_trade_details;
select 'exchange_trade end',clock_timestamp();

-- exchange_closed_orders
select 'exchange_closed_orders start',clock_timestamp();
select count(*) from exchange_closed_orders;
delete from exchange_closed_orders where timestamp < (select archive from tmp_archive);
select count(*) from exchange_closed_orders;
select 'exchange_closed_orders end',clock_timestamp();

-- fix_messages
select 'fix_messages start',clock_timestamp();
select count(*) from fix_messages;
delete from fix_messages where last_updated < (select archive from tmp_archive);
select count(*) from fix_messages;
select 'fix_messages end',clock_timestamp();

select 'trim end',clock_timestamp();
drop table if exists tmp_fix_messages;
--
commit;
