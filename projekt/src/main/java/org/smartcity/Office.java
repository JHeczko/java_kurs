package org.smartcity;

public class Office extends Building{
    private int employees;
    public Office(String adress, int floorsCount, int employees){
        super(adress,floorsCount);
        this.employees = employees;
    }
    public void operate(){
        System.out.println("Shop at " + getAdress() + " has " + getFloors() + " floors and " + " has: " + this.employees + " employees");
    }
    void setType(int employees){
        this.employees = employees;
    }
    int getType(){
        return this.employees;
    }
}
