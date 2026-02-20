package org.studentresource.decorator;

import org.studentresource.StudentResource;

public class RateableResource extends ResourceDecorator{
    private double rate;

    public RateableResource(StudentResource resource){
        super(resource);
    }

    public double getRating(){
        return this.rate;
    }
    public void setRating(double rate){
        this.rate = rate;
    }
}
