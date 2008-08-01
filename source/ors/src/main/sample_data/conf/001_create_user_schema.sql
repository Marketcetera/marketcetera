
    drop table if exists ors_users;

    create table ors_users (
        id bigint not null auto_increment,
        lastUpdated datetime,
        updateCount integer not null,
        description varchar(255),
        name varchar(255) not null,
        hashedPassword varchar(255) not null,
        primary key (id),
        unique (name)
    );
