package org.life;

public class Board {

  private int count;
  private int width;
  private int height;
  private Organism[][] organisms;

  public int getCount(){
    return this.count;
  }
  public Board(int width, int height) {
    this.width = width;
    this.height = height;
    this.organisms = new Organism[width][height];
    this.count = 0;
  }

  public boolean addOrganism(Organism organism, int x, int y) {
    if (organisms[x][y] == null) {
      organisms[x][y] = organism;
      organism.setPosition(new Position(x, y));
      count++;
      return true;
    } else {
      System.out.println("Position already occupied!");
      return false;
    }
  }

  //to moze wygladac dziwnie jak ta metoda attack jest zaimplementowana, ale tluamczac, mamy petle ktora bedzie nam robila -1 i 1, to jest po to aby wykonac atak w gore i w prawo, a nastepnie w dol i lewo, nastepnie patrzymy czy nasz atak nie wychodzi za granice tablicy i wtedy mozemy odjac energie za pomoca loseEnergy()
  public void attack(Organism organism, int[] pos){
    int range = organism.range;
    for(int i = -1; i<2; i+=2) {
      for(int j = 1; j <= range; j++){
        if((pos[0] + j*i) >= 0 && (pos[0]+j*i) < width && (pos[1]+j*i) >= 0 && (pos[1]+j*i) < height ){
          if(organisms[pos[0]+j*i][pos[1]] != null){
            organisms[pos[0]+j*i][pos[1]].loseEnergy(organism.attack);
            if(organisms[pos[0]+j*i][pos[1]].isDead()){
              System.out.println("Przeciwnik nie zyje na pozycji: " + (pos[0]+j*i) + " " + pos[1]);
              organisms[pos[0]+j*i][pos[1]] = null;
              this.count--;
            }
            System.out.println("Zaatakowalem przeciwnika na pozycji: " + (pos[0]+j*i) + " " + pos[1]);
          }
          if(organisms[pos[0]][pos[1]+j*i] != null){
            organisms[pos[0]][pos[1]+j*i].loseEnergy(organism.attack);
            if(organisms[pos[0]][pos[1]+j*i].isDead()){
              System.out.println("Przeciwnik nie zyje na pozycji: " + pos[0] + " " + pos[1]+j*i);
              organisms[pos[0]][pos[1]+j*i] = null;
              this.count--;
            }
            System.out.println("Zaatakowalem przeciwnika na pozycji: " + pos[0] + " " + pos[1]+j*i);
          }
        }
      }
    }
  }


  public void moveOrganism(Organism organism) {
    // TODO implement that one organism eats the other
    // newXY[0] = newX ; newXY[1] = newY
    int[] newXY = organism.move();
    if (newXY[0] >= 0 && newXY[0] < width && newXY[1] >= 0 && newXY[1] < height && organisms[newXY[0]][newXY[1]] == null) {
        organisms[organism.getPosition().getX()][organism.getPosition().getY()] = null;
        organisms[newXY[0]][newXY[1]] = organism;
        organism.setPosition(new Position(newXY[0], newXY[1]));
        attack(organism, newXY);
//        System.out.println("Making move to " + organism.getPosition().getX() + "X and" + organism.getPosition().getY()  + "Y");
      //organism is going to attack around his center 1 up, 1 down, 1 left, 1 right, its basic stats
      } else {
//      System.out.println("Invalid move!");
    }
  }
  public int endgame(){
    for(int i = 0; i < width; i++){
      for(int j = 0; j < height; j++){
        if(organisms[i][j] != null){
          System.out.println("\n Wygrał organizm klasy: " + organisms[i][j].name);
          return 1;
        }
      }
    }
    return -1;
  }
}
