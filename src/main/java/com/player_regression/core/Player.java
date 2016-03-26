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

class ValueCheckException extends Exception {};

public class Player {
    final static String[] sLongParamNames_ = {"yearOfBirth", "id"};
    final static CheckOperation[] longCheckOperations_ = {(par) -> (Long)par>1900 && (Long)par<2116, (par) -> true};
    Map<String, Long> longData_;
    final static String[] sDoubleParamNames_ = {"weight", "height"};
    final static CheckOperation[] doubleCheckOperations_ = {(par) -> (Double)par>8. && (Double)par<4086., (par) -> (Double)par>8. && (Double)par<380.};
    Map<String, Double> doubleData_;
    final static String[] sSParamNames_ = {"dateOfBirth", "lastName", "playerPosition", "playerGameStatus", "firstName"};
    final static CheckOperation[] sCheckOperations_ = {(par) -> ((String)par).length()>8 && ((String)par).length()<12, (par) -> true, (par) -> true, (par) -> true, (par) -> true};
    Map<String, String> sData_;
    boolean bCheck = false;

    <T> void put_and_check(final String[] asParamNames, final CheckOperation[] aCheckOperations, Map<String, T> aData, JSONObject rec) throws ValueCheckException {
        T par;
        int index = 0;
        for (String si : asParamNames) {
            par = (T) rec.get(si);
            if (!aCheckOperations[index++].operation(par)) {
                throw new ValueCheckException();
            }
            aData.put(si, par);
        }
    }

    public Player(JSONObject rec) {
        bCheck = true;
        try {
            longData_ = new HashMap<>();
            put_and_check(sLongParamNames_,longCheckOperations_,longData_, rec);
            doubleData_ = new HashMap<>();
            put_and_check(sDoubleParamNames_,doubleCheckOperations_,doubleData_, rec);
            sData_ = new HashMap<>();
            put_and_check(sSParamNames_,sCheckOperations_,sData_, rec);
        }
        catch (ClassCastException e) {
            e.printStackTrace();
            bCheck = false;
        } catch (ValueCheckException e) {
            e.printStackTrace();
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
