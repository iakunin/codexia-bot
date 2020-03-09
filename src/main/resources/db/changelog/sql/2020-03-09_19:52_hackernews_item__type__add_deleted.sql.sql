alter table "hackernews_item"
    add column if not exists "deleted" boolean default false;
