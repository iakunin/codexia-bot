create table "too_small_result"
(
    "id"                  bigserial primary key,
    "uuid"                uuid    not null unique,

    "github_repo_id"      bigint  not null references github_repo (id),
    "github_repo_stat_id" bigint  not null references github_repo_stat (id),
    "state"               varchar not null,

    "created_at"          timestamp,
    "updated_at"          timestamp
);


create index ix__too_small_result__github_repo_id
    on too_small_result (github_repo_id);
