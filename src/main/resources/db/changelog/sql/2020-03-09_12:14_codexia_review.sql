create table "codexia_review"
(
    "id"                 bigserial primary key,
    "uuid"               uuid    not null unique,

    "codexia_project_id" bigint  not null references codexia_project (id),
    "author"             varchar not null,
    "reason"             varchar not null,
    "text"               varchar not null,

    "created_at"         timestamp,
    "updated_at"         timestamp
);
