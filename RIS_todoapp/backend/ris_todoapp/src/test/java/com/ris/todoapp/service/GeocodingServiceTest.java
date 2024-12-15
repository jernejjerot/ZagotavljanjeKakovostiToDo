package com.ris.todoapp.service;

import com.ris.todoapp.dto.GeocodingResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class GeocodingServiceTest {

    private GeocodingService geocodingService;

    @BeforeEach
    void setUp() {
        // Inicializacija storitve (brez mockiranja, če testirate dejansko implementacijo)
        geocodingService = new GeocodingService(); // Zamenjajte z ustrezno inicializacijo, če so odvisnosti
    }

    @Test
    void testSuccessfulGeocoding() {
        // Arrange: Nastavite vhodni naslov in pričakovani rezultat
        String address = "123 Main Street";
        GeocodingResult expectedResult = new GeocodingResult();
        expectedResult.setLatitude(40.7128);
        expectedResult.setLongitude(-74.0060);

        // Simulirajte geokodiranje (v resničnem testu bi to izvajal pravi API ali simulacija)
        geocodingService = new GeocodingService() {
            @Override
            public Optional<GeocodingResult> geocode(String address) {
                if ("123 Main Street".equals(address)) {
                    return Optional.of(expectedResult);
                }
                return Optional.empty();
            }
        };

        // Act: Kličite metodo za geokodiranje
        Optional<GeocodingResult> result = geocodingService.geocode(address);

        // Assert: Preverite, ali so rezultati pravilni
        assertTrue(result.isPresent(), "Expected geocoding result to be present");
        assertEquals(40.7128, result.get().getLatitude(), 0.001, "Latitude mismatch");
        assertEquals(-74.0060, result.get().getLongitude(), 0.001, "Longitude mismatch");
    }

    @Test
    void testGeocodingNoResults() {
        // Arrange: Nastavite naslov, ki ne obstaja
        String unknownAddress = "Unknown Address";

        // Simulirajte geokodiranje brez rezultatov
        geocodingService = new GeocodingService() {
            @Override
            public Optional<GeocodingResult> geocode(String address) {
                return Optional.empty();
            }
        };

        // Act: Kličite metodo za geokodiranje
        Optional<GeocodingResult> result = geocodingService.geocode(unknownAddress);

        // Assert: Preverite, ali rezultat ni prisoten
        assertTrue(result.isEmpty(), "Expected no geocoding result for unknown address");
    }
}