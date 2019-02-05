/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lucacalcaterra.melcloudclient;

import com.lucacalcaterra.melcloudclient.api.MelCloudClient;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.eclipse.jetty.client.HttpClient;

/**
 *
 * @author assist
 */
public class Main {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException, InterruptedException, TimeoutException, ExecutionException{
        // TODO code application logic here
        HttpClient httpClient = new HttpClient();
        MelCloudClient client;
        client = new MelCloudClient("https://app.melcloud.com/Mitsubishi.Wifi.Client/Login/ClientLogin/", "calcaterra.luca@gmail.com", "Mrpulsar-2", (short) 19, "1.17.5.0");
        client.doLogin();
    }
    
}
