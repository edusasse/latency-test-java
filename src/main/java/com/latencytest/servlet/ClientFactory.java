package com.latencytest.servlet;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

public class ClientFactory extends BasePooledObjectFactory<Client> {

    @Override
    public Client create() throws Exception {
        return ClientBuilder.newClient();
    }

    @Override
    public boolean validateObject(PooledObject<Client> p) {
        // Simple validation, check if client can connect
        return true;
    }

    @Override
    public PooledObject<Client> wrap(Client client) {
        return new DefaultPooledObject<>(client);
    }

    @Override
    public void destroyObject(PooledObject<Client> p) throws Exception {
        p.getObject().close(); // Close client connection
    }
}