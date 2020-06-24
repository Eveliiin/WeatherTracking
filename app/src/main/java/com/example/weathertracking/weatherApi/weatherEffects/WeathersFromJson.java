package com.example.weathertracking.weatherApi.weatherEffects;

import android.content.Context;

import com.example.weathertracking.R;
import com.example.weathertracking.weatherApi.weather.Weather;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeathersFromJson {

    private static WeathersFromJson INSTANCE = null;
    private  Context context;
    private static Map<String, WeatherColours> map;

    private WeathersFromJson(Context context) {
        this.context=context;
        getWeatherConditions(context);

    }

    public static WeathersFromJson getweathers(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new WeathersFromJson(context);
        }
        return(INSTANCE);
    }
    public static String getStartHex( String code){
        String startHex= "#ffffff";
        map.clear();
        if(map!=null && map.get(code)!=null){
            if(null != map.get(code).startHex){
                startHex =map.get(code).startHex;
            }
        }
        return startHex;

    }
    public static String getendHex( String code){
        String endHex= "#286e84";
        map.clear();
        if(map!=null && map.get(code)!=null){
            if(null != map.get(code).endHex){
                endHex =map.get(code).endHex;
            }
        }
        return endHex;

    }
    public static String getStartHex(String code,String icon){
        String startHex= "#ffffff";
        code= code+ icon.substring(icon.length() - 1);
        map.clear();
        if(map!=null && map.get(code)!=null){
            if(null != map.get(code).startHex){
                startHex =map.get(code).startHex;
            }
        }
        return startHex;

    }
    public static String getEndHex(String code,String icon){
        String endHex= "#286e84";
        code= code+ icon.substring(icon.length() - 1);
        map.clear();
        if(map!=null && map.get(code)!=null){
            if(null != map.get(code).endHex){
                endHex =map.get(code).endHex;
            }
        }
        return endHex;

    }

    public static void readJsonStream(Context context) throws IOException {


        String jsonString=getJsonString(context);

        ObjectMapper mapper = new ObjectMapper();

        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        JsonNode jsonNode = mapper.readTree(jsonString);


        /*for(int i=0; i<=jsonNode.size();i++){

        }*/
        List<WeatherColours> myPojoList = mapper.readerFor(new TypeReference<List<WeatherColours>>(){}).readValue(jsonNode);


        map = new HashMap<>();
        for (WeatherColours i : myPojoList) map.put(i.code,i);
   }
   private static String getJsonString(Context context){
       InputStream is = context.getResources().openRawResource(R.raw.weather);
       Writer writer = new StringWriter();
       char[] buffer = new char[1024];
       try {
           Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
           int n;
           while ((n = reader.read(buffer)) != -1) {
               writer.write(buffer, 0, n);
           }
       } catch (UnsupportedEncodingException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       } finally {
           try {
               is.close();
           } catch (IOException e) {
               e.printStackTrace();
           }
       }

       String jsonString = writer.toString();
       return jsonString;
   }

    public static void getWeatherConditions(Context context) {
        try {
            readJsonStream(context);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
