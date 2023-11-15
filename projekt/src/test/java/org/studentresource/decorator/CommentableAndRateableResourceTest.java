package org.studentresource.decorator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.studentresource.Course;
import org.studentresource.StudentResource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentableAndRateableResourceTest {
    private StudentResource decoratedResource;

    @BeforeEach
    void setUp() {
        Course course = new Course("CS101", "Introduction to Computer Science");
        decoratedResource = new CommentableResource(course);
        decoratedResource = new RateableResource(decoratedResource);
    }

    @Test
    void testCommentAndRating() {
        // Access the specific functionalities of each decorator
        ((CommentableResource) ((RateableResource) decoratedResource).getDecoratedResource()).addComment("Very informative course");
        ((RateableResource) decoratedResource).setRating(5.0);

        String comment = ((CommentableResource) ((RateableResource) decoratedResource).getDecoratedResource()).getComment();
        double rating = ((RateableResource) decoratedResource).getRating();

        assertEquals("Very informative course", comment, "Comment should match");
        assertEquals(5.0, rating, "Rating should match");
    }
}
