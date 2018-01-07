package asteroids.participants;

import java.awt.Shape;
import java.awt.geom.Path2D;
import asteroids.destroyers.AlienDestroyer;
import asteroids.destroyers.AsteroidDestroyer;
import asteroids.destroyers.ShipDestroyer;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;
import static asteroids.game.Constants.*;

/**
 * Represents the bullets
 * 
 * @author Alex
 *
 */
public class AlienBullets extends Participant implements AsteroidDestroyer, ShipDestroyer
{

    /** The shape of the bullet */
    private Shape outline;

    public AlienBullets (int speed, double startX, double startY, double dir, int time)
    {
        setPosition(startX, startY);
        setVelocity(speed, dir);

        Path2D.Double poly = new Path2D.Double();

        poly.moveTo(2, 2);
        poly.lineTo(-2, 2);
        poly.lineTo(-2, -2);
        poly.lineTo(2, -2);
        poly.closePath();

        outline = poly;

        new ParticipantCountdownTimer(this, "expire", BULLET_DURATION);
    }

    /**
     * When an Asteroid collides with an AsteroidDestroyer, it expires.
     */
    @Override
    public void collidedWith (Participant p)
    {
        if (p instanceof AlienDestroyer)
        {
            // Expire the asteroid
            Participant.expire(this);
        }
    }

    @Override
    protected Shape getOutline ()
    {
        return outline;
    }

    @Override
    public void countdownComplete (Object payload)
    {
        if (payload.equals("expire"))
        {
            Participant.expire(this);
        }
    }
}
