package com.player_regression.core;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.WriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.UpdateOptions;
import org.bson.BsonString;
import org.bson.Document;
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

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by zlv on 16.03.16.
 */
public class League extends com.player_regression.core.Parser {
    //common
    //DateTime updatedatetime
    ////
    String name_;
    Integer siteid_;
    public League(String name0, Integer siteid0) {
        name_ = name0;
        siteid_ = siteid0;
    }

    public void parse(MongoCollection<Document> tableLeagues, MongoCollection<Document> tablePlayers) {
        BasicDBObject query = new BasicDBObject();
        query.put("siteid", siteid_);
        BasicDBObject document = new BasicDBObject();
        document.put("name", name_);
        document.put("siteid", siteid_);
        FindOneAndUpdateOptions updateOptions = new FindOneAndUpdateOptions();
        updateOptions.upsert(true);
        Object leagueId = tableLeagues.findOneAndUpdate(document, new Document("$set", document), updateOptions).get("_id");

        JSONParser parser = new JSONParser();
        final int iLimit = 3;
        int iOffset = 0;

        Object obj = parseJSON_init(String.format("http://api.eliteprospects.com:80/beta/leagues/%d/players?limit=%d&offset=%d",siteid_,iLimit,iOffset));
        JSONObject jsonObj = (JSONObject) obj;
        JSONObject jsonObj1 = (JSONObject) jsonObj.get("metadata");
        long totalCount = (Long) jsonObj1.get("totalCount");
        if (totalCount < 1) return;
        JSONArray array = (JSONArray) jsonObj.get("data");
        Iterator i = array.iterator();
        while (i.hasNext()) {
            Object recobj = i.next();
            if (recobj instanceof JSONObject) {
                JSONObject rec = (JSONObject) recobj;
                try {
                    Player player = new Player(rec);
                    player.parse(tablePlayers, leagueId, siteid_);
                } catch (ValueCheckException e) {
                    System.out.println("Can't add record");
                    e.print_info();
                }
            }
        }
    }
}
