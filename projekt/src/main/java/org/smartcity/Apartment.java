package org.smartcity;

public class Apartment extends Building{
    private int residents;
    public Apartment(String adress, int floorsCount, int residents){
        super(adress,floorsCount);
        this.residents = residents;
    }
    public void operate(){
        System.out.println("Apartment at " + getAdress() + " has " + getFloors() + "floors and" + "has: " + this.residents + "residents");
    }
    void setType(int residents){
        this.residents = residents;
    }
    int getType(){
        return this.residents;
    }
}
