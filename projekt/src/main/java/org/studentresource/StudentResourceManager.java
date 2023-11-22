package org.studentresource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class NoElementInListException extends Exception{
    public NoElementInListException(String messege){
        super(messege);
    }
}

// This class should manage different student resources
public class StudentResourceManager<T extends StudentResource> {
    private ArrayList<T> resources;

    StudentResourceManager(){
        this.resources = new ArrayList<T>();
    }
    public void addResource(T resource){
        resources.add(resource);
    }
    public T getResource(String id){
        for(T el : resources){
                if(id.compareTo(el.getId()) == 0){
                    return el;
                }
        }
        return null;
    }
    public int findResource(String id){
        for(int i = 0; i<resources.size(); i++){
            if(resources.get(i).getId().compareTo(id) == 0){
                return i;
            }
        }
        return -1;
    }
    public void deleteResource(String id) throws NoElementInListException{
        int pos = findResource(id);
        if(pos != -1){
            resources.remove(pos);
        }
        else{
            throw new NoElementInListException("No element in list");
        }
    }
    public boolean isEmpty(){
        return resources.isEmpty();
    }

    // Implement methods to manage resources (add, remove, find, etc.)
}
