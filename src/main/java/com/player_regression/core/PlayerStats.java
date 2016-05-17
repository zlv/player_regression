package com.player_regression.core;

import com.mongodb.BasicDBObject;
import org.json.simple.JSONObject;

/**
 * Created by zlv on 29.03.16.
 */
class PlayerStats extends ParserWithDataLists {
    static final CheckOperation more_then_zero = (par) -> (Long)par>=0;
    public PlayerStats() {
        sLongParamNames_ = new NameAndDBName[]{new NameAndDBName("G"), new NameAndDBName("A"), new NameAndDBName("season.startYear","season_startYear"), new NameAndDBName("season.endYear","season_endYear")};
        longCheckOperations_ = new CheckOperation[]{more_then_zero, more_then_zero, check_year_correct, check_year_correct};
    }

    public PlayerStats(JSONObject rec) throws ValueCheckException {
        this();
        init_datalists(rec);
    }

    public double price() {
        String svalues[] = {"G","A"};
        Long values[] = new Long[2];
        int index = 0;
        Long sum = Long.valueOf(0);
        for (String s : svalues) {
            Long v = longData_.get(s);
            if (v==null)
                v = Long.valueOf(0);
            values[index] = v;
            sum += values[index++];
        }
        return sum;
    }
}
