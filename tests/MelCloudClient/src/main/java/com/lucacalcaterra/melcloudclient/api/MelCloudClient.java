/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lucacalcaterra.melcloudclient.api;

import org.eclipse.jetty.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.util.ssl.SslContextFactory;

/**
 *
 * @author assist
 */
public class MelCloudClient {

    private final Logger logger = LoggerFactory.getLogger(MelCloudClient.class);

    private HttpClient httpClient;
    private final String apiUrl;
    private final String username;
    private final String password;
    private final short langId;
    private final String appVersion;
    
    //private final Gson gson;
    private boolean isLoggedIn = false;

    public MelCloudClient(String apiUrl, String username, String password, short langId, String appVersion) {
        this.httpClient = httpClient;
        this.apiUrl = apiUrl;
        this.username = username;
        this.password = password;
        this.langId = langId;
        this.appVersion = appVersion;
        // this.gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        //        .registerTypeAdapter(UniFiClient.class, new UniFiClientDeserializer()).create();
    }

    public boolean doLogin()  {
        isLoggedIn = false;

        httpClient = new HttpClient(new SslContextFactory());
        try {
            httpClient.start();
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(MelCloudClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        ContentResponse response;
        try {
            response = httpClient
                    .POST(apiUrl)
                    //.header(HttpHeader.ACCEPT, "application/json")
                    .header(HttpHeader.CONTENT_TYPE, "application/json")
                    .content(new StringContentProvider("{\"Email\":\"calcaterra.luca@gmail.com\",\"Password\":\"Mrpulsar-2\",\"Language\":19,\"AppVersion\":\"1.17.5.0\",\"Persist\":false,\"CaptchaResponse\":null}"), "utf-8")
                    .send();
             logger.info("Connection done");
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(MelCloudClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            java.util.logging.Logger.getLogger(MelCloudClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TimeoutException ex) {
            java.util.logging.Logger.getLogger(MelCloudClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            httpClient.stop();
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(MelCloudClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

}
