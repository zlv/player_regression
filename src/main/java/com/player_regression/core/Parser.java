package com.player_regression.core;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by zlv on 28.03.16.
 */
public class Parser {
    protected static Object parseJSON_init(String sUrl) {
        JSONParser parser = new JSONParser();
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
        } catch (ParseException pe) {
            System.out.println("position: " + pe.getPosition());
            System.out.println(pe);
        }
        return obj;
    }
}
