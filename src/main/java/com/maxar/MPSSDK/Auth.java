package com.maxar.MPSSDK;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.URI;
import java.util.HashMap;
import java.util.Objects;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

class Auth {

    private final String baseUrl;
    private final String apiBaseUrl;
    private final String username;
    private final String password;
    private final String clientId;
    private String access;
    private String refresh;
    private final String version;


    Auth(HashMap<String, String> credentials) throws IllegalArgumentException {
        this.baseUrl = "https://account.maxar.com";
        this.apiBaseUrl = "https://api.maxar.com";
        this.access = null;
        this.refresh = null;
        this.version = "Java_0.1.0";
        //XOR operator to make sure that not only username or password or clientid passed in
        if ((credentials.get("username") != null ^ credentials.get("password") != null) ||
            (credentials.get("clientId") != null ^ credentials.get("username") != null) ||
            (credentials.get("clientId") != null ^ credentials.get("password") != null)) {
            throw new IllegalArgumentException("Must pass in both a username and password");
        }
        //If credentials are passed
        else if (credentials.get("username") != null) {
            this.username = credentials.get("username");
            this.password = credentials.get("password");
            this.clientId = credentials.get(("clientId"));
        } else {
            //Read from MPS-config
            String homeDirectory = System.getProperty("user.home");
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(homeDirectory +
                    "/.MPS-config"));
            } catch (FileNotFoundException e) {
                throw new IllegalArgumentException(".MPS-Config file not found in user home directory");
            }
            //skip first line
            try {
                reader.readLine();
                this.username = reader.readLine().substring(10);
                this.password = reader.readLine().substring(14);
                this.clientId = reader.readLine().substring(10);
            } catch (IOException e) {
                throw new IllegalArgumentException(".MPS-Config file not formatted correctly");
            }

        }
        this.refreshToken();
    }

    String getApiBaseUrl() {
        return apiBaseUrl;
    }

    String getVersion() {
        return version;
    }

    String refreshToken() {

        /*
            Function takes in refresh token and generates a new access token and refresh token
         */
        if (this.refresh != null) {
            String URL = String.format("%s/auth/realms/mds/protocol/openid-connect/token", this.baseUrl);
            String payload = String.format("grant_type=refresh_token&refresh_token=%s&client_id=mds"
                    + "-internal-service",
                    this.refresh);
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest postRequest = HttpRequest.newBuilder(
                            URI.create(URL))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Authorization", String.format("Bearer %s", this.refresh))
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();
            HttpResponse<String> httpResponse;

            try {
                httpResponse = httpClient.send(postRequest, BodyHandlers.ofString());
            }
            catch (IOException | InterruptedException e) {
                return "API error please try again: " + e.toString();
            }
            JsonObject gsonResponse = new Gson().fromJson(httpResponse.body(), JsonObject.class);

            if (httpResponse.statusCode() == 400 && Objects.equals(String.valueOf(gsonResponse
                .get("error_description")), "Token is not active")) {
                return this.getAuth();
            } else if (httpResponse.statusCode() != 200) {
                System.out.println("Error. Status code = " + httpResponse.statusCode() + " " +
                    httpResponse.body());
                return null;
            } else {
                this.access = String.valueOf(gsonResponse.get("access_token")).replaceAll(
                    "\"", "");
                this.refresh = String.valueOf(gsonResponse.get("refresh_token")).replaceAll(
                    "\"", "");
                return this.access;
            }
        } else {
            return this.getAuth();
        }
    }

    private String getAuth() {

        /*
            Function generates an access token and refresh token based on a username and password combination
         */

        String URL = String.format("%s/auth/realms/mds/protocol/openid-connect/token", this.baseUrl);
        String payload = String.format("client_id=%s&username=%s&password=%s&grant"
            + "_type=password&scope=openid", this.clientId, this.username, this.password);
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest postRequest = HttpRequest.newBuilder(
                        URI.create(URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();
        HttpResponse<String> httpResponse = null;
        try {
            httpResponse = httpClient.send(postRequest, BodyHandlers.ofString());
        }
        catch (IOException | InterruptedException e) {
            return "API error please try again: " + e.toString();
        }
        JsonObject gsonResponse = new Gson().fromJson(httpResponse.body(), JsonObject.class);

        if (httpResponse.statusCode() != 200) {
            if (httpResponse.statusCode() == 400 && httpResponse.body().contains("Invalid client "
                + "credentials")) {
                throw new IllegalArgumentException("Authentication Error: Invalid User Credentials");
            }
            else if (httpResponse.statusCode() == 400 && httpResponse.body().contains("Account "
                + "disabled")) {
                System.out.println("Authentication Error:Account Disabled");
            } else {
                System.out.println("Error: " + httpResponse.body());
            }
            System.exit(10);
        } else {
            this.access = String.valueOf(gsonResponse.get("access_token")).replaceAll("\"",
                "");
            this.refresh = String.valueOf(gsonResponse.get("refresh_token")).replaceAll("\"",
                "");
            return this.access;
        }
        return null;
    }


}
