create index if not exists ix__github_repo_source__source__deleted_at
    on github_repo_source(source, deleted_at);
