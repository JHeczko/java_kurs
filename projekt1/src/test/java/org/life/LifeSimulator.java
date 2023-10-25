package org.life;

public class LifeSimulator {

  public static void main(String[] args) {
    Board board = new Board(10, 10);
    /* TODO:
     - Add at least 2 classes that implement the Organism interface.
     - The new classes should possess unique abilities, such as:
        -- Jumping (moving more than 1 step at a time).
        -- Sight (detecting other organisms within a certain radius).
        -- Avoiding illegal moves.
     - Ensure that an Organism doesn't move if it attempts an illegal move.
     - Implement a mechanism where an Organism consumes another (taking all its energy) when it occupies the same space.
     - Run simulation for some time, eg. when there is only one Organism left
     */
    Organism organism = new Organism(100);
    board.addOrganism(organism, 5, 5);
    organism.move();
  }
}