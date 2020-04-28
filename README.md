# codexia-bot

[![CircleCI](https://circleci.com/gh/iakunin/codexia-bot.svg?style=shield)](https://circleci.com/gh/iakunin/codexia-bot)
[![PDD status](http://www.0pdd.com/svg?name=iakunin/codexia-bot)](http://www.0pdd.com/p?name=iakunin/codexia-bot)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=iakunin_codexia-bot&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=iakunin_codexia-bot)
[![codecov](https://codecov.io/gh/iakunin/codexia-bot/branch/master/graph/badge.svg)](https://codecov.io/gh/iakunin/codexia-bot)
[![Maintainability](https://api.codeclimate.com/v1/badges/ad3831a0be7db8b87a5f/maintainability)](https://codeclimate.com/github/iakunin/codexia-bot/maintainability)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/iakunin/codexia-bot/blob/master/LICENSE)
[![Hits-of-Code](https://hitsofcode.com/github/iakunin/codexia-bot)](https://hitsofcode.com/view/github/iakunin/codexia-bot)

This is a supervised bot for [Codexia (open source incubator)](https://www.codexia.org/).


# Bots list

### not-found
`The repo is absent on GitHub`

### too-small
`The repo is too small (LoC is 123)`

* report when the size of the repo is smaller than the threshold
* set meta marker once reported
* don't report anymore, while the meta marker is present
* remove the marker is the size of the repo is above the threshold

### too-many-stars
`The repo is already too popular (5K star)`

### seen-on-hn
`The repo was seen on Hacker News (link)`

### seen-on-reddit
`The repo was seen on Reddit (link)`

### forks-up
`The repo was forked 40 times in 7 days`

### stars-up
`The repo gained 120 stars in 7 days`

* 5% increase/decrease in both metrics (forks/stars).
* If the difference is smaller than 10 stars, the bot stays quiet.
* From 0 to 200 stars bot will be sending 1 review per 10 stars increase.
* From 200 to 220 stars bot will be sending 1 review per 11 stars increase.
* From 220 to 240 stars bot will be sending 1 review per 12 stars increase.
* And so on.

# Ideas backlog

- Codexia review
    - Code quality requirements from @yegor256
    - Integration with CI
        - CI type (CircleCI, Travis, etc.)
        - CI status on master
    - Code coverage
    - Static analysis tools integration
    - Hits Of Code metrics
    - Repo has no activity during last year
    - LICENSE type
    - Programming language
    - CONTRIBUTING.md
    - Github issue and pull-request templates
    - Documentation (on some popular doc-platforms)


- Determine topic of github-repo (games, IoT, etc.)
    - Parse description и README
    - Topics stop-list
        * games
        * internet of things (iot)


- Create microservice for determining repo's programming language
    - Analogue of https://api.codetabs.com/v1/loc?github=jolav/betazone
    - https://stackoverflow.com/a/29012789/388916
    - https://github.com/cgag/loc
    - https://github.com/github/linguist
    - https://developer.github.com/v3/repos/#list-languages


- Submitting project to Codexia
    According the conversation [here](https://github.com/yegor256/codexia/issues/102) there are following rules for submitting a new project to Codexia:
    * Don't submit if there is no activity over the last year.
    * Don't submit with less than 100 stars.
    * Don't submit if there are more than 10K stars.
    * Don't submit if there are less than 5K lines of code.
    * Extra:
        * Don't submit if there is no README in repo.
        * Don't submit if the language of a README is not English.
    * https://www.codexia.org/focus



- Github parser
    - Search via api (https://developer.github.com/v3/search/)
    - Parse by repo-id
        - https://developer.github.com/v3/#rate-limiting
        - https://api.github.com/repositories/149106857
        - Use proxy


- Read articles about open-source best practices
