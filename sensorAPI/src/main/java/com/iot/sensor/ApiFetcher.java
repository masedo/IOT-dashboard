package com.iot.sensor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApiFetcher {

	@Value("${sensor.data.api.endpoint}")
	private String apiUrl;
	@Value("${sensor.data.api.user}")
	private String username;
	@Value("${sensor.data.api.password}")
	private String password;
	
	/**
	 * Funció que fa un GET a la API amb autenticació bàsica utilitzant els paràmetres del application.properties
	 * 
	 * @return 	retorna un objecte JSON amb el resultat de la crida a la API 
	 * 			retorna null si hi ha una excepció
	 */
	private JSONObject fetchDataFromApi() {		
		
		try {
			URL url = new URL(apiUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			String encodedCredentials = Base64.getEncoder()
					.encodeToString((username + ":" + password).getBytes());
			connection.setRequestProperty("Authorization", "Basic " + encodedCredentials);

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			StringBuilder response = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();

			return new JSONObject(response.toString());

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Funció que decodifica els valors de payload i ordena el resultat segons el timestamp 
	 * 
	 * El paràmetre que se li passa és un objecte JSON amb els payloads codificats
	 * @param jsonObject		
	 * 		
	 * @return	retorna un objecte JSON amb els payloads decodificats
	 */
	private static JSONObject decodeJsonPayload(JSONObject jsonObject) {
		JSONArray docs = jsonObject.getJSONArray("docs");
		JSONObject decodedDocs = new JSONObject();

		for (int i = 0; i < docs.length(); i++) {
			JSONObject doc = docs.getJSONObject(i);
			String wirelessDeviceId = doc.getString("WirelessDeviceId");
			String payloadData = doc.getString("PayloadData");
			JSONObject wirelessMetadata = doc.getJSONObject("WirelessMetadata").getJSONObject("LoRaWAN");
			String timestamp = wirelessMetadata.getString("Timestamp");

			byte[] bytes = Base64.getDecoder().decode(payloadData);

			Map<String, Object> decodedData = PayloadDecoder.decode(85, bytes);

			JSONObject decodedObj = new JSONObject(decodedData); 
			decodedObj.put("Timestamp", timestamp);

			JSONArray deviceEntries = decodedDocs.optJSONArray(wirelessDeviceId);
			if (deviceEntries == null) {
				deviceEntries = new JSONArray();
				decodedDocs.put(wirelessDeviceId, deviceEntries);
			}
			deviceEntries.put(decodedObj);


			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			decodedDocs.keys().forEachRemaining(deviceId -> {
				JSONArray currentEntries = decodedDocs.getJSONArray(deviceId);
				List<JSONObject> jsonList = new ArrayList<>();
				for (int j = 0; j < currentEntries.length(); j++) {
					jsonList.add(currentEntries.getJSONObject(j));
				}

				Collections.sort(jsonList, new Comparator<JSONObject>() {
					@Override
					public int compare(JSONObject a, JSONObject b) {
						try {
							Date dateA = dateFormat.parse(a.getString("Timestamp"));
							Date dateB = dateFormat.parse(b.getString("Timestamp"));
							return dateA.compareTo(dateB);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
				});

				JSONArray sortedArray = new JSONArray();
				for (JSONObject jsonObj : jsonList) {
					sortedArray.put(jsonObj);
				}
				decodedDocs.put(deviceId, sortedArray);
			});
		}
		return decodedDocs;
	}

	/**
	 * Funció per rebre les dades del sensor i decodificar els payloads
	 * 
	 * @return	retorna un objecte json amb la següent informació de cada sensor amd ID diferent (ordenat segons timestamp):
	 * 				- temperature
	 * 				- humidity
	 * 				- pressure
	 * 				- Timestamp
	 * 				- co2
	 */
	public JSONObject getSensorData() {

		JSONObject rawData = fetchDataFromApi();

		JSONObject decodedObject = decodeJsonPayload(rawData);

		return decodedObject;		
	}

	/**
	 * Funció per rebre les últimes dades del sensor i decodificar els payloads
	 * 
	 * @return	retorna un objecte json amb la següent informació de cada sensor amd ID diferent (només els últims valors):
	 * 				- temperature
	 * 				- humidity
	 * 				- pressure
	 * 				- Timestamp
	 * 				- co2
	 */
	public JSONObject getLastSensorData() {
        JSONObject allSensorData = getSensorData();
        JSONObject lastSensorData = new JSONObject();

        allSensorData.keys().forEachRemaining(deviceId -> {
            JSONArray entries = allSensorData.getJSONArray(deviceId);
            if (entries.length() > 0) {
                JSONObject lastEntry = entries.getJSONObject(entries.length() - 1);
                lastSensorData.put(deviceId, lastEntry);
            }
        });

        return lastSensorData;
    }


}
