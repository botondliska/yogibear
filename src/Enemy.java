import java.awt.*;
import java.util.Random;

public class Enemy extends Sprite{
    private double velx;
    private double vely;
    private boolean horizontal;
    Random random = new Random();

    public Enemy(int x, int y, int width, int height, Image image) {
        super(x,y,width,height,image);
        velx = 4;
        vely = 4;
        horizontal = random.nextBoolean();
    }

    public void move(){
        if(horizontal){
            x += velx;
            if (x + width >= 1920 || x <= 0) {
                invertVelX();
            }
        }
        else {
            y += vely;
            if (y + height >= 1080 || y <= 0) {
                invertVelY();
            }
        }
    }

    public void invertVelX() {
        velx = -velx;
    }

    public void invertVelY() {
        vely = -vely;
    }
}