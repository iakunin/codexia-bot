create index if not exists ix__codexia_review_notification__codexia_review_id
    on codexia_review_notification(codexia_review_id);

create index if not exists ix__codexia_review_notification__status
    on codexia_review_notification(status);
