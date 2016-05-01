package com.player_regression.core;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by zlv on 16.03.16.
 */

class ValueCheckException  extends Exception {
    NameAndDBName si_;
    String spar_;

    public <T> ValueCheckException() {
    }

    public <T> ValueCheckException(NameAndDBName si, T par) {
        si_ = si;
        if (par!=null)
            spar_ = par.toString();
    }

    public void print_info() {
        System.out.println("parameter name: " + si_.toString());
        System.out.println("parameter value: " + spar_);
    }
}

class Player extends ParserWithDataLists {

    public Player() {
        sLongParamNames_ = new NameAndDBName[]{new NameAndDBName("yearOfBirth"), new NameAndDBName("id","siteid")};
        longCheckOperations_ = new CheckOperation[]{check_year_correct, (par) -> true};
        sDoubleParamNames_ = new NameAndDBName[]{new NameAndDBName("weight"), new NameAndDBName("height")};
        doubleCheckOperations_ = new CheckOperation[]{(par) -> (Double)par>8. && (Double)par<4086., (par) -> (Double)par>8. && (Double)par<380.};
        sSParamNames_ = new NameAndDBName[]{new NameAndDBName("dateOfBirth"), new NameAndDBName("lastName"), new NameAndDBName("playerPosition"), new NameAndDBName("playerGameStatus"), new NameAndDBName("firstName")};
        sCheckOperations_ = new CheckOperation[]{(par) -> ((String)par).length()>8 && ((String)par).length()<12, (par) -> true, (par) -> true, (par) -> true, (par) -> true};
    }

    public Player(JSONObject rec) throws ValueCheckException {
        super();
        init_datalists(rec);
    }

    public void parse(MongoCollection<Document> tablePlayers, Object leagueId, Integer siteid) throws ValueCheckException {
        BasicDBObject document = new BasicDBObject();
        parse(document);
        JSONParser parser = new JSONParser();

        String s = String.format("http://api.eliteprospects.com/beta/players/%d/stats?filter=league.id%%3D%d",longData_.get("siteid"),siteid);
        Object obj = parseJSON_init(s);
        JSONObject jsonObj = (JSONObject) obj;
        JSONObject jsonObj1 = (JSONObject) jsonObj.get("metadata");
        long totalCount = (Long) jsonObj1.get("totalCount");
        if (totalCount < 1) return;
        JSONArray array = (JSONArray) jsonObj.get("data");
        Iterator i = array.iterator();
        List<BasicDBObject> stats = new ArrayList<>();
        while (i.hasNext()) {
            Object recobj = i.next();
            if (recobj instanceof JSONObject) {
                JSONObject rec = (JSONObject) recobj;
                PlayerStats playerStats = new PlayerStats(rec);
                BasicDBObject statDoc = new BasicDBObject();
                playerStats.parse(statDoc);
                stats.add(statDoc);
            }
        }
        document.put("leagueId", leagueId);
        document.put("stats", stats);
        UpdateOptions updateOptions = new UpdateOptions();
        updateOptions.upsert(true);
        BasicDBObject query = new BasicDBObject();
        BasicDBObject keyDocument = key();
        tablePlayers.updateOne(keyDocument, new Document("$set", document), updateOptions);
    }

    private BasicDBObject key() {
        BasicDBObject document = new BasicDBObject();
        document.put("name", longData_.get("siteid"));
        document.put("firstName", sData_.get("firstName"));
        document.put("secondName", sData_.get("secondName"));
        return document;
    }

    public void get_from_db(Document player) {
        get_datalists_from_db(player);
        List stats = (List) player.get("stats");
        for (Object objstat : stats) {
            Document stat = (Document) objstat;
            PlayerStats playerStats = new PlayerStats();
            playerStats.get_datalists_from_db(stat);
        }
    }

    public Object json() {
JSONObject obj = new JSONObject();
json(obj);
        return obj;
    }
}
