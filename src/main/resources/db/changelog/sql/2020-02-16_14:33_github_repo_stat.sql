create table "github_repo_stat"
(
    "id"              bigserial primary key,
    "uuid"            uuid    not null unique,

    "github_repo_id" bigint not null references github_repo(id),

    "type" varchar not null,
    "stat" json,

    "created_at"      timestamp,
    "updated_at"      timestamp
);

create index ix__github_repo_stat__type
    on github_repo_stat(type);
