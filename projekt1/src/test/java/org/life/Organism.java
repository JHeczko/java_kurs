package org.life;

import java.util.Random;

public class Organism {

  private int energy;
  private Position position;
  private Random random = new Random();

  public Organism(int energy) {
    this.energy = energy;
  }

  public void move() {
    int newX = position.getX();
    int newY = position.getY();

    // Decide whether to move vertically or horizontally
    boolean moveVertically = random.nextBoolean();

    if (moveVertically) {
      // Move up or down by 1
      newY += random.nextBoolean() ? 1 : -1;
    } else {
      // Move left or right by 1
      newX += random.nextBoolean() ? 1 : -1;
    }

    // TODO: Use the board's moveOrganism method to move the organism
  }

  public void setPosition(Position position) {
    this.position = position;
  }

  public Position getPosition() {
    return position;
  }
}

