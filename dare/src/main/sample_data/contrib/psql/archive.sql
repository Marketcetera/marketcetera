begin;
--
select now() as start;
--
-- prepare archive point of 1 day ago
--
drop table if exists tmp_archive;
create table tmp_archive (archive timestamp without time zone);
insert into tmp_archive values (now()-interval '1 day');
select * from tmp_archive;
--
-- archive closed orders older than the given archive point
--
drop table if exists tmp_exchange_orders;
select * into tmp_exchange_orders from exchange_orders where order_status in ('Filled','Canceled') and timestamp < (select archive from tmp_archive);
select count(*) as purged_orders from tmp_exchange_orders;
delete from exchange_orders where id in (select id from tmp_exchange_orders);
delete from fix_messages where id in (select current_message_id from tmp_exchange_orders);
--
-- archive trade details
--
select count(*) as purged_trades from exchange_trade_details where buy_order_id in (select order_id from tmp_exchange_orders) or sell_order_id in (select order_id from tmp_exchange_orders);
delete from exchange_trade_details where buy_order_id in (select order_id from tmp_exchange_orders) or sell_order_id in (select order_id from tmp_exchange_orders);
--
-- incoming fix_messages
--
delete from incoming_fix_messages where sending_time < (select archive from tmp_archive);
--
-- cleanup
--
drop table tmp_archive;
drop table tmp_exchange_orders;
--
select now() as end;
--
commit;
