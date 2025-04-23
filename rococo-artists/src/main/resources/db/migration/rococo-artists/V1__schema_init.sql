create extension if not exists "uuid-ossp";

create schema if not exists rococo;

create table if not exists rococo.artists
(
    id           UUID         not null unique default uuid_generate_v1(),
    name         varchar(255) not null unique,
    biography    varchar(2000),
    created_date timestamp    not null,
    primary key (id)
);