alter table if exists metc_pnl_positions drop column unrealized_gain cascade;
alter table if exists metc_profit_and_loss drop column unrealized_gain cascade;
alter table if exists metc_pnl_current_positions drop column unrealized_gain cascade;

alter table if exists metc_profit_and_loss rename to metc_pnl_profit_and_loss;
alter table if exists metc_trades rename to metc_pnl_trades;
alter table if exists metc_user_trades rename to metc_pnl_user_trades;

alter table if exists metc_pnl_profit_and_loss add column effective_date timestamp not null;
