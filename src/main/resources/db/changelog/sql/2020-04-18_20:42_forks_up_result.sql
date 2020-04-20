create table "forks_up_result"
(
    "id"                  bigserial primary key,
    "uuid"                uuid   not null unique,

    "github_repo_id"      bigint not null references github_repo (id),
    "github_repo_stat_id" bigint not null references github_repo_stat (id),

    "created_at"          timestamp,
    "updated_at"          timestamp
);

create index ix__forks_up_result__github_repo_id
    on forks_up_result (github_repo_id);
