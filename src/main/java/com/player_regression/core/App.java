package com.player_regression.core;

import java.net.UnknownHostException;
import java.util.Date;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Java + MongoDB Hello world Example
 *
 */
public class App {
    public static void main(String[] args) {

        try {
            JSONParser parser = new JSONParser();
            Integer id = 192;
            String sUrl = "http://api.eliteprospects.com/beta/leagues?limit=1&offset=0&filter=id%3D" + id.toString();
            URL url = new URL(sUrl);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(url.openStream()));

            String inputLine;
            String s = "";
            while ((inputLine = in.readLine()) != null)
                s += inputLine + "\n";
            in.close();

            Object obj = parser.parse(s);
            JSONObject jsonObj = (JSONObject) obj;
            JSONObject jsonObj1 = (JSONObject) jsonObj.get("metadata");
            long totalCount = (Long) jsonObj1.get("totalCount");
            if (totalCount < 1) return;
            JSONArray array = (JSONArray) jsonObj.get("data");
            Iterator i = array.iterator();

            MongoClient mongo = new MongoClient("localhost", 27017);
            DB db = mongo.getDB("player_regression_db");
            DBCollection tableLeagues = db.getCollection("leagues");
            DBCollection tablePlayers = db.getCollection("players");

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

            if (false) {
                /**** Connect to MongoDB ****/
                // Since 2.10.0, uses MongoClient

                /**** Get collection / table from 'testdb' ****/
                DBCollection table= db.getCollection("table");
                // if collection doesn't exists, MongoDB will create it for you

                /**** Insert ****/
                // create a document to store key and value

                /**** Find and display ****/
                BasicDBObject searchQuery = new BasicDBObject();
                searchQuery.put("name", "mkyong");

                DBCursor cursor = table.find(searchQuery);

                while (cursor.hasNext()) {
                    System.out.println(cursor.next());
                }

                /**** Update ****/
                // search document where name="mkyong" and update it with new values
                BasicDBObject query = new BasicDBObject();
                query.put("name", "mkyong");

                BasicDBObject newDocument = new BasicDBObject();
                newDocument.put("name", "mkyong-updated");

                BasicDBObject updateObj = new BasicDBObject();
                updateObj.put("$set", newDocument);

                table.update(query, updateObj);

                /**** Find and display ****/
                BasicDBObject searchQuery2
                        = new BasicDBObject().append("name", "mkyong-updated");

                DBCursor cursor2 = table.find(searchQuery2);

                while (cursor2.hasNext()) {
                    System.out.println(cursor2.next());
                }

                /**** Done ****/
                System.out.println("Done");
            }

        } catch (MongoException e) {
            e.printStackTrace();
        } catch (ParseException pe) {

            System.out.println("position: " + pe.getPosition());
            System.out.println(pe);
        } catch (IOException ioe) {

            System.out.println(ioe);
        } catch (ClassCastException cce) {
            System.out.println(cce);
        }
    }
}
