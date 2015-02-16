package com.example.marcotoni.pihome;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.alexd.jsonrpc.*;

public class RPiClient {
    JSONRPCClient client;

    public RPiClient(){
    }

    public RPiClient(Context context)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String url = sharedPreferences.getString("example_server_address", "http://localhost") + ":" + sharedPreferences.getString("example_server_port", "8080");
        client = JSONRPCClient.create(url, JSONRPCParams.Versions.VERSION_2);
        client.setConnectionTimeout(2000);
        client.setSoTimeout(2000);
    }

    public RPiClient(String url)
    {
        client = JSONRPCClient.create(url, JSONRPCParams.Versions.VERSION_2);
        client.setConnectionTimeout(2000);
        client.setSoTimeout(2000);
    }

    public Object sendRequest(String method,Object...params)
    {
        Object d = null;
        try{ d = client.call(method,params); }
        catch (JSONRPCException e) { e.printStackTrace(); }
        finally {
            return d;
        }

    }
}