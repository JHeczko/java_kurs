package org.smartcity;

public class Shop extends Building{
    private String type;
    public Shop(String adress, int floorsCount, String type){
        super(adress,floorsCount);
        this.type = type;
    }
    public void operate(){
        System.out.println("Shop at " + getAdress() + " has " + getFloors() + "floors and " + " is: " + this.type + " type");
    }
    void setType(String type){
        this.type = type;
    }
    String getType(){
        return this.type;
    }
}
