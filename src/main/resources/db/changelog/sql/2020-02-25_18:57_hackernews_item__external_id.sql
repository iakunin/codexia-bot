alter table "hackernews_item"
alter column external_id type int using external_id::int;
