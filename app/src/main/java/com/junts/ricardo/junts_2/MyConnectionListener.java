package com.junts.ricardo.junts_2;

import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;

/**
 * Created by ricardo on 02/03/16.
 */
public class MyConnectionListener implements ConnectionEventListener {
    @Override
    public void connectionClosed(ConnectionEvent theEvent) {

    }

    @Override
    public void connectionErrorOccurred(ConnectionEvent theEvent) {

    }
}
