package com.player_regression.core;

/**
 * Created by zlv on 29.03.16.
 */
public class NameAndDBName {
    String name_;
    String dbname_;

    NameAndDBName(String name) {
        dbname_ = name_ = name;
    }

    NameAndDBName(String name, String dbname) {
        dbname_ = dbname;
        name_ = name;
    }

    @Override
    public String toString() {
        return name() + " " + dbname();
    }

    public String name() {
        return name_;
    }

    public String dbname() {
        return dbname_;
    }
}
