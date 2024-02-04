az login
az acr login --name <AZURE_REGISTRY>.azurecr.io

#database
cd ./postgres
docker build -t sensor-api-postgres .
docker tag sensor-api-postgres <AZURE_REGISTRY>.azurecr.io/sensor-api-postgres:latest
docker push <AZURE_REGISTRY>.azurecr.io/sensor-api-postgres:latest


cd ..

#backend API
cd sensorAPI
.\gradlew.bat clean build
docker build -t sensor-api-image .
docker tag sensor-api-image <AZURE_REGISTRY>.azurecr.io/sensor-api:latest
docker push <AZURE_REGISTRY>.azurecr.io/sensor-api:latest

cd ..
#frontend
cd sensorwebapp
docker build -t sensor-dashboard-image .
docker tag sensor-dashboard-image <AZURE_REGISTRY>.azurecr.io/sensor-dashboard:latest
docker push <AZURE_REGISTRY>.azurecr.io/sensor-dashboard:latest


#to run locally:
#docker run --name sensor-api-db -d -p 5432:5432 -e POSTGRES_PASSWORD=<DB_PASSWORD> sensor-api-postgres
#docker run -d -p 8080:8080 -e JWT_SECRET="<JWT_SECRET>" -e DB_URL="jdbc:postgresql://host.docker.internal:5432/sensorapi" -e DB_USER="postgres" -e DB_PASSWORD="<DB_PASSWORD>" -e SENSOR_DATA_API_ENDPOINT="<LORA_SERVER>" -e SENSOR_DATA_API_USER="LORA_SERVER_USER" -e SENSOR_DATA_API_PASSWORD="LORA_SERVER_PASSWORD" --name sensor-api sensor-api-image 
#docker run -d -p 3000:3000 -e API_BASE_URL="http://host.docker.internal:8080" --name sensor-dashboard sensor-dashboard-image
