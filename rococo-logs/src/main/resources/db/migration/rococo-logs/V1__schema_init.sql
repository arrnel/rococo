create extension if not exists "uuid-ossp";

create schema if not exists rococo;

create table if not exists rococo.logs
(
    id      UUID         not null default uuid_generate_v1(),
    service varchar(255) not null,
    time    timestamp    not null,
    message varchar,
    primary key (id)
);

create index if not exists idx_logs_service
    on rococo.logs (service);
create index if not exists idx_logs_time
    on rococo.logs (time);
