# Solution

## Project structure

### Java implementation

All the source code content is located in the [src/main/java/...](src/main/java/com/clara/ops/challenge/document_management_service_challenge) package.

There are a few implementation details:

- `annotations` contains all the necessary annotations to reduce the amount of code needed in the controller classes.
- `config` the necessary configuration classes.
- `controller` contains the document-management controller with its DTOs and Interface classes.
- `domain` the JPA entities and repositories.
- `exception` Error handling utilities.
- `service` The service layer.
- `utils` Miscellaneous tools such as an InputSanitizer and an entity to DTO converter.

### Docker

The `docker` folder contains the [docker-compose.yml](docker/docker-compose.yml) file necessary to run the docker containers
as well as a `.env` file that is used to set the environment variables needed for the solution.

### API Reference

The open-api.yml file can be found [here](solution-open-api.yml)

## How to run it

To run the project, run the following command from the root folder of the directory

```shell
docker compose -f docker/docker-compose.yml up -d --build
```

# 

