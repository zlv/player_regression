package com.player_regression.core;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.WriteResult;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.UpdateOptions;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
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
import java.util.ArrayList;
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
    ArrayList<Player> players_;
    public League(String name0, Integer siteid0) {
        name_ = name0;
        siteid_ = siteid0;
    }

    public League(Integer siteid0) {
        siteid_ = siteid0;
    }

    public void parse(MongoCollection<Document> tableLeagues, MongoCollection<Document> tablePlayers, int iOffset, int iLimit) {
        BasicDBObject query = new BasicDBObject();
        query.put("siteid", siteid_);
        BasicDBObject document = new BasicDBObject();
        document.put("name", name_);
        document.put("siteid", siteid_);
        FindOneAndUpdateOptions updateOptions = new FindOneAndUpdateOptions();
        updateOptions.upsert(true);
        Object leagueId = tableLeagues.findOneAndUpdate(document, new Document("$set", document), updateOptions).get("_id");

        JSONParser parser = new JSONParser();

        Object obj = parseJSON_init(String.format("http://api.eliteprospects.com:80/beta/leagues/%d/players?limit=%d&offset=%d",siteid_,iLimit,iOffset));
        JSONObject jsonObj = (JSONObject) obj;
        JSONObject jsonObj1 = (JSONObject) jsonObj.get("metadata");
        long totalCount = (Long) jsonObj1.get("totalCount");
        if (totalCount < 1) return;
        JSONArray array = (JSONArray) jsonObj.get("data");
        Iterator i = array.iterator();
        players_ = new ArrayList<>();
        while (i.hasNext()) {
            Object recobj = i.next();
            if (recobj instanceof JSONObject) {
                JSONObject rec = (JSONObject) recobj;
                try {
                    Player player = new Player(rec);
                    player.parse(tablePlayers, leagueId, siteid_);
                    players_.add(player);
                } catch (ValueCheckException e) {
                    System.out.println("Can't add record");
                    e.print_info();
                }
            }
        }
    }

    public void get_from_db(MongoCollection<Document> tableLeagues, MongoCollection<Document> tablePlayers) {
        FindIterable<Document> result = tableLeagues.find(key());
        for (Document res : result) {
            Object leagueId = res.get("_id");
            FindIterable<Document> resultPlayers = tablePlayers.find(eq("leagueId",leagueId));
            players_ = new ArrayList<>();
            for (Document resPlayer : resultPlayers) {
                Player player = new Player();
                player.get_from_db(resPlayer);
                players_.add(player);
            }
            return; //one step
        }
    }

    private Document key() {
        Document request = new Document();
        request.append("siteid", siteid_);
        request.append("name", name_);
        return request;
    }

    public String json() {
        JSONArray list = new JSONArray();
        JSONObject obj = new JSONObject();
        for (Player player : players_) {
            list.add(player.json());
        }
        obj.put("data", list);
        return obj.toJSONString();
    }

    static String string_parameters(double[] x) {
        String result="";
        int j;
        for (j = 0; j < x.length-1; ++j) {
            result+=(new Double(x[j])).toString() + " ";
        }
        result+=(new Double(x[j])).toString();
        return result;
    }

    public void count_regression() {

        double[] y = new double[players_.size()];
        double[][] x = new double[players_.size()][];
        for (int i=0; i<players_.size(); ++i) {
            Player current = players_.get(i);
            x[i] = new double[current.par_number()];
            current.copy_parameters_to(x[i]);
            y[i] = current.price();
            System.out.println(current);
            System.out.println(string_parameters(x[i])+ " " + y[i]);
        }
        OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
        regression.newSampleData(y, x);
        double[] regressionParameters = regression.estimateRegressionParameters();
        for (int i=0; i<regressionParameters.length; ++i) {
            System.out.println(regressionParameters[i]);
        }
        System.out.println();
        double[][] sampleParameters = new double[5][];
        int index = 0;
        sampleParameters[index++] = new double[]{1,1985,65,170};
        sampleParameters[index++] = new double[]{1,2000,65,170};
        sampleParameters[index++] = new double[]{1,1989,69,175};
        sampleParameters[index++] = new double[]{1,1993,79,195};
        sampleParameters[index++] = new double[]{1,1983,79,195};
        for (int j=0; j<sampleParameters.length; ++j) {
            double price = 0;
            for (int i = 0; i < regressionParameters.length; ++i) {
                price += sampleParameters[j][i]*regressionParameters[i];
            }
            System.out.println(string_parameters(sampleParameters[j])+ " " + price);
        }
        System.out.println();
    }
}
