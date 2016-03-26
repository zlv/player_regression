package com.player_regression.core;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zlv on 16.03.16.
 */
interface CheckOperation {
    boolean operation(Object a);
}

public class Player {
    final String[] sLongParamNames_ = {"yearOfBirth", "id"};
    final static CheckOperation[] longCheckOperations_ = {(par) -> (Long)par>1900 && (Long)par<2016, (par) -> true};
    Map<String, Long> longData_;
    final String[] sDoubleParamNames_ = {"weight", "height"};
    final static CheckOperation[] doubleCheckOperations_ = {(par) -> (Double)par>8. && (Double)par<4086., (par) -> (Double)par>8. && (Double)par<380.};
    Map<String, Double> doubleData_;
    final String[] sSParamNames_ = {"dateOfBirth", "lastName", "playerPosition", "playerGameStatus", "firstName"};
    Map<String, String> sData_;
    boolean bCheck = false;

    public Player(JSONObject rec) {
        longData_ = new HashMap<>();
        int index = 0;
        Long pa = 0L;
        bCheck = true;
        try {
            for (String si : sLongParamNames_) {
                pa = (Long) rec.get(si);
                if (!longCheckOperations_[index++].operation(pa)) {
                    bCheck = false;
                    break;
                }
                longData_.put(si, pa);
            }
            if (bCheck) {
                doubleData_ = new HashMap<>();
                for (String si : sDoubleParamNames_) {
                    doubleData_.put(si, (Double) rec.get(si));
                }
                sData_ = new HashMap<>();
                for (String si : sSParamNames_) {
                    sData_.put(si, (String) rec.get(si));
                }
            }
        }
        catch (ClassCastException e) {
            bCheck = false;
        }

    }

    public void parse(DBCollection tablePlayers, Object leagueId) {
        BasicDBObject document = new BasicDBObject();
        for (String si : sLongParamNames_) {
            document.put(si, longData_.get(si));
        }
        for (String si : sDoubleParamNames_) {
            document.put(si, doubleData_.get(si));
        }
        for (String si : sSParamNames_) {
            document.put(si, sData_.get(si));
        }
        document.put("leagueId", leagueId);
        BasicDBObject query = new BasicDBObject();
        query.put("id", longData_.get("id"));
        tablePlayers.update(query, document, true, false);
    }
}
