package org.life;

import java.util.Random;
import java.lang.Thread;


public class LifeSimulator {

  public static void main(String[] args) {
    Board board = new Board(15, 15);
    /* TODO:
     - Add at least 2 classes that implement the Organism interface.
     - The new classes should possess unique abilities, such as:
        -- Jumping (moving more than 1 step at a time).
        -- Sight (detecting other organisms within a certain radius).
        -- Avoiding illegal moves.
     - Ensure that an Organism doesn't move if it attempts an illegal move.
     - Implement a mechanism where an Organism consumes another (taking all its energy) when it occupies the same space.
     - Run simulation for some time, eg. when there is only one Organism left
     energy,attack,range,name
     */
    Random rand = new Random();
    int countAdd = 0;
    Organism[] arrOrg = new Organism[10];
    for(int i = 0; i < 10; i++){
      switch(i%3) {
        case 0:
          arrOrg[i] = new Bear(400,70,1,"Niedzwiedz");
          break;
        case 1:
          arrOrg[i] = new Wolf(300,50,2,"Wilk");
          break;
        case 2:
          arrOrg[i] = new Cat(250,30,3,"Kot");
          break;
      }
    }
    while(countAdd != 10){
      int x = rand.nextInt(14);
      int y = rand.nextInt(14);
      if(board.addOrganism(arrOrg[countAdd],x,y)){
        countAdd++;
      }
    }
  while(board.getCount() != 1){
    for(int i = 0; i < 10; i++){
      if(board.getCount() == 1){
        break;
      }
      board.moveOrganism(arrOrg[i]);
      try {
        Thread.sleep(200);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
    System.out.println("Zostalo: " + board.getCount() + " organizmow");
  }
  board.endgame();
//Sterowanie reczne:
//    Scanner skaner = new Scanner(System.in);
//    int in = 0;
//    do{
//      System.out.println("Wybieerz opcje:\n1) Zrob losowy ruch\n2) Koniec gry\n3) Pokaz licznik graczy");
//      in = skaner.nextInt();
//      switch(in){
//        case 2:
//          break;
//        case 3:
//          System.out.println(board.getCount());
//      }
//      board.moveOrganism((organism1));
//    }while(true);

  }
}