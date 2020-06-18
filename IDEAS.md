# Ideas backlog

* Codexia review
    * Code quality requirements from @yegor256
    * Static analysis tools integration (like pmd/checkstyle/qulice)
    * LICENSE type
    * Programming language
    * CONTRIBUTING.md
    * Github issue and pull-request templates
    * Documentation (on some popular doc-platforms)
    * Community (chats, mailing lists, SO tag)
    * Throughput of issues / pull-requests
    * README
        * is exist
        * is english language
    * Public artifacts (Docker/jar/mvn)
        * is exist
        * downloads count
    * yegor256 criteria
    ([source](https://www.yegor256.com/2015/06/08/deadly-sins-software-project.html)):
        * Strict and visible principles of design.
        * Continuous delivery (automated procedure).
        * Traceability of changes.
            * Always Use Tickets.
            * Reference Tickets in Commits.
            * Don’t Delete Anything in git history.
        * Self-documented source code.
        * Strict rules of code formatting.
        * Known test coverage.
        * Milestones and releases with release notes.


* Determine a topic of github-repo (games, IoT, etc.)
    * Parse description и README
    * Topics stop-list
        * games
        * internet of things (iot)

* Create a microservice for determining repo's programming language
    * Analogue of <https://api.codetabs.com/v1/loc?github=jolav/betazone>
    * Reference implementation (slow): <https://github.com/AlDanial/cloc>
    * NPM-package: <https://www.npmjs.com/package/cloc>
    * Go implementation: <https://github.com/boyter/scc>
    * Rust implementation: <https://github.com/cgag/loc>
    * Main repo language is the most LoC language after `Total`
    excluding `YAML`, `XML`, `HTML`, `Text` and others



* Submitting project to Codexia
    According the conversation [here](https://github.com/yegor256/codexia/issues/102)
    there are following rules for submitting a new project to Codexia:
    * Don't submit if there is no activity over last year.
    * Don't submit with less than 100 stars.
    * Don't submit if there are more than 10K stars.
    * Don't submit if there are less than 5K lines of code.
    * Extra:
        * Don't submit if there is no README in a repo.
        * Don't submit if the language of a README is not English.
    * <https://www.codexia.org/focus>



* Github parser
    * Search via api (<https://developer.github.com/v3/search/>)
    * Parse by repo-id
        * <https://developer.github.com/v3/#rate-limiting>
        * <https://api.github.com/repositories/149106857>
        * Use proxy


* Read articles about open-source best practices
