create table "github_repo_source"
(
    "id"              bigserial primary key,
    "uuid"            uuid    not null unique,

    "github_repo_id" bigint not null references github_repo(id),

    "source" varchar not null,
    "external_id" varchar not null,

    "created_at"      timestamp,
    "updated_at"      timestamp
);

create unique index uq__github_repo_source__type
    on github_repo_source("source", "external_id");
