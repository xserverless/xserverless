create schema if not exists xserverless
    character set = 'utf8'
    collate = 'utf8_general_ci';

CREATE USER IF NOT EXISTS 'xserverless'@'%' IDENTIFIED BY 'Abc123++';
GRANT ALL PRIVILEGES ON *.* TO 'xserverless'@'%' IDENTIFIED BY 'Abc123++' WITH GRANT OPTION;
FLUSH PRIVILEGES;

use xserverless;
create table if not exists tb_function
(
    id           int          not null auto_increment,
    jar_id       int          not null,
    path         varchar(256) not null,
    owner        varchar(256) not null,
    name         varchar(256) not null,
    descriptor   varchar(256) not null,
    url_pattern  varchar(256) not null,
    http_methods int          not null, -- 0x0001: get, 0x0010: post, 0x0100: put, 0x1000: delete

    primary key (id)
) engine = InnoDB;

create table if not exists tb_jar
(
    id               int          not null auto_increment,
    name             varchar(256) not null,
    path             varchar(256) not null,
    status           int          not null, -- 0: init, 1: started, 2: finished, 3+: error
    start_timestamp  bigint       not null,
    finish_timestamp bigint       not null,

    primary key (id)
) engine = InnoDB;

create table if not exists tb_env
(
    id          int                 not null auto_increment,
    name        varchar(256) unique not null,
    description varchar(1024) default '',
    domain      varchar(256) unique not null,
    db_schema   varchar(256)        not null,
    db_username varchar(256)        not null,
    db_password varchar(256)        not null,

    primary key (id)
) engine = InnoDB;

create table if not exists tb_deployment
(
    id          int not null auto_increment,
    env_id      int not null,
    function_id int not null,

    primary key (id)
) engine = InnoDB;


create table if not exists tb_event
(
    id          int          not null auto_increment,
    type        varchar(256) not null,
    timestamp   bigint       not null,
    function_id int          null,
    env_domain  varchar(256) null,
    server      varchar(256) not null,

    primary key (id)
) engine = InnoDB;


create table if not exists tb_front_page
(
    id   int          not null auto_increment,
    name varchar(256) not null,
    path varchar(256) not null,

    primary key (id)
) engine = InnoDB;


create table if not exists tb_front_deployment
(
    id          int          not null auto_increment,
    env_id      int          not null,
    front_page_id int          not null,
    context     varchar(256) not null,

    primary key (id)
) engine = InnoDB;