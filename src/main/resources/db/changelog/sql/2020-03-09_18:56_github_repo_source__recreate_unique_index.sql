drop index if exists "uq__github_repo_source__external_id__source__github_repo_id";

create unique index uq__github_repo_source__source__external_id__github_repo_id
    on github_repo_source(source, external_id, github_repo_id);
