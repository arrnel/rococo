create extension if not exists "uuid-ossp";

create schema if not exists rococo;

create table if not exists rococo.image_metadata
(
    id           uuid        not null unique default uuid_generate_v1(),
    entity_id    uuid        not null,
    entity_type  varchar(50) not null,
    format       varchar(50) not null,
    content_hash varchar(64) not null,
    content_id   uuid unique not null,
    primary key (id)
);

create table if not exists rococo.image_content
(
    id             uuid  not null unique default uuid_generate_v1(),
    data           bytea not null,
    thumbnail_data bytea not null,
    primary key (id)
);

alter table rococo.image_metadata
    add constraint unique_entity_type_id unique (entity_type, entity_id);

alter table rococo.image_metadata
    add constraint fk__image_metadata__image_content foreign key (content_id)
        references rococo.image_content (id)