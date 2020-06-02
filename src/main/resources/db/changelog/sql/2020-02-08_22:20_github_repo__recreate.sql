drop table "github_repo";

create table "github_repo"
(
    "id"              bigserial primary key,
    "uuid"            uuid    not null unique,
    "external_id"     varchar not null unique,

    "full_name"       varchar,
    "html_url"        varchar,
    "description"     varchar,
    "language"        varchar,
    "homepage"        varchar,

    "forks"           integer,
    "watchers"        integer,
    "stars"           integer,
    "releases"        integer,
    "used_by"         integer,

    "lines_of_code"   json,

    "repo_created_at" timestamp,
    "pushed_at"       timestamp,

    "created_at"      timestamp,
    "updated_at"      timestamp
);
