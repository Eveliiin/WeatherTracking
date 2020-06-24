package com.example.weathertracking.weatherApi.weatherEffects;

import android.content.Context;
import android.graphics.Color;

import com.example.weathertracking.R;
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

    public WeathersFromJson(Context context) {
        this.context=context;
        try {
            readJsonStream(context);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static WeathersFromJson getweathers(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new WeathersFromJson(context);
        }
        return(INSTANCE);
    }
    public  int getStartHex( String code){
        int startHex= 0xffffffff;
        if(map!=null && map.get(code)!=null){
            if(null!= map.get(code).startHex){
                startHex = Color.parseColor(map.get(code).startHex);
            }
        }
        return startHex;

    }
    public  int getendHex( String code){
        int endHex= 0xff286e84;
        if(map!=null && map.get(code)!=null){
            if(null!= map.get(code).endHex){
                endHex =Color.parseColor(map.get(code).endHex);
            }
        }
        return endHex;

    }
    public int getSnow(String code){
        int snow= 0;
        if(map!=null && map.get(code)!=null){
            if(null!= map.get(code).endHex){
                snow =map.get(code).snow;
            }
        }
        return snow;
    }
    public int getRain(String code){
        int rain= 0;
        if(map!=null && map.get(code)!=null){
            if(null!= map.get(code).endHex){
                rain =map.get(code).rain;
            }
        }
        return rain;
    }

    private static void readJsonStream(Context context) throws IOException {


        String jsonString=getJsonString(context);

        ObjectMapper mapper = new ObjectMapper();

        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        JsonNode jsonNode = mapper.readTree(jsonString);

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
}
