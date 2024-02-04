# IOT dashboard for EM500-CO2 sensor data

This is a demo project to present the latest and historical sensor data of the EM500-CO2 sensor data from a Lora server.

There are 3 components, the frontend, the backend API (used by the frontend) and the database (to manage user authentication).


## Frontend

The frontend application is located under "sensorwebapp".

- Tech stack:
  - Typescript
  - React
  - Remix.run
  - Tailwindcss

Component to handle browser request. Authentication and data fetching is handled by the backend API.

Page endpoints:

* /
* /login
* /logout
* /sensor


## Backend API

The backend API is located under "sensorAPI".

* Tech stack:
  * Java
  * Spring Boot
  * Gradle
  * Hibernate

Component to provide data and user authentication to the frontend, making the frontend agnostic to the data source.

Rest API endpoints:

* /login
* /invalidatetoken
* /checktoken
* /sensordata
* /lastsensordata

Authentication: User and password for /login. Token for the rest endpoints.


## Database

The database files are located under "postgres".

* Tech stack:
  * PostgreSQL

Component to store users and hashed passwords, and invalidated tokens.


## Deployment to Azure

To run the application in Azure, the following resources are needed:

* Resource group
  * Application gateway
    * Used to enable public access
    * Redirects the traffic to the frontend container (also load balancer if multiple frontend containers)
  * Network security group
    * Firewall to allow connectivity between:
      * Gategay --> Frontend container
      * Frontend container --> Backend API
      * Backend API --> database
  * Container instance
    * 3 container instances, one for each component
    * all using private network
    * Environment variables needed for all of them for users / passwords / URLs
  * Public IP address
    * Used by the gateway to enable internet access to the frontend
  * Virtual network
    * Used by the 3 containers
  * Container registry
    * Repository to store the 3 container images


## Others

##### Run the application

Commands to run and deploy the 3 modules in containers are located in build_all.ps1


##### Authentication

For the login, the frontend calls the /login endpoint of the backend API with a user and password, receiving a temporary token to be used for the other API calls.

The token is stored in the browser cookies.

Since the token mechanism is steteless, for the logout funcionallity, the
