package org.studentresource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.studentresource.decorator.CommentableResource;

import static org.junit.jupiter.api.Assertions.*;

class StudentResourceManagerTest {
    private StudentResourceManager<Course> manager;

    @BeforeEach
    void setUp() {
        manager = new StudentResourceManager<>();
    }

    @Test
    void addAndRetrieveResourceTest() {
        Course course = new Course("CS101", "Introduction to Computer Science");
        CommentableResource courseCommented = new CommentableResource(course);
        courseCommented.addComment("Welcome from commented course");
        manager.addResource(course);

        Course retrieved = manager.getResource("CS101");
        assertNotNull(retrieved, "Retrieved course should not be null.");
        assertEquals("Introduction to Computer Science", retrieved.getName(), "Course name should match.");
    }
    @Test
    void findTest(){
        Course course = new Course("CS101", "Introduction to Computer Science");
        CommentableResource courseCommented = new CommentableResource(course);

        courseCommented.addComment("Welcome from commented course");

        manager.addResource(course);

        assertEquals(-1, manager.findResource("Cos"), "Shouldn't find a course");
        assertEquals(0,manager.findResource("CS101"), "Should give correct position of test element");
    }
    @Test
    void removeResourceTest() {
        Course course = new Course("CS101", "Introduction to Computer Science");
        CommentableResource courseCommented = new CommentableResource(course);

        courseCommented.addComment("Welcome from commented course");

        manager.addResource(course);

        try{
            manager.deleteResource("CS101");
        }
        catch(NoElementInListException e){
            System.out.println(e.getMessage());
        }

        assertTrue(manager.isEmpty(), "Maneger should be empty ");
    }

    // Add more tests to cover all functionalities
}
