package com.ris.todoapp.microsoft;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;

public class MicrosoftCalendarService {

    private static final String GRAPH_API_BASE_URL = "https://graph.microsoft.com/v1.0/me/events";
    private final String accessToken;

    /**
     * Konstruktor za inicializacijo MicrosoftCalendarService.
     *
     * @param accessToken Dostopni žeton za Microsoft Graph API
     */
    public MicrosoftCalendarService(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Ustvari dogodek v Microsoft Outlook Calendar in vrne ID dogodka.
     *
     * @param subject       Naslov dogodka
     * @param startDateTime Začetni čas dogodka (ISO 8601 format, npr. "2024-12-16T10:00:00")
     * @param endDateTime   Končni čas dogodka (ISO 8601 format, npr. "2024-12-16T11:00:00")
     * @param body          Opis dogodka
     * @return ID ustvarjenega dogodka
     * @throws IOException Če pride do napake pri klicu API
     */
    public String createEvent(String subject, String startDateTime, String endDateTime, String body) throws IOException {
        // OkHttpClient za HTTP klice
        OkHttpClient client = new OkHttpClient();

        // Priprava podatkov za dogodek v JSON formatu
        String eventJson = String.format("""
                {
                  "subject": "%s",
                  "start": {
                    "dateTime": "%s",
                    "timeZone": "UTC"
                  },
                  "end": {
                    "dateTime": "%s",
                    "timeZone": "UTC"
                  },
                  "body": {
                    "contentType": "HTML",
                    "content": "%s"
                  }
                }
                """, subject, startDateTime, endDateTime, body);

        // Priprava HTTP POST zahteve
        Request request = new Request.Builder()
                .url(GRAPH_API_BASE_URL)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(eventJson, MediaType.parse("application/json")))
                .build();

        // Pošlji zahtevo in obdelaj odgovor
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Napaka pri ustvarjanju dogodka: " + response.code() + " - " + response.message());
            }

            // Pretvori odgovor v JSON
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonResponse = mapper.readTree(response.body().string());

            // Vrni ID dogodka
            return jsonResponse.get("id").asText();
        }
    }

    /**
     * Izbriše dogodek iz Microsoft Outlook Calendar.
     *
     * @param eventId ID dogodka za brisanje
     * @throws IOException Če pride do napake pri klicu API
     */
    public void deleteEvent(String eventId) throws IOException {
        // OkHttpClient za HTTP klice
        OkHttpClient client = new OkHttpClient();

        // Priprava HTTP DELETE zahteve
        Request request = new Request.Builder()
                .url(GRAPH_API_BASE_URL + "/" + eventId)
                .addHeader("Authorization", "Bearer " + accessToken)
                .delete()
                .build();

        // Pošlji zahtevo in obdelaj odgovor
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Napaka pri brisanju dogodka: " + response.code() + " - " + response.message());
            }
            System.out.println("Dogodek uspešno izbrisan.");
        }
    }
}