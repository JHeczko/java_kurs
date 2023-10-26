package org.life;

public class Bear extends Organism{
    public Bear(int energy, int attack, int range, String name){
        super(energy,attack,range,name);
    }

    protected String name;
    protected int attack;
    protected int energy;
    protected int range;
    public final int[] move() {
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
    }
}