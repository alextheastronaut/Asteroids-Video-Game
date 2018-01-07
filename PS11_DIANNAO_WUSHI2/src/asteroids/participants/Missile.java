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
public class Missile extends Participant implements AsteroidDestroyer, AlienDestroyer
{
    /** The outline of the ship */
    private Shape ship;

    /** The outline of the ship with flame */
    private Shape flame;

    /** flame represent */
    private boolean flameOn;

    private Controller controller;
    private AlienShip alien;

    /**
     * Constructs a ship at the specified coordinates that is pointed in the given direction.
     */
    public Missile (double x, double y, double direction, double vector, double speed, Controller controller)
    {
        this.controller = controller;
        alien = controller.getAlien();
        flameOn = false;
        setPosition(x, y);
        setRotation(direction);
        setVelocity(speed, vector);

        Path2D.Double poly = new Path2D.Double();
        poly.moveTo(21, 0);
        poly.lineTo(-21, 12);
        poly.lineTo(-14, 10);
        poly.lineTo(-14, -10);
        poly.lineTo(-21, -12);
        poly.closePath();

        poly.transform(AffineTransform.getScaleInstance(0.5, 0.5));
        ship = poly;

        Path2D.Double polyFlame = new Path2D.Double();
        polyFlame.moveTo(21, 0);
        polyFlame.lineTo(-21, 12);
        polyFlame.lineTo(-14, 10);
        polyFlame.lineTo(-14, -10);
        polyFlame.lineTo(-21, -12);
        polyFlame.closePath();
        polyFlame.moveTo(-25, 0);
        polyFlame.lineTo(-14, -5);
        polyFlame.lineTo(-14, 5);
        polyFlame.closePath();

        polyFlame.transform(AffineTransform.getScaleInstance(0.5, 0.5));
        flame = polyFlame;

        new ParticipantCountdownTimer(this, "move", 100);

        new ParticipantCountdownTimer(this, "seek", 750);
    }

    /**
     * Returns the x-coordinate of the point on the screen where the ship's nose is located.
     */
    public double getXNose ()
    {
        Point2D.Double point = new Point2D.Double(20, 0);
        transformPoint(point);
        return point.getX();
    }

    /**
     * Returns the x-coordinate of the point on the screen where the ship's nose is located.
     */
    public double getYNose ()
    {
        Point2D.Double point = new Point2D.Double(20, 0);
        transformPoint(point);
        return point.getY();
    }

    @Override
    protected Shape getOutline ()
    {
        if (flameOn = (flameOn) ? false : true)
        {
            return flame;
        }
        else
            return ship;
    }

    public Shape getShape ()
    {
        return ship;
    }

    /**
     * Customizes the base move method by imposing friction
     */
    @Override
    public void move ()
    {
        super.move();
        alien = controller.getAlien();
        if (alien != null)
        {
            // alien's angle relative to missile
            double angle = this.getAngle(alien.getX(), alien.getY()) - this.getRotation();
            // this.setRotation(this.getAngle(alien.getX(), alien.getY()));
            if (Math.abs(angle) < Math.PI / 16 || Math.abs(angle) == Math.PI)
                setRotation(this.getAngle(alien.getX(), alien.getY()));
            else
            {
                // If alien is clockwise to missile
                if (angle > 0)
                {
                    // If missile below alien turn clockwise
                    this.turnRight();
                }
                else // alien is counterClock to missile turn counterClock
                    this.turnLeft();
            }
        }
    }

    /**
     * Turns right by Pi/16 radians
     */
    public void turnRight ()
    {
        rotate(Math.PI / 30);
    }

    /**
     * Turns left by Pi/16 radians
     */
    public void turnLeft ()
    {
        rotate(-Math.PI / 30);
    }

    /**
     * Accelerates by SHIP_ACCELERATION
     */
    public void accelerate ()
    {
        accelerate(SHIP_ACCELERATION, SPEED_LIMIT / 2);
    }

    /**
     * When a Ship collides with a ShipDestroyer, it expires
     */
    @Override
    public void collidedWith (Participant p)
    {
        if (p instanceof ShipDestroyer)
        {
            // Expire the ship from the game
            Participant.expire(this);
        }
    }

    /**
     * This method is invoked when a ParticipantCountdownTimer completes its countdown.
     */
    @Override
    public void countdownComplete (Object payload)
    {
        // Give a burst of acceleration, then schedule another
        // burst for 200 msecs from now.
        if (payload.equals("move"))
        {
            accelerate();
            new ParticipantCountdownTimer(this, "move", 100);
        }

    }
}
