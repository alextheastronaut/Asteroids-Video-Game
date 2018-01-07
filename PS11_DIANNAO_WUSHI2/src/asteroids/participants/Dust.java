package asteroids.participants;

import static asteroids.game.Constants.*;
import java.awt.Shape;
import java.awt.geom.Path2D;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;

public class Dust extends Participant
{

    /** The shape of debris */
    private Shape outline;

    public Dust (double x, double y)
    {
        //Sets speed of debris
        setPosition(x, y);
        setVelocity(RANDOM.nextDouble() * 4, RANDOM.nextDouble() * 2 * Math.PI);
        setRotation(2 * Math.PI * RANDOM.nextDouble());

        Path2D.Double poly = new Path2D.Double();

        poly.moveTo(1, 1);
        poly.lineTo(-1, 1);
        poly.lineTo(-1, -1);
        poly.lineTo(1, -1);
        poly.closePath();

        this.outline = poly;

        new ParticipantCountdownTimer(this, "expire", RANDOM.nextInt(END_DELAY));
    }

    @Override
    protected Shape getOutline ()
    {
        return this.outline;
    }

    @Override
    public void collidedWith (Participant p)
    {
        // TODO Auto-generated method stub
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
