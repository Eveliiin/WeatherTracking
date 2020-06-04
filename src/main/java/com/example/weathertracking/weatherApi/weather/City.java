package com.example.weathertracking.weatherApi.weather;

public class City{

    public int id;
    public String name;
    public Coord coord ;
    public String country ;

    public City(String id, String name, String country){
        this.id= Integer.parseInt(id);
        this.name=name;
        this.country=country;
    }

}
