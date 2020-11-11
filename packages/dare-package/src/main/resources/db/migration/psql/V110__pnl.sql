alter table if exists pnl_lots drop constraint if exists FKqvsp8f4pxfns0t6jq6uwr9oj1;
alter table if exists pnl_lots drop constraint if exists FKalmf30gxgcxp0kp1rjo5ovta7;
alter table if exists pnl_lots drop constraint if exists FKdxhm1tfhjt02nueaegsjfb6vo;

alter table if exists pnl_positions drop constraint if exists FKmyjvihgiuvaefedwgkkb6g2ia;

alter table if exists profit_and_loss drop constraint if exists FK5pykm73j4uese5s02n77m5j4s;

alter table if exists user_trades drop constraint if exists FKej0h6152whvqet8rhow849u3i;
alter table if exists user_trades drop constraint if exists FKroycpqibf1bjm2jt1msih0ord;
alter table if exists user_trades drop constraint if exists FK56c0xugtl05r0cqq1abdmj19o;

drop table if exists pnl_lots cascade;
drop table if exists pnl_positions cascade;
drop table if exists profit_and_loss cascade;
drop table if exists trades cascade;
drop table if exists user_trades cascade;

create table pnl_lots (id int8 not null, last_updated timestamp not null, update_count int4 not null, allocated_quantity numeric(17, 7) not null, basis_price numeric(17, 7) not null, effective_date timestamp not null, gain numeric(17, 7) not null, quantity numeric(17, 7) not null, trade_price numeric(17, 7) not null, position_id int8 not null, trade_id int8 not null, user_id int8 not null, primary key (id));
create table pnl_positions (id int8 not null, last_updated timestamp not null, update_count int4 not null, effective_date timestamp not null, expiry varchar(255), option_type int4, position numeric(17, 7) not null, realized_gain numeric(17, 7) not null, security_type int4 not null, strike_price numeric(17, 7) not null, symbol varchar(255) not null, unrealized_gain numeric(17, 7) not null, weighted_average_cost numeric(17, 7) not null, user_id int8 not null, primary key (id));
create table profit_and_loss (id int8 not null, last_updated timestamp not null, update_count int4 not null, basis_price numeric(17, 7), expiry varchar(255), option_type int4, position numeric(17, 7), realized_gain numeric(17, 7), security_type int4 not null, strike_price numeric(17, 7) not null, symbol varchar(255) not null, unrealized_gain numeric(17, 7), user_id int8 not null, primary key (id));
create table trades (id int8 not null, last_updated timestamp not null, update_count int4 not null, order_id varchar(255), expiry varchar(255), option_type int4, price numeric(17, 7), quantity numeric(17, 7), security_type int4 not null, strike_price numeric(17, 7) not null, symbol varchar(255) not null, transaction_time timestamp, primary key (id));
create table user_trades (id int8 not null, last_updated timestamp not null, update_count int4 not null, order_id varchar(255), side int4 not null, pnl_id int8 not null, trade_id int8 not null, user_id int8 not null, primary key (id));

alter table if exists pnl_lots add constraint FKqvsp8f4pxfns0t6jq6uwr9oj1 foreign key (position_id) references pnl_positions;
alter table if exists pnl_lots add constraint FKalmf30gxgcxp0kp1rjo5ovta7 foreign key (trade_id) references trades;
alter table if exists pnl_lots add constraint FKdxhm1tfhjt02nueaegsjfb6vo foreign key (user_id) references users;

alter table if exists profit_and_loss add constraint FK5pykm73j4uese5s02n77m5j4s foreign key (user_id) references users;

alter table if exists pnl_positions add constraint FKmyjvihgiuvaefedwgkkb6g2ia foreign key (user_id) references users;

alter table if exists user_trades add constraint UK_8nt6asfm7ke2mr9d2qdxkhmrx unique (pnl_id);
alter table if exists user_trades add constraint FKej0h6152whvqet8rhow849u3i foreign key (pnl_id) references profit_and_loss;
alter table if exists user_trades add constraint FKroycpqibf1bjm2jt1msih0ord foreign key (trade_id) references trades;
alter table if exists user_trades add constraint FK56c0xugtl05r0cqq1abdmj19o foreign key (user_id) references users;
