package com.player_regression.core;

/**
 * Created by zlv on 29.04.16.
 */
public class ServerBoard {
    private TCPServer mServer;

    public ServerBoard() {

        //creates the object OnMessageReceived asked by the TCPServer constructor
        mServer = new TCPServer(new TCPServer.OnMessageReceived() {
            @Override
            //this method declared in the interface from TCPServer class is implemented here
            //this method is actually a callback method, because it will run every time when it will be called from
            //TCPServer class (at while)
            public void messageReceived(String message) {
                mServer.sendMessage(mServer.message());
            }
        });
        mServer.start();
    }
}

