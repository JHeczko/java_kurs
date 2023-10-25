package org.life;

import java.util.Random;

public class Organism {

  private int energy;
  private Position position;

  protected int range;

  private Random random = new Random();

  public Organism(int energy) {
    this.energy = energy;
    this.range = 1;
  }

  public int[] move() {
    int newX = position.getX();
    int newY = position.getY();
    int[] vec = new int[2];
    // Decide whether to move vertically or horizontally
    boolean moveVertically = random.nextBoolean();

    if (moveVertically) {
      // Move up or down by 1
      newY += random.nextBoolean() ? 1 : -1;
    } else {
      // Move left or right by 1
      newX += random.nextBoolean() ? 1 : -1;
    }
    vec[0] = newX;
    vec[1] = newY;
    return vec;
    // TODO: Use the board's moveOrganism method to move the organism
  }

  public void setPosition(Position position) {
    this.position = position;
  }

  public void loseEnergy(int lose){
    this.energy -= lose;
  }

  public boolean isDead(){
    return (this.energy == 0);
  }
  public Position getPosition() {
    return position;
  }
}

