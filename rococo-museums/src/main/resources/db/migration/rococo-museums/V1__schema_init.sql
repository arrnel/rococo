create extension if not exists "uuid-ossp";

create schema if not exists rococo;

create table if not exists rococo.museums
(
    id           UUID         not null unique default uuid_generate_v1(),
    title        varchar(255) not null unique,
    description  varchar(2000),
    country_id   UUID,
    city         varchar(255),
    created_date timestamp    not null,
    primary key (id)
);