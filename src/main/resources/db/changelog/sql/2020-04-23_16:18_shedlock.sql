create table shedlock
(
    name       varchar primary key,
    lock_until timestamp null,
    locked_at  timestamp null,
    locked_by  varchar
);
