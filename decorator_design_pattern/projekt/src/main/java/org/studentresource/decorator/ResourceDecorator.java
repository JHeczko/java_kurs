package org.studentresource.decorator;

import org.studentresource.StudentResource;

public abstract class ResourceDecorator implements StudentResource {
    protected StudentResource decoratedResource;

    public ResourceDecorator(StudentResource decoratedResource) {
        this.decoratedResource = decoratedResource;
    }

    public StudentResource getDecoratedResource(){
        return this.decoratedResource;
    }
    @Override
    public String getName() {
        return this.decoratedResource.getName();
    }
    @Override
    public String getId(){
        return this.decoratedResource.getId();
    }
    @Override
    public void setName(String name){
        this.decoratedResource.setName(name);
    }
    @Override
    public void setId(String id){
        this.decoratedResource.setId(id);
    }
    // Implement all necessary methods from StudentResource
    // Override methods to add additional behaviors
}
