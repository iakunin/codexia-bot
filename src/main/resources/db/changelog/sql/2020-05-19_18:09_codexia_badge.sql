create table "codexia_badge"
(
    "id"                 bigserial primary key,
    "uuid"               uuid    not null unique,

    "codexia_project_id" bigint  not null references codexia_project (id),
    "badge"              varchar not null,

    "created_at"         timestamp,
    "updated_at"         timestamp,
    "deleted_at"         timestamp
);

create unique index uq__codexia_badge__project__badge
    on codexia_badge ("codexia_project_id", "badge");
