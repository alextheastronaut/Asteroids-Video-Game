package asteroids.game;

import static asteroids.game.Constants.*;
import java.awt.event.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Iterator;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import asteroids.participants.AlienBullets;
import asteroids.participants.AlienShip;
import asteroids.participants.Asteroid;
import asteroids.participants.Bullets;
import asteroids.participants.Debris;
import asteroids.participants.Dust;
import asteroids.participants.Missile;
import asteroids.participants.Ship;

/**
 * Controls a game of Asteroids.
 */
public class Controller implements KeyListener, ActionListener
{
    /** The state of all the Participants */
    private ParticipantState pstate;

    /** The ship (if one is active) or null (otherwise) */
    private Ship ship, ship2;

    /** One bullet */
    private Bullets bullets;

    /** When this timer goes off, it is time to refresh the animation */
    private Timer refreshTimer;

    /** When this timer goes off, it is time to beat */
    private Timer beatTimer;

    /**
     * The time at which a transition to a new stage of the game should be made. A transition is scheduled a few seconds
     * in the future to give the user time to see what has happened before doing something like going to a new level or
     * resetting the current level.
     */
    private long transitionTime;

    /** The beat delay */
    private int beatDelay;

    /** Number of lives left */
    private int lives;

    /** The current level */
    private int level;

    /** Records the score */
    private int score;

    /** The game display */
    private Display display;

    /** Alien ship */
    private AlienShip alien;

    /** The time at which a new alien spawns after the old one is destroyed */
    private long alienSpawnTime;

    /** Each alien bullet */
    private AlienBullets alienBullet;

    /** Boolean for splash screen */
    private boolean isSplashScreen;

    private Missile mis;

    /**
     * All the sound clips for the game are below. Bullet firing, ship destruction, asteroid destructions, thrusters,
     * aliens, and saucer sounds
     */
    private Clip fireClip, shipClip, bigBang, medBang, smallBang, thrustClip, beat1, beat2, beatSound, alienBang,
            alienSound, saucerSmall, saucerBig;

    /** Keyboard status */
    private static boolean isRightDown, isLeftDown, isUpDown, isSpace;

    /**
     * Constructs a controller to coordinate the game and screen
     */
    public Controller ()
    {
        // Initialize the ParticipantState
        pstate = new ParticipantState();

        // Set up the refresh timer.
        refreshTimer = new Timer(FRAME_INTERVAL, this);

        // Set up beat timer
        beatTimer = new Timer(INITIAL_BEAT, this);

        // Clear the transitionTime
        transitionTime = Long.MAX_VALUE;

        // Record the display object
        display = new Display(this);

        // No keys are pressed down
        isRightDown = isLeftDown = isUpDown = isSpace = false;

        // Sets the level to 0
        level = 0;

        isSplashScreen = true;

        thrustClip = createClip("/sounds/thrust.wav");
        fireClip = createClip("/sounds/fire.wav");
        bigBang = createClip("/sounds/bangLarge.wav");
        medBang = createClip("/sounds/bangMedium.wav");
        smallBang = createClip("/sounds/bangSmall.wav");
        saucerSmall = createClip("/sounds/saucerSmall.wav");
        saucerBig = createClip("/sounds/saucerBig.wav");
        shipClip = createClip("/sounds/bangShip.wav");
        alienBang = createClip("/sounds/bangAlienShip.wav");
        beat1 = createClip("/sounds/beat1.wav");
        beat2 = createClip("/sounds/beat2.wav");
        alienSound = null;

        // Bring up the splash screen and start the refresh timer
        splashScreen();
        display.setVisible(true);
        refreshTimer.start();
    }

