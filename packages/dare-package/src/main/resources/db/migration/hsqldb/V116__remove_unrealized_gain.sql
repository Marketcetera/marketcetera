alter table if exists metc_pnl_positions drop column unrealized_gain cascade;
alter table if exists metc_profit_and_loss drop column unrealized_gain cascade;
alter table if exists metc_pnl_current_positions drop column unrealized_gain cascade;
