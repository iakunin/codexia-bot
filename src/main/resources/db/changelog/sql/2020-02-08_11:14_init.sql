create table "item" (
    "id" bigserial primary key,
    "uuid" uuid unique,
    "external_id" varchar unique,
    "type" varchar,
    "by" varchar,
    "title" varchar,
    "text" varchar,
    "url" varchar,
    "time" timestamp,
    "created_at" timestamp,
    "updated_at" timestamp
);
