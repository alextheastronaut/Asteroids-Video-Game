package asteroids.participants;

import static asteroids.game.Constants.*;
import java.awt.Shape;
import java.awt.geom.*;
import asteroids.destroyers.*;
import asteroids.game.Controller;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;

/**
 * Represents ships
 */
public class AlienShip extends Participant implements AsteroidDestroyer, ShipDestroyer
{
    /** The outline of the Alien ship */
    private Shape outline;

    /** Size of Alien Ship */
    private int size;
    
    /** Points the alien ship gives */
    private int score;
    
    /** The game controller */
    private Controller controller;
    
    /** Direction ship moves */
    private double[] direction;

    /**
     * Constructs an Alien ship at the specified coordinates that is pointed in the given direction.
     */
    public AlienShip (Controller controller, int level)
    {
        this.controller = controller;
        this.size = (level == 1) ? 1 : 0;
        
        this.score = ALIENSHIP_SCORE[size];

        double initialDirection = RANDOM.nextInt(2) * Math.PI;
        setDirection(initialDirection);

        setPosition(0, RANDOM.nextDouble() * SIZE);
        setVelocity(ALIENSHIP_SPEED[size], initialDirection);

        Path2D.Double poly = new Path2D.Double();
        poly.moveTo(20, 0);
        poly.lineTo(10, 8);
        poly.lineTo(-10, 8);
        poly.lineTo(-20, 0);
        poly.lineTo(20, 0);
        poly.lineTo(-20, 0);
        poly.lineTo(-10,-8);
        poly.lineTo(10, -8);
        poly.lineTo(-8, -8);
        poly.lineTo(-6, -15);
        poly.lineTo(6, -15);
        poly.lineTo(8, -8);
        poly.lineTo(10, -8);
        poly.closePath();
        outline = poly;
        
        direction = new double[]{initialDirection + 180/Math.PI, initialDirection, initialDirection - 180/Math.PI };

        // Scale to the desired size
        double scale = ALIENSHIP_SCALE[size];
        poly.transform(AffineTransform.getScaleInstance(scale, scale));
        
        new ParticipantCountdownTimer(this, "move", RANDOM.nextInt(ALIEN_DELAY));
        new ParticipantCountdownTimer(this, "shoot", ALIEN_DELAY);
    }
    
    public int getScore() 
    {
        return this.score;
    }
    
    public int getSize() 
    {
        return this.size;
    }

    @Override
    protected Shape getOutline ()
    {
        return outline;
    }

    /**
     * When an Alien Ship collides with a ShipDestroyer, it expires
     */
    @Override
    public void collidedWith (Participant p)
    {
        if (p instanceof AlienDestroyer)
        {
            // Expire the ship from the game
            Participant.expire(this);
            
            // Inform controller alien destroyed
            controller.alienShipDestroyed();
        }
    }

    /**
     * This method is invoked when a ParticipantCountdownTimer completes its countdown.
     */
    @Override
    public void countdownComplete (Object payload)
    {
        // Ship randomly changes direction
        if (payload.equals("move"))
        {
            setVelocity(ALIENSHIP_SPEED[size], direction[RANDOM.nextInt(3)]);
            new ParticipantCountdownTimer(this, "move", RANDOM.nextInt(ALIEN_DELAY));
        }
        
        if (payload.equals("shoot")) 
        {            
            controller.alienShoot();
            new ParticipantCountdownTimer(this, "shoot", ALIEN_DELAY);
        }
    }
}
