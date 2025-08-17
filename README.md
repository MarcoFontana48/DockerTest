[![ci-cd](https://github.com/MarcoFontana48/AUSL-Romagna-microservizi-CCE-proposta-di-progetto/actions/workflows/ci-cd.yml/badge.svg?branch=master)](https://github.com/MarcoFontana48/AUSL-Romagna-CCE-Microservices-Project-Proposal/actions/workflows/ci-cd.yml)
[![semantic-release: angular](https://img.shields.io/badge/semantic--release-angular-e10079?logo=semantic-release)](https://github.com/semantic-release/semantic-release)
[![Renovate](https://img.shields.io/badge/renovate-enabled-brightgreen.svg)](https://renovatebot.com)

# DockerTest
Library to enable tests using Docker in a simple and automated way.

## How to use it
- Import the library in your project by adding the dependency to your `build.gradle` file
- Create a test class that extends `DockerTest`
- Add a docker-compose file in the `src/test/resources` directory of your project, any name is fine, but it must be a valid docker-compose file
- Now you can use the methods provided by the `DockerTest` class to run tests with Docker; for example, you can use the `dockerComposeUp(...)` method passing the name of the docker-compose file, and it will start the containers defined in the file. You can also use `dockerComposeDown(...)` to stop and remove the containers.