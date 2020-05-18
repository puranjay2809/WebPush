package model;

import org.json.JSONObject;

import java.io.Serializable;

public class Subscription implements Serializable {

    private String endpoint;
    private String p256dh;
    private String auth;

    public Subscription(String endpoint, String p256dh, String auth) {
        this.endpoint = endpoint;
        this.p256dh = p256dh;
        this.auth = auth;
    }

    public Subscription(String json) {
        JSONObject obj = new JSONObject(json);
        endpoint = obj.getString("endpoint");
        p256dh = obj.getString("key");
        auth = obj.getString("auth");
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getP256dh() {
        return p256dh;
    }

    public void setP256dh(String p256dh) {
        this.p256dh = p256dh;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }
}
