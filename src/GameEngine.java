import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

public class GameEngine extends JPanel implements KeyListener {

    private final int FPS = 240;
    private final int PLAYER_X = 896;
    private final int PLAYER_Y = 952;
    private final int PLAYER_MOVEMENT_SPEED = 3;

    private boolean paused = false;
    private final Image background = new ImageIcon("data/images/background.jpg").getImage();
    private final Image door = new ImageIcon("data/images/door.png").getImage();
    private final Image playerImage = new ImageIcon("data/images/player.png").getImage();
    private final Image enemyImage = new ImageIcon("data/images/guard.png").getImage();
    private final Image basketImage = new ImageIcon("data/images/basket.png").getImage();
    private final Image mountainImage = new ImageIcon("data/images/mountain.png").getImage();
    private int levelNum;
    private Player player;
    private Enemy enemy;
    private Basket basket;
    private Mountain mountain;
    private ArrayList<Mountain> mountains;
    private Timer newFrameTimer;
    private ArrayList<Enemy> enemies;
    private int collected;
    private ArrayList<Basket> baskets;
    private boolean gameOver = false;
    private boolean leaderboardView = false;
    private String name;
    private ArrayList<HighScore> highScores;


    public GameEngine() {
        super();
        setFocusable(true);
        addKeyListener(this);
        name = JOptionPane.showInputDialog("Enter your name:");
        if (name.isEmpty()) {
            name = "Player";
        }
        else if (name == null){
            System.exit(0);
        }
        restart(3, 1, 0);
        newFrameTimer = new Timer(1000/FPS, new NewFrameListener());
        newFrameTimer.start();
    }

