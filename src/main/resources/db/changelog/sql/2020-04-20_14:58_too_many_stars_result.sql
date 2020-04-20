create table "too_many_stars_result"
(
    "id"                  bigserial primary key,
    "uuid"                uuid   not null unique,

    "github_repo_id"      bigint not null references github_repo (id),
    "github_repo_stat_id" bigint not null references github_repo_stat (id),

    "created_at"          timestamp,
    "updated_at"          timestamp
);

create index ix__too_many_stars_result__github_repo_id
    on too_many_stars_result (github_repo_id);
