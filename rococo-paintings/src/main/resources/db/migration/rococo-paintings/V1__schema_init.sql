create extension if not exists "uuid-ossp";

create schema if not exists rococo;

create table if not exists rococo.paintings
(
    id           UUID         not null unique default uuid_generate_v1(),
    title        varchar(255) not null unique,
    description  varchar(2000),
    artist_id    UUID         not null,
    museum_id    UUID         not null,
    created_date timestamp    not null,
    primary key (id)
);