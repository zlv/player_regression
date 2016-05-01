package com.player_regression.core;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;

/**
 * The class extends the Thread class so we can receive and send messages at the same time
 */
public class TCPServer extends Thread {

    public static final int SERVERPORT = 4444;
    private boolean running = false;
    private PrintWriter mOut;
    private OnMessageReceived messageListener;

    public static void main(String[] args) {
        ServerBoard board = new ServerBoard();
    }

    /**
     * Constructor of the class
     * @param messageListener listens for the messages
     */
    public TCPServer(OnMessageReceived messageListener) {
        this.messageListener = messageListener;
    }

    /**
     * Method to send the messages from server to client
     * @param message the message sent by the server
     */
    public void sendMessage(String message){
        if (mOut != null && !mOut.checkError()) {
            mOut.println(message);
            mOut.flush();
        }
    }

    @Override
    public void run() {
        super.run();

        running = true;

        try {
            System.out.println("S: Connecting...");
            System.out.println(message());

            //create a server socket. A server socket waits for requests to come in over the network.
            ServerSocket serverSocket = new ServerSocket(SERVERPORT);

            //create client socket... the method accept() listens for a connection to be made to this socket and accepts it.
            Socket client = serverSocket.accept();
            System.out.println("S: Receiving...");

            try {

                //sends the message to the client
                mOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);

                //read the message received from client
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                //in this while we wait to receive messages from client (it's an infinite loop)
                //this while it's like a listener for messages
                while (running) {
                    String message = in.readLine();

                    if (message != null && messageListener != null) {
                        //call the method messageReceived from ServerBoard class
                        messageListener.messageReceived(message);
                    }
                }

            } catch (Exception e) {
                System.out.println("S: Error");
                e.printStackTrace();
            } finally {
                client.close();
                System.out.println("S: Done.");
            }

        } catch (Exception e) {
            System.out.println("S: Error");
            e.printStackTrace();
        }

    }

    public String message() {
        Integer id = 192;
        String name = "KHL";
        //get_data_from_api("http://api.eliteprospects.com/beta/leagues?limit=1&offset=0&filter=id%3D" + id.toString(),id);
        return get_league_data_from_db(id,name);
    }

    public interface OnMessageReceived {
        public void messageReceived(String message);
    }

    void get_data_from_api(String sapi, Integer id) {
        try {
            Parser parser = new Parser();
            Object obj = parser.parseJSON_init(sapi);
            JSONObject jsonObj = (JSONObject) obj;
            JSONObject jsonObj1 = (JSONObject) jsonObj.get("metadata");
            long totalCount = (Long) jsonObj1.get("totalCount");
            if (totalCount < 1) return;
            JSONArray array = (JSONArray) jsonObj.get("data");
            Iterator i = array.iterator();

            MongoClient mongo = new MongoClient("localhost", 27017);
            MongoDatabase db = mongo.getDatabase("player_regression_db");
            MongoCollection<Document> tableLeagues = db.getCollection("leagues");
            MongoCollection<Document> tablePlayers = db.getCollection("players");

            while (i.hasNext()) {
                Object recobj = i.next();
                if (recobj instanceof JSONObject) {
                    JSONObject rec = (JSONObject) recobj;
                    String v = (String)rec.get("name");
                    League league = new League(v,id);
                    league.parse(tableLeagues,tablePlayers, 30, 10);
                }
            }
        } catch (MongoException e) {
            e.printStackTrace();
        } catch (ClassCastException cce) {
            System.out.println(cce);
        }

    }

    private String get_league_data_from_db(Integer id, String name) {
        try {
            MongoClient mongo = new MongoClient("localhost", 27017);
            MongoDatabase db = mongo.getDatabase("player_regression_db");
            MongoCollection<Document> tableLeagues = db.getCollection("leagues");
            MongoCollection<Document> tablePlayers = db.getCollection("players");
            League league = new League(name,id);
            league.get_from_db(tableLeagues,tablePlayers);
            return league.json();
        } catch (MongoException e) {
            e.printStackTrace();
        } catch (ClassCastException cce) {
            System.out.println(cce);
        }
        return "";
    }
}

