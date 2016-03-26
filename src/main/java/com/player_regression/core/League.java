package com.player_regression.core;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.WriteResult;
import com.mongodb.client.model.UpdateOptions;
import org.bson.BsonString;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by zlv on 16.03.16.
 */
public class League {
    //common
    //DateTime updatedatetime
    ////
    String name_;
    Integer siteid_;
    public League(String name0, Integer siteid0) {
        name_ = name0;
        siteid_ = siteid0;
    }

    public void parse(DBCollection tableLeagues, DBCollection tablePlayers) {
        BasicDBObject query = new BasicDBObject();
        query.put("siteid", siteid_);
        BasicDBObject document = new BasicDBObject();
        document.put("name", name_);
        document.put("siteid", siteid_);
        tableLeagues.update(query, document, true, false);
        Object leagueId = tableLeagues.findOne(query).get("_id");

        JSONParser parser = new JSONParser();
        final int iLimit = 3;
        int iOffset = 0;

        String sUrl = String.format("http://api.eliteprospects.com:80/beta/leagues/%d/players?limit=%d&offset=%d",siteid_,iLimit,iOffset);
        URL url = null;
        try {
            url = new URL(sUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        BufferedReader in = null;
        String s = "";
        try {
            in = new BufferedReader(new InputStreamReader(url.openStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null)
                s += inputLine + "\n";
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Object obj = null;
        try {
            obj = parser.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JSONObject jsonObj = (JSONObject) obj;
        JSONObject jsonObj1 = (JSONObject) jsonObj.get("metadata");
        long totalCount = (Long) jsonObj1.get("totalCount");
        if (totalCount < 1) return;
        JSONArray array = (JSONArray) jsonObj.get("data");
        Iterator i = array.iterator();
        while (i.hasNext()) {
            Object recobj = i.next();
            if (recobj instanceof JSONObject) {
                /*"yearOfBirth": 1985,
                        "dateOfBirth": "1985-02-12",
                        "lastName": "Pushkaryov",
                        "weight": 84,
                        "id": 9657,
                        "playerPosition": "FORWARD",
                        "playerGameStatus": "INJURED",
                        "height": 180,
                        "firstName": "Konstantin"*/
                JSONObject rec = (JSONObject) recobj;
                Player player = new Player(rec);
                player.parse(tablePlayers,leagueId);
                /*League league = new League(v,id);
                league.parse(tableLeagues,tablePlayers);
                System.out.println(v);*/
            }
        }
    }
}
