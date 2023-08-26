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
- Feign
- Lombok
- JUnit 5
- Wiremock
- Mockito
- Maven
- Docker
- GitHub Actions
- Docker Hug for image storing which is used by GitHub Actions to move it to Render.com
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
We use Render.com for deployment. Configuration details can be found [here](https://render.com/docs/deploy-an-image). Remember to add the environment variables in the Render Dashboard for deployed container.

### GitHub Actions Workflow
This project uses a GitHub Actions workflow for CI/CD. Unit tests and integration tests are run before any deployment steps. Failure in these steps will halt the deployment process.

## Test Endpoints on Production
- The API is deployed at [https://githubrepoexplorer.onrender.com](https://githubrepoexplorer.onrender.com).
- Example success endpoint request for `lmiadowicz` repos: `https://githubrepoexplorer.onrender.com/repos/lmiadowicz`
- Example for invalid headers: `https://githubrepoexplorer.onrender.com/repos/lmiadowicz` (with header `Accept: application/xml`)
- Example for user not found: `https://githubrepoexplorer.onrender.com/repos/nonexistent-user`