# IOT dashboard for EM500-CO2 sensor data

This is a demo project that presents the latest and historical sensor data from the EM500-CO2 sensor through a LoRa server.

The project comprises three components: the frontend, the backend API (utilized by the frontend), and the database (to manage user authentication).


## Frontend

The frontend application is located in the "sensorwebapp" directory.

* Tech stack:
  * TypeScript
  * React
  * Remix.run
  * Tailwind CSS

This component handles browser requests. Authentication and data fetching are managed by the backend API.

Page endpoints:

* /
* /login
* /logout
* /sensor


## Backend API

The backend API is situated in the "sensorAPI" directory.

* Tech stack:
  * Java
  * Spring Boot
  * Gradle
  * Hibernate

This component provides data and user authentication to the frontend, making the frontend agnostic to the data source.

REST API endpoints:

* /login
* /invalidatetoken
* /checktoken
* /sensordata
* /lastsensordata

Authentication involves using a username and password for /login and tokens for the rest of the endpoints.


## Database

The database files are found in the "postgres" directory.

* Tech stack:
  * PostgreSQL

This component stores users and hashed passwords, as well as invalidated tokens.


## Deployment to Azure

To deploy the application on Azure, the following resources are required:

* Resource group
  * Application gateway
    * Enables public access
    * Redirects traffic to the frontend container (also acts as a load balancer for multiple frontend containers)
  * Network security group
    * Firewall settings allow connectivity between:
      * Gateway → Frontend container
      * Frontend container → Backend API
      * Backend API → Database
  * Container instances
    * Three container instances, one for each component
    * All utilize a private network
    * Environment variables are necessary for all instances for users/passwords/URLs
  * Public IP address
    * Enables internet access to the frontend through the gateway
  * Virtual network
    * Used by the three containers
  * Container registry
    * Repository to store the three container images


## Additional Information

##### Running the Application

Commands to run and deploy the three modules in containers are located in `build_all.ps1`.


##### Authentication

For login, the frontend calls the /login endpoint of the backend API with a username and password, receiving a temporary token for other API calls.

The token is stored in browser cookies.

Since the token mechanism is stateless, for the logout functionality, the backend inserts the token into the invalidated tokens table. A batch job removes invalidated tokens when they expire.
