create table "codexia_review_notification"
(
    "id"                bigserial primary key,
    "uuid"              uuid    not null unique,

    "codexia_review_id" bigint  not null references codexia_review (id),

    "status"            varchar not null,
    "response_code"     integer,
    "response"          varchar,

    "created_at"        timestamp,
    "updated_at"        timestamp
);
