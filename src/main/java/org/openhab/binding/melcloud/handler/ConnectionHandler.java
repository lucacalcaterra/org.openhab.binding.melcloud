package org.openhab.binding.melcloud.handler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.io.net.http.HttpUtil;
import org.openhab.binding.melcloud.internal.MelCloudBindingConstants;
import org.openhab.binding.melcloud.json.LoginClientRes;
//import org.jsoup.Jsoup;
// import org.openhab.binding.riscocloud.json.ServerDatasHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class ConnectionHandler {
    final static Logger logger = LoggerFactory.getLogger(ConnectionHandler.class);
    private final static Gson gson = new Gson();
    // private static boolean isConnected = false;
    // private static boolean isError = false;
    private static String errorDesc;
    public LoginClientRes loginClientRes;

    public LoginResult Login(Configuration config) {
        LoginResult loginResult = new LoginResult();

        if (config.get(MelCloudBindingConstants.LOGIN_USERNAME) == null
                || config.get(MelCloudBindingConstants.LOGIN_PASS) == null) {
            errorDesc += " Parameter 'username' and 'webpass' must be configured.";
            loginResult.statusDescr = "Missing credentials";
        } else {
            try {
                // Document document = null;
                String loginResponse = null;
                JsonObject jsonReq = new JsonObject();
                jsonReq.addProperty("Email", (String) config.get(MelCloudBindingConstants.LOGIN_USERNAME));
                jsonReq.addProperty("Password", (String) config.get(MelCloudBindingConstants.LOGIN_PASS));
                jsonReq.addProperty("Language", (Number) config.get(MelCloudBindingConstants.LOGIN_LANG));
                jsonReq.addProperty("AppVersion", "1.17.5.0");
                jsonReq.addProperty("Persist", false);
                jsonReq.addProperty("CaptchaResponse", (String) null);

                // String content = MelCloudBindingConstants.WEBPASS;
                String content = jsonReq.toString();
                InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

                loginResponse = HttpUtil.executeUrl("POST", (String) config.get(MelCloudBindingConstants.LOGIN_URL),
                        null, stream, "application/json", 20000);
                logger.debug("loginPage=" + loginResponse);
                Gson gson = new Gson();
                loginClientRes = gson.fromJson(loginResponse, LoginClientRes.class);
                logger.debug("LoginClientRes assigned");

            } catch (IOException e) {
                loginResult.error += "Connection error to " + config.get(MelCloudBindingConstants.LOGIN_URL);
                loginResult.errorDetail = e.getMessage();
                loginResult.statusDescr = "@text/offline.uri-error-1";

            } catch (IllegalArgumentException e) {
                loginResult.error += "caught exception !";
                loginResult.errorDetail = e.getMessage();
                loginResult.statusDescr = "@text/offline.uri-error-2";
            }
        }
        return loginResult;
    }

}
