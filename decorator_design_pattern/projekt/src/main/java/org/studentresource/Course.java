package org.studentresource;

public class Course implements StudentResource {
    private String id;
    private String name;

    public Course(String id, String name){
        this.id = id;
        this.name = name;
    }
    @Override
    public String getName(){
        return this.name;
    }
    @Override
    public String getId(){
        return this.id;
    }
    @Override
    public void setName(String name){
        this.name = name;
    }
    public void setId(String id){
        this.id = id;
    }
    // Constructor, getters, setters
    // Implement all necessary methods from StudentResource
}