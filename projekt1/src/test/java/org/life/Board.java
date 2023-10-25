package org.life;

public class Board {

  private int width;
  private int height;
  private Organism[][] organisms;

  public Board(int width, int height) {
    this.width = width;
    this.height = height;
    this.organisms = new Organism[width][height];
  }

  public void addOrganism(Organism organism, int x, int y) {
    if (organisms[x][y] == null) {
      organisms[x][y] = organism;
      organism.setPosition(new Position(x, y));
    } else {
      System.out.println("Position already occupied!");
    }
  }

  public void moveOrganism(Organism organism, int newX, int newY) {
    // TODO implement that one organism eats the other
    if (newX >= 0 && newX < width && newY >= 0 && newY < height && organisms[newX][newY] == null) {
      organisms[organism.getPosition().getX()][organism.getPosition().getY()] = null;
      organisms[newX][newY] = organism;
      organism.setPosition(new Position(newX, newY));
    } else {
      System.out.println("Invalid move!");
    }
  }
}
