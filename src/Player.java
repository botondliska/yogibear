import javax.swing.*;
import java.awt.*;

public class Player extends Sprite{
    private double velx;
    private double vely;
    private int HP = 3;

    public Player (int x, int y, int width, int height, Image image, int HP) {
        super(x,y,width,height,image);
        this.HP = HP;
    }

    public void move() {
        if ((velx < 0 && x > 0) || (velx > 0 && x + width <= 1920)) {
            x += velx;
        }
        if ((vely < 0 && y > 0) || (vely > 0 && y + height <= 1080)) {
            y += vely;
        }
        if(y + height > 1080)y=1080-height;
    }

    public double getVelx(){
        return velx;
    }
    public double getVely(){
        return vely;
    }
    public int getNextX(){
        return getX()+(int)getVelx();
    }
    public int getNextY(){
        return getY()+(int)getVely();
    }
    public void setVelx(double velx){ this.velx = velx;}
    public void setVely(double vely){ this.vely = vely;}

    public int getHP(){ return HP;}
    public void damage(){ HP = HP-1;}
}