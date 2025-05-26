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

create table rococo.tests_stats
(
    id                uuid             not null default uuid_generate_v1(),
    failed            integer          not null,
    broken            integer          not null,
    skipped           integer          not null,
    passed            integer          not null,
    unknown           integer          not null,
    total             integer          not null,
    is_passed         bool             not null,
    passed_percentage double precision not null default 0.0,
    date_time         timestamp        not null,
    primary key (id)
);
