package org.studentresource.decorator;

import org.studentresource.StudentResource;

// This class should allow adding comments to the resource
public class CommentableResource extends ResourceDecorator {

    public CommentableResource(StudentResource decoratedResource) {
        super(decoratedResource);
    }

    // Implement commenting features
}
