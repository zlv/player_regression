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
    }1.0 1985.0 65.0 170.0 8.758906439165418
            1.0 2000.0 65.0 170.0 2.203132472773138
            1.0 1989.0 69.0 175.0 5.909598257282791
            1.0 1993.0 79.0 195.0 0.05923837625845607
            1.0 1983.0 79.0 195.0 4.429754353853234

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
