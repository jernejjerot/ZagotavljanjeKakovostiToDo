package com.ris.todoapp.service;

import com.ris.todoapp.dto.NominatimResponse;
import com.ris.todoapp.dto.GeocodingResult;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class GeocodingService {
    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search?q=%s&format=json";

    public Optional<GeocodingResult> geocode(String address) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            // Prepare the URL with the address
            String url = String.format(NOMINATIM_URL, address.replace(" ", "+"));
            NominatimResponse[] response = restTemplate.getForObject(url, NominatimResponse[].class);

            // If the API returns results
            if (response != null && response.length > 0) {
                GeocodingResult result = new GeocodingResult();
                result.setLatitude(Double.parseDouble(response[0].getLat()));
                result.setLongitude(Double.parseDouble(response[0].getLon()));
                return Optional.of(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty(); // Return empty Optional if no results or exception occurs
    }
}