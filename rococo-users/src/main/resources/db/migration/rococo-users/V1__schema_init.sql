create extension if not exists "uuid-ossp";

create schema if not exists rococo;

create table if not exists rococo.users
(
    id         UUID        not null unique default uuid_generate_v1(),
    username   varchar(50) not null unique,
    first_name varchar(255),
    last_name  varchar(255),
    primary key (id, username)
);