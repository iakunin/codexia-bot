# codexia-bot

Kafka tips & tricks: https://dddpaul.github.io/blog/2018/06/23/kafka-tips/


# TODO list

+ Подключить кафку
+ Научиться реактивно читать из Postgres
+ Научиться пушить в неё, читая hackernews_item из PG

+ Научиться читать из кафки hackernews_item
+ Restore unique indexes after batch import is done
   `alter table hackernews_item add constraint uuid unique`
   `alter table hackernews_item add constraint external_id unique`

+ Разделить сущности `github_repo` и `github_repo_stat`
+ Добавить информацию о том, в каких источниках (hackernews/reddit) 
упоминается репозиторий (таблица `gihub_repo_source`)



- Крон-парсер hackernews в реальном времени.
    hackernews_item -> изменить external_id на int
    Перестроить индекс по hackernews_item.external_id
    Проверить, что max(hackernews_item.external_id) отрабатывает мгновенно
    Проходить автоинкрементом по айдишнику, начиная от max(hackernews_item.external_id) пока не будет 404го http-кода.
    Без реактивщины - просто while (true) цикл.
    Закидывать в кафку.
    
- Кафки-консумер hackernews, который пишет в БД.
    - Переп записью проверять наличие по hackernews_item.external_id

- Крон сборщик статистики из github (раз в час)
    Типы статистики:
        - GITHUB_API
        - HEALTH_CHECK

- Codexia integration
    - Get all projects from codexia
        - Create new kafka-topic `codexia-item`
        - Create new table `codexia_item`
        - Create cron which should call `https://www.codexia.org/recent.json`
        - Create new source CODEXIA in GithubRepoSource enum

- Создать проект-аналог codetabs (https://api.codetabs.com/v1/loc?github=jolav/betazone)
    - https://stackoverflow.com/a/29012789/388916
    - https://github.com/cgag/loc


- Парсить текст и распозновать тему github-репозитория (games, IoT, etc.)
    - Парсить description и README
    - Stop-list
        * games
        * internet of things (iot)


- Reddit-parser

- Github parser
    - Search via api (https://developer.github.com/v3/search/)
    - Перебор репозиториев github по айдишнику
        - https://developer.github.com/v3/#rate-limiting
        - https://api.github.com/repositories/149106857
        - Через прокси для инстаграма (один запрос - одна прокся)




- Слой, который принимает решение отправлять проект/ревью в Codexia (через ручную модерацию)
    - Нотификация в Telegram
    - HTML-страничка со всей инфой по нотификации в codexia + кнопка "Send"
    
    
    
- Схема кафка-взаимодействия > закоммитить в репозиторий

- Написать всех-всех ботов, о которых упоминал Егор
    - not-found: “The repo is absent on GitHub”
    - too-small: “The repo is too small (LoC is 123)”
    - too-many-stars: “The repo is already too popular (5K star)”
    - stars-up: “The repo gained 120 stars in 7 days”
    - forks-up: “The repo was forked 40 times in 7 days”
    - seen-on-hn: “The repo was seen on Hacker News” (link)
    - seen-on-reddit: “The repo was seen on Reddit” (link)






# Бэклог идей

- Ревью
    - Взять критерии качества кода от Егора
    - Подключение к CI
    - Покрытие кода
    - Метрика Hits Of Code


- github-analizer
    - LICENSE type
    - Language
    - Определить тип CI
        - Составить список CI для open-source
        - Перебирать публичные урлы для каждой из CI
    - Определить статус CI на мастере
    - Вытаскивать статистику


- Почитать статьи 
    - "Как должен выглядеть хороший опен-сорс проект"
    - Open-source repo best practices
