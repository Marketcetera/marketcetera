drop table if exists id_repository;

drop table if exists execreports;

drop table if exists reports;

drop table if exists ors_users;

create table ors_users (
    id bigint not null auto_increment,
    lastUpdated datetime,
    updateCount integer not null,
    description varchar(255),
    name varchar(255) not null,
    active bit not null,
    hashedPassword varchar(255) not null,
    superuser bit not null,
    primary key (id),
    unique (name)
);

create table reports (
    id bigint not null auto_increment,
    lastUpdated datetime,
    updateCount integer not null,
    brokerID varchar(255),
    fixMessage text not null,
    originator integer,
    reportType integer not null,
    sendingTime datetime not null,
    orderID varchar(255) not null,
    viewer_id bigint,
    actor_id bigint,
    primary key (id),
    index idx_sendingTime (sendingTime),
    index idx_orderID (orderID),
    index idx_viewer_id (viewer_id),
    constraint fk_reports_actor_id foreign key (actor_id)
     references ors_users(id),
    constraint fk_reports_viewer_id foreign key (viewer_id)
     references ors_users(id)
);

create table execreports (
    id bigint not null auto_increment,
    lastUpdated datetime,
    updateCount integer not null,
    avgPrice numeric(15,5) not null,
    cumQuantity numeric(15,5) not null,
    lastPrice numeric(15,5),
    lastQuantity numeric(15,5),
    orderID varchar(255) not null,
    viewer_id bigint,
    orderStatus integer not null,
    origOrderID varchar(255),
    rootID varchar(255) not null,
    sendingTime datetime not null,
    side integer not null,
    symbol varchar(255) not null,
    account varchar(255),
    report_id bigint not null,
    primary key (id),
    index idx_report_id (report_id),
    index idx_symbol (symbol),
    index idx_sendingTime (sendingTime),
    index idx_orderID (orderID),
    index idx_viewer_id (viewer_id),
    index idx_rootID (rootID),
    constraint fk_execreports_viewer_id foreign key (viewer_id)
     references ors_users(id),
    constraint fk_execreports_report_id foreign key (report_id)
     references reports(id)
);

create table id_repository (
    id bigint not null auto_increment,
    nextAllowedID bigint not null default 0,
    primary key (id)
);
