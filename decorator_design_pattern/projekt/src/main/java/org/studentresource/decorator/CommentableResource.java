package org.studentresource.decorator;

import org.studentresource.StudentResource;

// This class should allow adding comments to the resource
public class CommentableResource extends ResourceDecorator implements StudentResource{

    private String comment;
    public CommentableResource(StudentResource decoratedResource) {
        super(decoratedResource);
    }

    public void addComment(String comment){
        this.comment = comment;
    }
    public String getComment(){
        return this.comment;
    }
    // Implement commenting features
}
