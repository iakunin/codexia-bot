drop index if exists ix__github_repo_stat__type;

create index if not exists ix__github_repo_stat__type__github_repo_id
    on github_repo_stat(type, github_repo_id);
