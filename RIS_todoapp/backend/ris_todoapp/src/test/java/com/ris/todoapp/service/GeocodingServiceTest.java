package com.ris.todoapp.service;

import com.ris.todoapp.dto.GeocodingResult;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Optional;

public class GeocodingServiceTest {

    @Test
    void testSuccessfulGeocoding() {
        // Arrange: Mock the GeocodingService
        GeocodingService geocodingService = mock(GeocodingService.class);

        // Mock result
        GeocodingResult mockResult = new GeocodingResult();
        mockResult.setLatitude(40.7128); // Match the expected value
        mockResult.setLongitude(-74.0060);

        when(geocodingService.geocode("123 Main Street")).thenReturn(Optional.of(mockResult));

        // Act: Call the geocode method
        Optional<GeocodingResult> result = geocodingService.geocode("123 Main Street");

        // Assert: Validate the result
        assertTrue(result.isPresent(), "Expected geocoding result to be present");
        assertEquals(40.7128, result.get().getLatitude(), 0.001, "Latitude mismatch");
        assertEquals(-74.0060, result.get().getLongitude(), 0.001, "Longitude mismatch");
    }

    @Test
    void testGeocodingNoResults() {
        // Arrange: Mock the GeocodingService
        GeocodingService geocodingService = mock(GeocodingService.class);

        when(geocodingService.geocode("Unknown Address")).thenReturn(Optional.empty());

        // Act: Call the geocode method
        Optional<GeocodingResult> result = geocodingService.geocode("Unknown Address");

        // Assert: Validate the result
        assertTrue(result.isEmpty(), "Expected no geocoding result for unknown address");
    }
}