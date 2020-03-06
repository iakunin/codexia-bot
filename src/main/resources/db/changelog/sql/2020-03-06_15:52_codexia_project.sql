create table "codexia_project"
(
    "id"                 bigserial primary key,
    "uuid"               uuid      not null unique,

    "external_id"        int       not null unique,
    "coordinates"        varchar   not null,
    "author"             varchar   not null,
    "author_id"          int       not null,
    "deleted"            varchar,
    "project_created_at" timestamp not null,

    "created_at"         timestamp,
    "updated_at"         timestamp
);
