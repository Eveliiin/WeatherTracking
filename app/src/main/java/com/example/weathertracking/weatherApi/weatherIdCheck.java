package com.example.weathertracking.weatherApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class weatherIdCheck {

    private static int THUNDER =1;

/*
    dict singl RAIN rgb rgbé
    Snow RGB rGB*/
    private static int[] Thunder ={200,201,202,210,211,212,221,230,231,232};
    private static List thunder=Arrays.asList(Thunder);

    public static int checkId(int id){
        if(thunder.contains(id)){
          return 1;
        }
        return 1;
    }
}

