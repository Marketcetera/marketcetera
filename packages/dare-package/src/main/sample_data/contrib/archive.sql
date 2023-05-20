begin;
--
select now() as archive_start;
---
--- save ids of open orders
---
drop table if exists tmp_ids;
select clord_id,current_message_id into tmp_ids from exchange_open_orders;
---
--- remove non-open order records
---
delete from exchange_closed_orders;
delete from exchange_trade_details where buy_order_id not in (select clord_id from tmp_ids) and sell_order_id not in (select clord_id from tmp_ids);
delete from exec_reports where order_id not in (select clord_id from tmp_ids);
delete from order_status where order_id not in (select clord_id from tmp_ids);
delete from reports where id not in (select report_id from exec_reports);
delete from outgoing_messages where order_id not in (select clord_id from tmp_ids);
delete from fix_messages where id not in (select current_message_id from tmp_ids) and id not in (select fix_message_id from reports) and id not in (select fix_message_id from outgoing_messages);
--
-- sixer tables
--
--
-- sixer_current_position negative position check
--
select count(*) from sixer_current_positions where position < 0;
--
-- cleanup
--
drop table if exists tmp_ids;
--
select clock_timestamp() as archive_end;
--
commit;
--
select clock_timestamp() as va_start;
VACUUM ANALYZE exchange_closed_orders;
VACUUM ANALYZE exchange_marketdata_content;
VACUUM ANALYZE exchange_marketdata_exchanges;
VACUUM ANALYZE exchange_marketdata_instruments;
VACUUM ANALYZE exchange_marketdata_requests;
VACUUM ANALYZE exchange_open_orders;
VACUUM ANALYZE exchange_orderbook_stats;
VACUUM ANALYZE exchange_orderbooks;
VACUUM ANALYZE exchange_trade_details;
VACUUM ANALYZE exchanges;
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
VACUUM ANALYZE sixer_current_positions;
VACUUM ANALYZE sixer_events;
VACUUM ANALYZE sixer_lots;
VACUUM ANALYZE sixer_managed_orders;
VACUUM ANALYZE sixer_marketdata;
VACUUM ANALYZE sixer_marketdata_elements;
VACUUM ANALYZE sixer_order_status;
VACUUM ANALYZE sixer_placed_orders;
VACUUM ANALYZE sixer_pnl;
VACUUM ANALYZE sixer_positions;
VACUUM ANALYZE sixer_trades;
VACUUM ANALYZE sixer_user_trades;
VACUUM ANALYZE sixer_users;
VACUUM ANALYZE supervisor_permissions;
VACUUM ANALYZE supervisor_permissions_permissions;
VACUUM ANALYZE supervisor_permissions_users;
VACUUM ANALYZE user_attributes;
VACUUM ANALYZE users;
select clock_timestamp() as va_end;
--
reindex table exchange_closed_orders;
reindex table exchange_marketdata_content;
reindex table exchange_marketdata_exchanges;
reindex table exchange_marketdata_instruments;
reindex table exchange_marketdata_requests;
reindex table exchange_open_orders;
reindex table exchange_orderbook_stats;
reindex table exchange_orderbooks;
reindex table exchange_trade_details;
reindex table exchanges;
reindex table exec_reports;
reindex table fix_messages;
reindex table fix_session_attr_dscrptrs;
reindex table fix_session_attributes;
reindex table fix_sessions;
reindex table flyway_schema_history;
reindex table handled_messages;
reindex table id_repository;
reindex table incoming_fix_messages;
reindex table message_store_messages;
reindex table message_store_sessions;
reindex table order_status;
reindex table outgoing_messages;
reindex table permissions;
reindex table reports;
reindex table roles;
reindex table roles_permissions;
reindex table roles_users;
reindex table sixer_allocations;
reindex table sixer_contract_definitions;
reindex table sixer_contract_extensions;
reindex table sixer_contracts;
reindex table sixer_current_positions;
reindex table sixer_events;
reindex table sixer_game_players;
reindex table sixer_games;
reindex table sixer_lots;
reindex table sixer_managed_orders;
reindex table sixer_marketdata;
reindex table sixer_marketdata_elements;
reindex table sixer_order_status;
reindex table sixer_placed_orders;
reindex table sixer_pnl;
reindex table sixer_positions;
reindex table sixer_trades;
reindex table sixer_user_trades;
reindex table sixer_users;
reindex table supervisor_permissions;
reindex table supervisor_permissions_permissions;
reindex table supervisor_permissions_users;
reindex table user_attributes;
reindex table users;
--
select clock_timestamp() as reindex_end;
