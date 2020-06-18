# How to contribute

:+1::tada: First off, thanks for taking the time to contribute! :tada::+1:

Steps to contribute:

* fork repository,
* make changes,
* send us a pull request.

We will review your changes and apply them to the master branch shortly,
provided they don't violate our quality standards.


## Testing

To avoid frustration, before sending us your pull request please run full Gradle build.

First, install [Docker](https://docs.docker.com/get-docker/).

Then just run following bash command from project root-dir:

```bash
bash bin/gradle_in_docker.sh clean build --info
```

## Quality standards

All the quality standards presented in the form of GitHub merge-checks.

Therefore, don't hesitate to open a new pull-request and see if your changes break any
quality standards.

There is no way to check all the quality standards on local environment: only by opening
a pull-request.
