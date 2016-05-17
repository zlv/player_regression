package com.player_regression.core;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 * Created by zlv on 17.05.16.
 */
public class TCPServerBDProxy extends TCPServer {
    public TCPServerBDProxy(OnMessageReceived messageListener) {
        super(messageListener);
    }

    League get_league_data(Integer id, String name) {
        return get_league_data_from_db(id, name);
    }

    private League get_league_data_from_db(Integer id, String name) {
        try {
            MongoClient mongo = new MongoClient("localhost", 27017);
            MongoDatabase db = mongo.getDatabase("player_regression_db");
            MongoCollection<Document> tableLeagues = db.getCollection("leagues");
            MongoCollection<Document> tablePlayers = db.getCollection("players");
            League league = new League(name,id);
            league.get_from_db(tableLeagues,tablePlayers);
            return league;
        } catch (MongoException e) {
            e.printStackTrace();
        } catch (ClassCastException cce) {
            System.out.println(cce);
        }
        return null;
    }
}
