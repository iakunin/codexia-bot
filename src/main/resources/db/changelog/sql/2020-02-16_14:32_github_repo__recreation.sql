drop table if exists "github_repo";

create table "github_repo"
(
    "id"              bigserial primary key,
    "uuid"            uuid    not null unique,

    "external_id"     varchar not null unique,
    "full_name"       varchar not null,
    "html_url"        varchar,
    "repo_created_at" timestamp,

    "created_at"      timestamp,
    "updated_at"      timestamp
);
