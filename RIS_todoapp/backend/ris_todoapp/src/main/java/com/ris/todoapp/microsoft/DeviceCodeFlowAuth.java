package com.ris.todoapp.microsoft;

import com.microsoft.aad.msal4j.*;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class DeviceCodeFlowAuth {

    private final String clientId; // Client ID za aplikacijo
    private final Set<String> scopes; // Scopes za Microsoft Graph API

    /**
     * Konstruktor za inicializacijo Device Code Flow avtentikacije.
     *
     * @param clientId Client ID aplikacije v Azure AD
     * @param scopes   Zahtevana dovoljenja (npr. "Calendars.ReadWrite")
     */
    public DeviceCodeFlowAuth(String clientId, Set<String> scopes) {
        this.clientId = clientId;
        this.scopes = scopes;
    }

    /**
     * Pridobi dostopni žeton z uporabo Device Code Flow.
     *
     * @return Dostopni žeton kot String
     * @throws Exception Če pride do napake pri avtentikaciji
     */
    public String getAccessToken() throws Exception {
        // Ustvari MSAL javnega klienta za Device Code Flow
        PublicClientApplication pca = PublicClientApplication.builder(clientId).build();

        // Device Code Flow - generiraj kodo za avtorizacijo
        CompletableFuture<IAuthenticationResult> future = pca.acquireToken(
                DeviceCodeFlowParameters.builder(scopes, deviceCode -> {
                    // Izpis informacij za uporabnika
                    System.out.println("Prijavite se na naslednji URL: " + deviceCode.verificationUri());
                    System.out.println("Uporabite naslednjo kodo: " + deviceCode.userCode());
                    System.out.println("Ko zaključite prijavo, se bo postopek nadaljeval.");
                }).build()
        );

        // Pridobi rezultat avtentikacije
        IAuthenticationResult result = future.get();
        return result.accessToken();
    }
}