    public Clip createClip (String soundFile)
    {
        // Opening the sound file this way will work no matter how the
        // project is exported. The only restriction is that the
        // sound files must be stored in a package.
        try (BufferedInputStream sound = new BufferedInputStream(getClass().getResourceAsStream(soundFile)))
        {
            // Create and return a Clip that will play a sound file. There are
            // various reasons that the creation attempt could fail. If it
            // fails, return null.
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(sound));
            return clip;
        }
        catch (LineUnavailableException e)
        {
            return null;
        }
        catch (IOException e)
        {
            return null;
        }
        catch (UnsupportedAudioFileException e)
        {
            return null;
        }
    }

    /**
     * Returns the ship, or null if there isn't one
     */
    public Ship getShip ()
    {
        return ship;
    }
    
    public Ship getShip2 ()
    {

        return ship2;
    }
    
    public AlienShip getAlien() 
    {
        return alien;
    }

    protected Missile getMissile ()
    {
        mis = new Missile(ship.getXNose(), ship.getYNose(), ship.getRotation(), ship.getDirection(), ship.getSpeed(), this);
        return mis;
    }

    /**
     * Configures the game screen to display the splash screen
     */
    private void splashScreen ()
    {
        // Clear the screen, reset the level, and display the legend
        clear();
        display.setLegend("Asteroids");

        // Place four asteroids near the corners of the screen.
        placeAsteroids();
    }

    /**
     * The game is over. Displays a message to that effect.
     */
    private void finalScreen ()
    {
        display.setLegend(GAME_OVER);
        display.removeKeyListener(this);
    }

    /**
     * Place a new ship in the center of the screen. Remove any existing ship first.
     */
    protected void placeShip ()
    {
        // Place a new ship
        Participant.expire(ship);
        ship = new Ship(SIZE / 2, SIZE / 2, -Math.PI / 2, this);
        addParticipant(ship);
        display.setLegend("");
    }

    /**
     * places alien ship based on level
     */
    private void placeAlienShip ()
    {
        Participant.expire(alien);
        if (level > 0)
        {
            alien = new AlienShip(this, level);
            addParticipant(alien);
            alienSound = (alien.getSize() == 1) ? saucerBig : saucerSmall;
            alienSound.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    /**
     * Alien ship destroyed
     */
    public void alienShipDestroyed ()
    {
        score += alien.getScore();
        placeDebris(alien.getX(), alien.getY());
        alienSound.stop();

        if (alienBang.isRunning())
        {
            alienBang.stop();
        }
        alienBang.setFramePosition(0);
        alienBang.start();
        alien = null;

        alienSpawnTime = System.currentTimeMillis() + ALIEN_DELAY + RANDOM.nextInt(6);
    }

    /**
     * Places four asteroids at the corners of the screen at level 1. Adds one more asteroid per level.
     */
    private void placeAsteroids ()
    {
        int[] position = { RANDOM.nextInt(EDGE_OFFSET), SIZE - RANDOM.nextInt(EDGE_OFFSET) };

        for (int i = 0; i < level + 4; i++)
        {
            addParticipant(new Asteroid(RANDOM.nextInt(4), 2, position[RANDOM.nextInt(2)], position[RANDOM.nextInt(2)],
                    MAXIMUM_LARGE_ASTEROID_SPEED, this));
        }
    }

    /**
     * Places four dust particles
     */
    private void placeDust (double x, double y)
    {
        for (int i = 0; i < 4; i++)
        {
            addParticipant(new Dust(x, y));
        }
    }

    private void placeDebris (double x, double y)
    {
        for (int i = 0; i < 3; i++)
        {
            addParticipant(new Debris(x, y));
        }
    }

    /**
     * Shoots bullet form ship
     */
    protected void shootBullet ()
    {
        if (fireClip.isRunning())
        {
            fireClip.stop();
        }
        fireClip.setFramePosition(0);
        fireClip.start();

        bullets = new Bullets(BULLET_SPEED, ship.getXNose(), ship.getYNose(), ship.getRotation(), BULLET_DURATION);
        // adds bullet to participants
        addParticipant(bullets);
    }

    /**
     * alien shoots bullet
     */
    public void alienShoot ()
    {
        if (alien != null)
        {
            // Select bullet angle based on level
            double angle = (alien.getSize() == 1 || ship == null) ? RANDOM.nextDouble() * 2 * Math.PI : alien.getAngle(ship.getX(), ship.getY());

            alienBullet = new AlienBullets(BULLET_SPEED, alien.getX(), alien.getY(), angle, BULLET_DURATION);

            // adds bullet to participants
            addParticipant(alienBullet);

            if (fireClip.isRunning())
            {
                fireClip.stop();
            }
            fireClip.setFramePosition(0);
            fireClip.start();
        }
    }

    /**
     * Clears the screen so that nothing is displayed
     */
    private void clear ()
    {
        pstate.clear();
        display.setLegend("");
        ship = null;
        alien = null;
    }

    /**
     * Sets things up and begins a new game.
     */
    private void initialScreen ()
    {
        // Clear the screen
        clear();

        // Shuts up Alien
        if (alienSound != null && alienSound.isRunning())
            alienSound.stop();

        beatDelay = INITIAL_BEAT;
        beatTimer.setDelay(beatDelay);
        beatTimer.restart();

        // Place asteroids
         placeAsteroids();

        // Place the ship
        placeShip();

        this.isSplashScreen = false;

        // Spawns alien ship after 5-10 seconds
        alienSpawnTime = System.currentTimeMillis() + ALIEN_DELAY + RANDOM.nextInt(6);

        // Start listening to events (but don't listen twice)
        display.removeKeyListener(this);
        display.addKeyListener(this);

        // Give focus to the game screen
        display.requestFocusInWindow();
    }

    /**
     * Adds a new Participant
     */
    public void addParticipant (Participant p)
    {
        pstate.addParticipant(p);
    }

    /**
     * The ship has been destroyed
     */
    public void shipDestroyed ()
    {
        placeDebris(ship.getX(), ship.getY());

        if (shipClip.isRunning())
        {
            shipClip.stop();
        }
        shipClip.setFramePosition(0);
        shipClip.start();

        beatTimer.stop();

        // Null out the ship
        ship = null;

        // Decrement lives
        lives--;

        // Since the ship was destroyed, schedule a transition
        scheduleTransition(END_DELAY);
    }

    /**
     * An asteroid has been destroyed
     */
    public void asteroidDestroyed ()
    {
        Iterator<Participant> iter = getParticipants();

        while (iter.hasNext())
        {
            Participant p = iter.next();
            if (p instanceof Asteroid && p.isExpired())
            {
                Asteroid a = (Asteroid) p;
                if (a.getSize() == 2)
                {
                    if (bigBang.isRunning())
                    {
                        bigBang.stop();
                    }
                    bigBang.setFramePosition(0);
                    bigBang.start();
                    addParticipant(new Asteroid(RANDOM.nextInt(4), 1, a.getX(), a.getY(),
                            MAXIMUM_LARGE_ASTEROID_SPEED + RANDOM.nextInt(3), this));
                    addParticipant(new Asteroid(RANDOM.nextInt(4), 1, a.getX(), a.getY(),
                            MAXIMUM_LARGE_ASTEROID_SPEED + RANDOM.nextInt(3), this));
                    this.score += 20;
                }
                else if (a.getSize() == 1)
                {
                    if (medBang.isRunning())
                    {
                        medBang.stop();
                    }
                    medBang.setFramePosition(0);
                    medBang.start();
                    addParticipant(new Asteroid(RANDOM.nextInt(4), 0, a.getX(), a.getY(),
                            MAXIMUM_LARGE_ASTEROID_SPEED + RANDOM.nextInt(6), this));
                    addParticipant(new Asteroid(RANDOM.nextInt(4), 0, a.getX(), a.getY(),
                            MAXIMUM_LARGE_ASTEROID_SPEED + RANDOM.nextInt(6), this));
                    this.score += 50;
                }
                else
                {
                    if (smallBang.isRunning())
                    {
                        smallBang.stop();
                    }
                    smallBang.setFramePosition(0);
                    smallBang.start();
                    this.score += 100;
                }
                placeDust(a.getX(), a.getY());
            }
        }
        // If all the asteroids are gone, schedule a transition
        if (pstate.countAsteroids() == 0)
        {
            scheduleTransition(END_DELAY);
        }
    }

    /**
     * Schedules a transition m msecs in the future
     */
    private void scheduleTransition (int m)
    {
        transitionTime = System.currentTimeMillis() + m;
    }

    /**
     * This method will be invoked because of button presses and timer events.
     */
    @Override
    public void actionPerformed (ActionEvent e)
    {
        // The start button has been pressed. Stop whatever we're doing
        // and bring up the initial screen
        if (e.getSource() instanceof JButton)
        {
            // Reset statistics
            lives = 3;
            level = 0;
            score = 0;

            initialScreen();

            // It may be time to make a game transition
            performTransition();

            // Move the participants to their new locations
            pstate.moveParticipants();

            // Refresh screen
            display.refresh();
        }

        // Time to refresh the screen and deal with keyboard input
        else if (e.getSource() == refreshTimer)
        {
            if (ship != null)
            {
                if (isRightDown)
                {
                    ship.turnRight();
                }
                if (isLeftDown)
                {
                    ship.turnLeft();
                }
                if (isUpDown)
                {
                    ship.accelerate();
                    thrustClip.loop(Clip.LOOP_CONTINUOUSLY);
                }
                if (isSpace)
                {
                    if (pstate.countBullets() < BULLET_LIMIT)
                    {
                        shootBullet();
                    }
                }

                // Spawns alien ship after 5-10 seconds
                if (alienSpawnTime <= System.currentTimeMillis())
                {
                    alienSpawnTime = Long.MAX_VALUE;
                    placeAlienShip();
                }
            }

            if (!isSplashScreen)
            {
                display.setLabels(score, level, lives);
            }

            // It may be time to make a game transition
            performTransition();

            // Move the participants to their new locations
            pstate.moveParticipants();

            // Refresh screen
            display.refresh();
        }

        if (e.getSource() == beatTimer)
        {
            beatSound = (beatSound == beat1) ? beat2 : beat1;
            if (beatSound.isRunning())
            {
                beatSound.stop();
            }
            beatSound.setFramePosition(0);
            beatSound.start();

            if (beatDelay > FASTEST_BEAT + 6)
            {
                beatDelay = beatTimer.getDelay() - BEAT_DELTA;
                beatTimer.setDelay(beatDelay);
            }
            else if (beatDelay == FASTEST_BEAT + 6)
                beatTimer.setDelay(FASTEST_BEAT);
        }
    }

    public boolean isAccelerate ()
    {
        return this.isUpDown;
    }

    /**
     * Returns an iterator over the active participants
     */
    public Iterator<Participant> getParticipants ()
    {
        return pstate.getParticipants();
    }

    /**
     * If the transition time has been reached, transition to a new state
     */
    private void performTransition ()
    {
        // Do something only if the time has been reached
        if (transitionTime <= System.currentTimeMillis())
        {
            // Clear the transition time
            transitionTime = Long.MAX_VALUE;

            if (lives > 0)
            {
                if (pstate.countAsteroids() > 0)
                {
                    placeShip();
                    beatTimer.start();
                }
                else
                {
                    level++;
                    initialScreen();
                }
            }

            // If there are no lives left, the game is over. Show the final
            // screen.
            else if (lives == 0)
            {
                finalScreen();
            }
        }
    }

    /**
     * If a key of interest is pressed, ship performs an action
     */
    @Override
    public void keyPressed (KeyEvent e)
    {
        // Ship turns right if right arrow key or D are pressed
        if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D && ship != null)
        {
            isRightDown = true;
        }

        // Ship turns left if left arrow key or A are pressed
        else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A && ship != null)
        {
            isLeftDown = true;
        }

        // Ship thrusts if up arrow key or W are pressed
        else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W && ship != null)
        {
            isUpDown = true;

        }

        // Ship shoots if S, space bar, or down key are pressed
        else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_SPACE
                || e.getKeyCode() == KeyEvent.VK_S && ship != null)
        {
            isSpace = true;
        }
    }

    /**
     * These events are ignored.
     */
    @Override
    public void keyTyped (KeyEvent e)
    {
    }

    /**
     * If a key of interest is released, ship stops performing the action
     */
    @Override
    public void keyReleased (KeyEvent e)
    {
        // Ship right if right arrow key or D are pressed
        if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D && ship != null)
        {
            isRightDown = false;
        }

        // Ship turns left if left arrow key or A are pressed
        else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A && ship != null)
        {
            isLeftDown = false;
        }

        // Ship thrusts if up arrow key or W are pressed
        else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W && ship != null)
        {
            isUpDown = false;
            thrustClip.stop();
        }

        // Ship shoots if S, space bar, or down key are pressed
        else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_SPACE
                || e.getKeyCode() == KeyEvent.VK_S && ship != null)
        {
            isSpace = false;
        }

    }
}
