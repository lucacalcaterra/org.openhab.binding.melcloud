/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lucacalcaterra.melcloudclient.api;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.smarthome.io.net.http.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;

/**
 *
 * @author assist
 */
public class MelCloudClient {

    private final Logger logger = LoggerFactory.getLogger(MelCloudClient.class);

    private final HttpClient httpClient;
    private final String apiUrl;
    private final String username;
    private final String password;
    private final short langId;
    private final String appVersion;
    
    //private final Gson gson;
    private boolean isLoggedIn = false;

    public MelCloudClient(String apiUrl, String username, String password, short langId, String appVersion, HttpClient httpClient) {
        this.httpClient = httpClient;
        this.apiUrl = apiUrl;
        this.username = username;
        this.password = password;
        this.langId = langId;
        this.appVersion = appVersion;
        // this.gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        //        .registerTypeAdapter(UniFiClient.class, new UniFiClientDeserializer()).create();
    }

    public boolean doLogin() throws IOException {
        isLoggedIn = false;

        Request request = httpClient.newRequest(apiUrl).method(HttpMethod.POST).timeout(5000, TimeUnit.MILLISECONDS);
        request.header(HttpHeader.CONTENT_TYPE, "application/json");
        JsonObject loginParms = new JsonObject();
        loginParms.addProperty("Email", username);
        loginParms.addProperty("Password", password);
        loginParms.addProperty("Language", langId);
        loginParms.addProperty("Appversion", appVersion);
        loginParms.addProperty("CaptchaResponse", (String) null);
        loginParms.addProperty("Persist", false);
        request.content(new StringContentProvider(loginParms.toString()), "application/json");
        
        try {
        ContentResponse response = request.send();
        logger.debug ("response value is {}",response.getStatus());
        }
        catch (Exception e) {
            //logger.debug("Fatal transport error: {}", e.toString());
            throw new IOException(e);
        }
        
        return false;
    }

}
