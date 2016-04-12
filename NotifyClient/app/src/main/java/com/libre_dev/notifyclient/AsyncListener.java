package com.libre_dev.notifyclient;

import org.json.JSONObject;

/**
 * Created by dalew on 2/17/16.
 */
public interface AsyncListener {
    public void asyncResult(int type, int status, JSONObject json);
}
