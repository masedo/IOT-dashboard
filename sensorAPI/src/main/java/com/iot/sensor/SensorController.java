package com.iot.sensor;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SensorController {
	
	private final ApiFetcher apiFetcher;

    public SensorController(ApiFetcher apiFetcher) {
        this.apiFetcher = apiFetcher;
    }

    /**
     * Endpoint function to return all sensor data
     * 
     * @return
     */
    @GetMapping("/sensordata")
    public String getSensorData() {
        JSONObject jsonResponse = apiFetcher.getSensorData();
        return jsonResponse.toString();
    }
    
    /**
     * Endpoint function to return latest sensor data
     * 
     * @return
     */
    @GetMapping("/lastsensordata")
    public String getLastSensorData() {
        JSONObject jsonResponse = apiFetcher.getLastSensorData();
        return jsonResponse.toString();
    }
}