    @Override
    public void keyPressed(KeyEvent e){
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            player.setVelx(-PLAYER_MOVEMENT_SPEED);
        } else if (key == KeyEvent.VK_RIGHT) {
            player.setVelx(PLAYER_MOVEMENT_SPEED);
        } else if (key == KeyEvent.VK_UP) {
            player.setVely(-PLAYER_MOVEMENT_SPEED);
        } else if (key == KeyEvent.VK_DOWN) {
            player.setVely(PLAYER_MOVEMENT_SPEED);
        }
        if (key == KeyEvent.VK_ESCAPE) {
            paused = !paused;
        }
        if(gameOver){
            if (key == KeyEvent.VK_SPACE) {
                name = JOptionPane.showInputDialog("Enter your name:");
                if (name.isEmpty()) {
                    name = "Player";
                }
                else if (name == null){
                    System.exit(0);
                }
                leaderboardView=false;
                restart(3,1,0);
                gameOver = false;
            }
            if (key == KeyEvent.VK_ENTER) {
                leaderboardView = true;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e){
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT) {
            player.setVelx(0);
        }
        if (key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN) {
            player.setVely(0);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    public void restart(int hp, int level, int score) {
        levelNum = level;
        collected = score;
        player = new Player(PLAYER_X,PLAYER_Y,128,128,playerImage,hp);
        player.setVelx(0);
        player.setVely(0);
        enemies = new ArrayList<>();
        baskets = new ArrayList<>();
        mountains = new ArrayList<>();
        for(int i=0;i<levelNum;++i){
            Random random = new Random();
            enemy = new Enemy(random.nextInt(1792), random.nextInt(824),128,128,enemyImage);
            enemies.add(enemy);
        }
        for(int i=0;i<levelNum;++i){
            Random random = new Random();
            mountain = new Mountain(random.nextInt(1792), random.nextInt(824),128,128,mountainImage);
            mountains.add(mountain);
        }
        for(int i=0;i<3;++i){
            Random random = new Random();
            basket = new Basket(random.nextInt(1792), random.nextInt(824),128,128,basketImage);
            baskets.add(basket);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, 1920, 1080, null);
        if(!gameOver){
            g.drawImage(door, PLAYER_X, PLAYER_Y, 128, 128, null);
            player.draw(g);
            for(Enemy enemy : enemies){
                enemy.draw(g);
            }
            for(Mountain mountain : mountains){
                mountain.draw(g);
            }
            for(Basket basket : baskets){
                basket.draw(g);
            }
            g.setColor(Color.ORANGE);
            g.setFont(new Font("Arial", Font.BOLD, 72));
            g.drawString("POINTS: " + collected, 30, 70);
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 72));
            g.drawString("HP: " + player.getHP(), 1700, 70);
        }
        else if(!leaderboardView){
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 72));
            g.drawString("GAME OVER!", 720, 300);
            g.setFont(new Font("Arial", Font.PLAIN, 48));
            g.drawString("Score: " + collected, 850, 400);
            g.setFont(new Font("Arial", Font.PLAIN, 36));
            g.drawString("Press SPACE to restart the game!", 680, 500);
            g.drawString("Press ENTER to show the leaderboard!", 630, 600);
        }
        else {
            getLeaderBoard();
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(new Color(20, 20, 20, 200));
            g.fillRoundRect(400, 100, 1120, 880, 50, 50);

            g.setFont(new Font("Arial", Font.BOLD, 72));
            g.setColor(Color.WHITE);
            g.drawString("LEADERBOARD", 640, 200);

            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.setColor(new Color(255, 215, 0));
            g.drawString("Name", 500, 300);
            g.drawString("Score", 1100, 300);

            g.setColor(new Color(255, 255, 255, 50));
            g.fillRect(450, 320, 1020, 3);

            g.setFont(new Font("Arial", Font.PLAIN, 36));
            for (int i = 0; i < highScores.size(); ++i) {
                HighScore record = highScores.get(i);

                if (i == 0) {
                    g.setColor(new Color(255, 215, 0));
                } else if (i == 1) {
                    g.setColor(new Color(192, 192, 192));
                } else if (i == 2) {
                    g.setColor(new Color(205, 127, 50));
                } else {
                    g.setColor(Color.WHITE);
                }

                g.drawString(record.name(), 500, 380 + i * 60);
                g.drawString(String.valueOf(record.score()), 1100, 380 + i * 60);
            }

            g.setColor(new Color(255, 215, 0, 150));
            g.fillRect(400, 90, 1120, 5);
            g.fillRect(400, 970, 1120, 5);

            g.setFont(new Font("Arial", Font.ITALIC, 28));
            g.setColor(new Color(128, 128, 128));
            g.drawString("Press SPACE to return to the main menu", 680, 1050);

        }
    }

    class NewFrameListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            if (!paused && !gameOver) {
                Rectangle nextBounds = new Rectangle(player.getNextX(), player.getNextY(), player.getWidth(), player.getHeight());
                boolean collision = false;

                for(Mountain mountain : mountains){
                    Rectangle mountainBounds = new Rectangle(mountain.getX(), mountain.getY(), mountain.getWidth(), mountain.getHeight());
                    if(nextBounds.intersects(mountainBounds)){
                        collision = true;
                        break;
                    }
                }
                if(!collision){
                    player.move();
                }
                else {
                    player.setVelx(0);
                    player.setVely(0);
                }

                for(int i=0;i<enemies.size();++i) {
                    Enemy e = enemies.get(i);
                    for(Mountain mountain : mountains){
                        if(e.collides(mountain)){
                            e.invertVelX();
                            e.invertVelY();
                        }
                    }
                    e.move();
                    if (player.collides(e)) {
                        player.damage();
                        enemies.remove(i);
                        --i;
                        player.setX(PLAYER_X);
                        player.setY(PLAYER_Y);
                        player.setVelx(0);
                        player.setVely(0);
                    }
                }
                for(int i=0;i<baskets.size();++i) {
                    Basket b = baskets.get(i);
                    if (player.collides(b)) {
                        baskets.remove(i);
                        collected++;
                        --i;
                    }
                }
            }
            if(collected % 3 == 0 && collected>(levelNum-1)*3){
                restart(player.getHP(), ++levelNum, collected);
            }
            if(player.getHP()<1){
                gameOver=true;
                try{
                    HighScores.instance().insertScore(name,collected);
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            repaint();
        }

    }
    private void getLeaderBoard(){
        try {
            highScores = HighScores.instance().getTopScores();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}