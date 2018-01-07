package asteroids.game;

import java.util.Random;

/**
 * Provides constants that govern the game.
 */
public class Constants
{
    /**
     * A shared random number generator for general use.
     */
    public final static Random RANDOM = new Random();

    /**
     * The height and width of the game area.
     */
    public final static int SIZE = 750;

    /**
     * Game title
     */
    public final static String TITLE = "CS 1410 Asteroids";

    /**
     * Label on start game button
     */
    public final static String START_LABEL = "Start Game";

    /**
     * Speed beyond which participants may not accelerate
     */
    public final static double SPEED_LIMIT = 15;

    /**
     * Amount of "friction" that can be applied to ships so that they eventually
     * stop. Should be negative.
     */
    public final static double SHIP_FRICTION = -0.05;

    /**
     * Constant of acceleration of ship. Should be positive.s
     */
    public final static double SHIP_ACCELERATION = .65;

    /**
     * The number of milliseconds between the beginnings of frame refreshes
     */
    public final static int FRAME_INTERVAL = 33;
    
    /**
     * The number of milliseconds between beats, initially.
     */
    public final static int INITIAL_BEAT = 900;
    
    /**
     * The fastest beat interval.
     */
    public final static int FASTEST_BEAT = 300;
    
    /**
     * The amount by which the beat interval increases each time there's a beat.
     */
    public final static int BEAT_DELTA = 9;

    /**
     * The number of milliseconds between the end of a life and the display of
     * the next screen.
     */
    public final static int END_DELAY = 2500;

    /**
     * The average offset in pixels from the edges of the screen of newly-placed
     * asteroids.
     */
    public final static int EDGE_OFFSET = 150;

    /**
     * The game over message
     */
    public final static String GAME_OVER = "Game Over";

    /**
     * Duration in milliseconds of a bullet before it disappears.
     */
    public final static int BULLET_DURATION = 1000;

    /**
     * Speed, in pixels per frame, of a bullet.
     */
    public final static int BULLET_SPEED = 15;

    /**
     * Maximum number of bullets that can exist at one time.
     */
    public final static int BULLET_LIMIT = 8;

    /**
     * Scaling factors used for asteroids of size 0, 1, and 2.
     */
    public final static double[] ASTEROID_SCALE = { 0.5, 1.0, 2.0 };

    /**
     * Score earned for asteroids of size 0, 1, and 2.
     */
    public final static int[] ASTEROID_SCORE = { 100, 50, 20 };

    /**
     * Scaling factors used for alien ships of size 0 and 1.
     */
    public final static double[] ALIENSHIP_SCALE = { 0.5, 1.0 };
    
    /**
     * Score earned for alien ships of size 0 and 1.
     */
    public final static int[] ALIENSHIP_SCORE = { 1000, 200 };
    
    /**
     * Delay after which an alien ship appears.
     */
    public final static int ALIEN_DELAY = 5000;
    
    /**
     * Speed for alien ships of size 0 and 1.
     */
    public final static int[] ALIENSHIP_SPEED = { 10, 5 };
    
    /**
     * Maximum speed of large asteroid
     */
    public final static int MAXIMUM_LARGE_ASTEROID_SPEED = 3;
    
    /**
     * Maximum speed of medium asteroid
     */
    public final static int MAXIMUM_MEDIUM_ASTEROID_SPEED = 5;
    
    /**
     * Maximum speed of small asteroid
     */
    public final static int MAXIMUM_SMALL_ASTEROID_SPEED = 8;
    
    /**
     * Offset of score/level from left/right side.
     */
    public final static int LABEL_HORIZONTAL_OFFSET = 50;
    
    /**
     * Offset of score/level from top side.
     */
    public final static int LABEL_VERTICAL_OFFSET = 30;
    
    /**
     * Height of ship
     */
    public final static int SHIP_HEIGHT = 42;
    
    /**
     * Width of ship
     */
    public final static int SHIP_WIDTH = 24;
    
    /**
     * Distance between ships when used to display lives
     */
    public final static int SHIP_SEPARATION = 6;
}
