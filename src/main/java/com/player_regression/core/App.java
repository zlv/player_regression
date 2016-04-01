package com.player_regression.core;

import java.net.UnknownHostException;
import java.util.Date;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import java.net.*;
import java.io.*;
import java.util.Iterator;

/**
 * Java + MongoDB Hello world Example
 *
 */
public class App extends com.player_regression.core.Parser {
    public static void main(String[] args) {

        try {

            Integer id = 192;
            Object obj = parseJSON_init("http://api.eliteprospects.com/beta/leagues?limit=1&offset=0&filter=id%3D" + id.toString());
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

            /**** Get database ****/
            // if database doesn't exists, MongoDB will create it for you
            while (i.hasNext()) {
                Object recobj = i.next();
                if (recobj instanceof JSONObject) {
                    JSONObject rec = (JSONObject) recobj;
                    String v = (String)rec.get("name");
                    League league = new League(v,id);
                    league.parse(tableLeagues,tablePlayers);
                    System.out.println(v);
                }
            }
        } catch (MongoException e) {
            e.printStackTrace();
        } catch (ClassCastException cce) {
            System.out.println(cce);
        }
    }

}
