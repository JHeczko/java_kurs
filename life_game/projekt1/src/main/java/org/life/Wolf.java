package org.life;

public class Wolf extends Organism{
    public Wolf(int energy, int attack, int range, String name){
        super(energy,attack,range,name);
    }
    protected String name = "Wilk";
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
            newY += random.nextBoolean() ? 3 : -3;
        } else {
            // Move left or right by 1
            newX += random.nextBoolean() ? 3 : -3;
        }
        vec[0] = newX;
        vec[1] = newY;
        return vec;
    }
}
