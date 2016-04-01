package com.player_regression.core;

import com.mongodb.BasicDBObject;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zlv on 29.03.16.
 */
class ParserWithDataLists extends com.player_regression.core.Parser {
    static final CheckOperation check_year_correct = (par) -> (Long)par>1900 && (Long)par<2116;
    NameAndDBName[] sLongParamNames_ = {};
    CheckOperation[] longCheckOperations_ = {};
    Map<String, Long> longData_;
    NameAndDBName[] sDoubleParamNames_ = {};
    CheckOperation[] doubleCheckOperations_ = {};
    Map<String, Double> doubleData_;
    NameAndDBName[] sSParamNames_ = {};
    CheckOperation[] sCheckOperations_ = {};
    Map<String, String> sData_;
    boolean bCheck = false;

    ParserWithDataLists() {
        bCheck = true;
        longData_ = new HashMap<>();
        doubleData_ = new HashMap<>();
        sData_ = new HashMap<>();
    }

    protected void init_datalists(JSONObject rec) throws ValueCheckException {
        try {
            put_and_check(sLongParamNames_,longCheckOperations_,longData_, rec);
            put_and_check(sDoubleParamNames_,doubleCheckOperations_,doubleData_, rec);
            put_and_check(sSParamNames_,sCheckOperations_,sData_, rec);
        }
        catch (ClassCastException e) {
            e.printStackTrace();
            bCheck = false;
        }
    }

    <T> T rec_get(JSONObject rec, String s) {
        String[] slist = s.split("\\.");

        int i;
        for (i = 0; i < slist.length - 1; ++i) {
            rec = (JSONObject) rec.get(slist[i]);
        }
        return (T) rec.get(slist[i]);

    }

    protected <T> void put_and_check(final NameAndDBName[] asParamNames, final CheckOperation[] aCheckOperations, Map<String, T> aData, JSONObject rec) throws ValueCheckException {
        T par;
        int index = 0;
        for (NameAndDBName si : asParamNames) {
            String s0;
            par = (T) rec_get(rec,si.name());
            if (par==null || !aCheckOperations[index++].operation(par)) {
                throw new ValueCheckException(si,par);
            }
            aData.put(si.dbname(), par);
        }
    }

    protected void parse(BasicDBObject document) throws ValueCheckException {
        if (!bCheck)
            throw new ValueCheckException();
        for (NameAndDBName si : sLongParamNames_) {
            document.put(si.dbname(), longData_.get(si.dbname()));
        }
        for (NameAndDBName si : sDoubleParamNames_) {
            document.put(si.dbname(), doubleData_.get(si.dbname()));
        }
        for (NameAndDBName si : sSParamNames_) {
            document.put(si.dbname(), sData_.get(si.dbname()));
        }
    }
}
