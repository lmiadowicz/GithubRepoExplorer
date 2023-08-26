# Github Repo Explorer API

## Overview
This project exposes an API to list all GitHub repositories of a given user which are not forks. It returns details such as the repository name, the owner's login, and details about each branch, including its name and the SHA of the last commit.

## Acceptance Criteria

### Criteria 1
Given a username and header "Accept: application/json", the API will list all of the user's GitHub repositories that are not forks. The response will include:
- Repository Name
- Owner Login
- For each branch, its name and last commit SHA

### Criteria 2
Given a non-existent GitHub username, the API will return a 404 response formatted as:
```json
{
  "status": 404,
  "Message": "User not found"
}
```

### Criteria 3
Given a header "Accept: application/xml", the API will return a 406 response formatted as:
```json
{
"status": 406,
"Message": "Unsupported accept header"
}
```

## Tech Stack & Libraries

- Java 17
- Spring Boot 3.1.2
- Feign Http Client
- Lombok
- JUnit 5
- Wiremock
- Mockito
- Maven
- Docker
- GitHub Actions
- Docker Hub for image storing which is used by GitHub Actions to move it to Render.com
- Render.com for deployment

## Local Build Process
1. Clone the repository:  
   `git clone https://github.com/yourusername/GithubRepoExplorer.git`
2. Navigate to the project directory:  
   `cd GithubRepoExplorer`
3. Run the Maven build:  
   `mvn clean install`
4. Build the Docker image:  
   `docker build -t githubrepoexplorer .`
5. Run the Docker container:  
   `docker run -p 8080:8080 githubrepoexplorer`

## Environment Variables
- `GH_API_TOKEN`: GitHub API Token

## Deployment
### Environment Variables in GitHub Secrets
- `DOCKER_USERNAME`: Your Docker username
- `DOCKER_TOKEN`: Your Docker API token
- `RENDER_API_KEY`: Your Render API key
- `RENDER_SERVICE_ID`: Your Render service ID
- `GH_API_TOKEN`: GitHub API Token

### Deployment Service
I use Render.com for deployment. Configuration details can be found [here](https://render.com/docs/deploy-an-image). Remember to add the environment variables in the Render Dashboard for deployed container.

## GitHub Actions Workflow

This project leverages GitHub Actions for continuous integration and deployment (CI/CD). Below are the stages that make up the pipeline as defined in `.github/workflows/main.yml`.

### Stages

#### Checkout Code
- Uses `actions/checkout@v2` to checkout the project code into the runner.

#### Setup Environment
- Sets up Java using `actions/setup-java@v2`.
- Sets up Maven caching to speed up future builds.
- Sets up Docker.

#### Build and Test
- Runs `mvn clean install` to build the project and run unit tests.
- Uses `wiremock` for mocking external services during integration tests.

#### Dockerize Application
- Builds a Docker image using the project's `Dockerfile`.
- Pushes the Docker image to Docker Container Registry.

#### Deploy to Render.com
- Uses a custom script to deploy the pushed Docker image to Render.com.
- Deployment only proceeds if all test cases pass and the Docker image is successfully pushed to the registry.

#### Environment Variables
- Various secrets and environment variables are used, these should be set in your GitHub project settings under "Secrets". They include `DOCKER_USERNAME`, `DOCKER_TOKEN`, and `RENDER_API_KEY`, `RENDER_SERVICE_ID`, `GH_API_TOKEN`.

#### Failure Handling
- If any step fails, the entire workflow is halted, and no deployment occurs.


## Test Endpoints on Production

The API is deployed at `https://githubrepoexplorer.onrender.com`.

### Example Success Endpoint Request for lmiadowicz Repos

To test a successful request for user `lmiadowicz`'s repositories, you can run the following cURL command:

    ```bash
    curl -X GET "https://githubrepoexplorer.onrender.com/repos/lmiadowicz" \
         -H "Accept: application/json"
    ```

### Example for Invalid Headers

To test a request with invalid headers, use the following cURL command:

    ```bash
    curl -X GET "https://githubrepoexplorer.onrender.com/repos/lmiadowicz" \
         -H "Accept: application/xml"
    ```

You should receive a 406 response code along with a JSON message.

### Example for User Not Found

To test a request where the user does not exist, use the following cURL command:

    ```bash
    curl -X GET "https://githubrepoexplorer.onrender.com/repos/nonexistent-user" \
         -H "Accept: application/json"
    ```

You should receive a 404 response code along with a JSON message.
