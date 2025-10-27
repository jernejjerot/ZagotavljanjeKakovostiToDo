package com.ris.todoapp.service;

import com.ris.todoapp.dto.GeocodingResult;
import com.ris.todoapp.dto.NominatimResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@Service
public class GeocodingService {

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Izvede geokodiranje podanega naslova z uporabo Nominatim API-ja.
     * Če pride do napake ali praznega odgovora, vrne Optional.empty().
     */
    public Optional<GeocodingResult> geocode(String address) {
        try {
            if (address == null || address.isBlank()) {
                System.err.println("[Geocoding] Empty address – skipping lookup.");
                return Optional.empty();
            }

            String url = UriComponentsBuilder
                    .fromHttpUrl("https://nominatim.openstreetmap.org/search")
                    .queryParam("format", "json")
                    .queryParam("limit", 1)
                    .queryParam("q", address)
                    .toUriString();

            // Nominatim zahteva User-Agent header, sicer vrne HTML in 403
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "todoapp/1.0 (student project, FERI)");
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<NominatimResponse[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    NominatimResponse[].class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                System.err.println("[Geocoding] Non-200 response: " + response.getStatusCode());
                return Optional.empty();
            }

            NominatimResponse[] body = response.getBody();
            if (body != null && body.length > 0) {
                NominatimResponse result = body[0];
                double lat = Double.parseDouble(result.getLat());
                double lon = Double.parseDouble(result.getLon());
                return Optional.of(new GeocodingResult(lat, lon));
            } else {
                System.err.println("[Geocoding] Empty result for address: " + address);
                return Optional.empty();
            }

        } catch (RestClientException | NumberFormatException e) {
            System.err.println("[Geocoding] Request failed for address '" + address + "': " + e.getMessage());
            return Optional.empty();
        }
    }
}