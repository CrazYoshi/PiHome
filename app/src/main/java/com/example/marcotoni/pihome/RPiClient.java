package com.example.marcotoni.pihome;

import org.alexd.jsonrpc.*;

public class RPiClient {
    JSONRPCClient client;

    public RPiClient(){
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