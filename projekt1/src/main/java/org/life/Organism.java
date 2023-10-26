package org.life;

import java.util.Random;

public class Organism {

  protected String name;
  protected int attack;
  protected int energy;
  protected int range;
  protected Position position;

  protected final Random random = new Random();

  public Organism(int energy, int attack, int range, String name) {
    this.energy = energy;
    this.attack = attack;
    this.range = range;
    this.name = name;
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
    return (this.energy == 0 || this.energy < 0);
  }
  public Position getPosition() {
    return position;
  }
}

