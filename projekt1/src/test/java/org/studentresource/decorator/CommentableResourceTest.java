package org.studentresource.decorator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.studentresource.Course;
import org.studentresource.decorator.CommentableResource;

import static org.junit.jupiter.api.Assertions.*;

class CommentableResourceTest {
    private CommentableResource commentableResource;

    @BeforeEach
    void setUp() {
        Course course = new Course("CS101", "Introduction to Computer Science");
        commentableResource = new CommentableResource(course);
    }

    @Test
    void addAndGetCommentTest() {
        String comment = "Excellent course for beginners.";
        commentableResource.addComment(comment);

        assertEquals(comment, commentableResource.getComment(), "The comment should match the added one.");
    }
}
