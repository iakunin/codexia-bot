drop index if exists ix__github_repo_source__source__deleted_at;

create index if not exists
    ix__github_repo_source__source__deleted_at
on github_repo_source(source)
where github_repo_source.deleted_at is null;
