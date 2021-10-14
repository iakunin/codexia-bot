# codexia-bot

[![CircleCI](https://circleci.com/gh/iakunin/codexia-bot.svg?style=shield)](https://circleci.com/gh/iakunin/codexia-bot)
[![PDD status](http://www.0pdd.com/svg?name=iakunin/codexia-bot)](http://www.0pdd.com/p?name=iakunin/codexia-bot)
[![codecov](https://codecov.io/gh/iakunin/codexia-bot/branch/master/graph/badge.svg)](https://codecov.io/gh/iakunin/codexia-bot)
[![dependabot](https://api.dependabot.com/badges/status?host=github&repo=iakunin/codexia-bot)](https://dependabot.com/)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/iakunin/codexia-bot/blob/master/LICENSE)

[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=iakunin_codexia-bot&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=iakunin_codexia-bot)
[![Maintainability](https://api.codeclimate.com/v1/badges/ad3831a0be7db8b87a5f/maintainability)](https://codeclimate.com/github/iakunin/codexia-bot/maintainability)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/061785f8241f4207ae772a48c9678009)](https://www.codacy.com/manual/yakuninm-github/codexia-bot?utm_source=github.com&utm_medium=referral&utm_content=iakunin/codexia-bot&utm_campaign=Badge_Grade)
[![CodeFactor](https://www.codefactor.io/repository/github/iakunin/codexia-bot/badge)](https://www.codefactor.io/repository/github/iakunin/codexia-bot)
[![codebeat badge](https://codebeat.co/badges/92112209-798a-4d3a-9dc3-12e31deca058)](https://codebeat.co/projects/github-com-iakunin-codexia-bot-master)
[![BCH compliance](https://bettercodehub.com/edge/badge/iakunin/codexia-bot?branch=master)](https://bettercodehub.com/)

[![Hits-of-Code](https://hitsofcode.com/github/iakunin/codexia-bot)](https://hitsofcode.com/view/github/iakunin/codexia-bot)
[![Scc Total Lines](https://sloc.xyz/github/iakunin/codexia-bot)](https://github.com/iakunin/codexia-bot)
[![Scc Code Lines](https://sloc.xyz/github/iakunin/codexia-bot?category=code)](https://github.com/iakunin/codexia-bot)

**Codexia-bot** a supervised bot for [Codexia (open source incubator)](https://www.codexia.org/).


## Bots list

### not-found

`The repo is absent on GitHub`

### too-small

`The repo is too small (LoC is 123)`

* report when the size of the repo is smaller than the threshold
* report text `The repo is too small (LoC is 123)`
* set meta marker once reported
* attach a badge `bad` once reported
* don't report anymore, while the meta marker is present
* report, remove the marker and badge if the size of the repo is above the threshold
* report text `The repo is not small anymore (LoC is 456)`
* the threshold is 5K LoC

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

## How to contribute

See [CONTRIBUTING.md](https://github.com/iakunin/codexia-bot/blob/master/.github/CONTRIBUTING.md)
for more details.

## Got questions?

If you have questions or general suggestions, don't hesitate to submit
a new [Github issue](https://github.com/iakunin/codexia-bot/issues/new/choose).


