begin;
--
select now() as archive_start;
--
-- prepare archive point of 1 day ago
--
drop table if exists tmp_archive;
create table tmp_archive (archive timestamp without time zone);
insert into tmp_archive values (now()-interval '1 day');
select * from tmp_archive;
select 'fix messages start' as comment,count(*) from fix_messages;
delete from fix_messages where id in (select current_message_id from tmp_exchange_orders);
select 'fix messages end' as comment,count(*) from fix_messages;
--
-- incoming fix_messages
--
select 'incoming fix messages start',clock_timestamp();
--
select 'purged incoming fix messages' as comment,count(*) from incoming_fix_messages where sending_time < (select archive from tmp_archive);
delete from incoming_fix_messages where sending_time < (select archive from tmp_archive);
select 'incoming fix messages remaining' as comment,count(*) from incoming_fix_messages;
--
select 'incoming fix messages end',clock_timestamp();
--
-- exec_reports
--
select 'exec reports start',clock_timestamp();
--
drop table if exists tmp_reports_id;
select report_id into tmp_reports_id from exec_reports where send_time < (select archive from tmp_archive) and ord_status not in ('New','PendingNew','PendingCancel','PendingReplace','Replaced','PartiallyFilled');
select 'purged exec reports' as comment,count(*) from tmp_reports_id;
delete from exec_reports where report_id in (select report_id from tmp_reports_id);
select 'exec reports remaining' as comment,count(*) from exec_reports;
--
select 'exec reports end',clock_timestamp();
--
-- order_status
--
select 'order status start',clock_timestamp();
--
select 'purged order status records' as comment,count(*) from order_status where report_id in (select report_id from tmp_reports_id);
delete from order_status where report_id in (select report_id from tmp_reports_id);
select 'order status records remaining' as comment,count(*) from order_status;
--
select 'order status end',clock_timestamp();
--
-- reports
--
select 'reports start',clock_timestamp();
--
drop table if exists tmp_fix_messages;
select fix_message_id into tmp_fix_messages from reports where id in (select report_id from tmp_reports_id);
select 'purged report FIX messages' as comment, count(*) from tmp_fix_messages;
delete from reports where id in (select report_id from tmp_reports_id);
select 'remaining FIX messages' as comment, count(*) from fix_messages;
--
select 'reports end',clock_timestamp();
--
-- outgoing messages
--
select 'outgoing messages start',clock_timestamp();
--
drop table if exists tmp_fix_messages2;
--select fix_message_id into tmp_fix_messages2 from outgoing_messages where last_updated < (select archive from tmp_archive);
--delete from outgoing_messages where last_updated < (select archive from tmp_archive);
--delete from fix_messages where id in (select fix_message_id from tmp_fix_messages2);
--
select 'outgoing messages end',clock_timestamp();
--
-- fix_messages
--
select 'fix messages start',clock_timestamp();
--
delete from fix_messages where id in (select fix_message_id from tmp_fix_messages);
--
select 'fix messages end',clock_timestamp();
--
-- cleanup
--
drop table if exists tmp_archive;
drop table if exists tmp_exchange_orders;
drop table if exists tmp_reports_id;
drop table if exists tmp_fix_messages;
drop table if exists tmp_fix_messages2;
--
select clock_timestamp() as archive_end;
--
commit;
--
select clock_timestamp() as va_start;
VACUUM ANALYZE exec_reports;
VACUUM ANALYZE fix_messages;
VACUUM ANALYZE fix_session_attr_dscrptrs;
VACUUM ANALYZE fix_session_attributes;
VACUUM ANALYZE fix_sessions;
VACUUM ANALYZE flyway_schema_history;
VACUUM ANALYZE handled_messages;
VACUUM ANALYZE id_repository;
VACUUM ANALYZE incoming_fix_messages;
VACUUM ANALYZE message_store_messages;
VACUUM ANALYZE message_store_sessions;
VACUUM ANALYZE order_status;
VACUUM ANALYZE outgoing_messages;
VACUUM ANALYZE permissions;
VACUUM ANALYZE reports;
VACUUM ANALYZE roles;
VACUUM ANALYZE roles_permissions;
VACUUM ANALYZE roles_users;
VACUUM ANALYZE supervisor_permissions;
VACUUM ANALYZE supervisor_permissions_permissions;
VACUUM ANALYZE supervisor_permissions_users;
VACUUM ANALYZE user_attributes;
VACUUM ANALYZE users;
select clock_timestamp() as va_end;
--
REINDEX database metc;
--
select clock_timestamp() as reindex_end;
