package org.smartcity;

abstract class Building {

    private int floorsCount;
    private String adress;
    public Building(String adress, int floorsCount){
        this.floorsCount = floorsCount;
        this.adress = adress;
    }
    abstract public void operate();

    public int getFloors(){
        return this.floorsCount;
    }
    public String getAdress(){
        return adress;
    }
}
