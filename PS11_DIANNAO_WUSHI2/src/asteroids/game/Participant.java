package asteroids.game;

import static asteroids.game.Constants.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * Represents a single moving element in an asteroids game. Each Participant object has an outline (used for drawing
 * it), a position, a rotation, and a velocity. A Participant can be either "active" or "expired". An active Participant
 * is displayed on the screen and can interact with other Participants. An expired Participant is not displayed on the
 * screen, cannot interact with other Participants, and will be removed automatically from the game
 * 
 * This is an abstract class, so it can be used only by creating a derived class that extends it and implements its
 * abstract methods
 */
public abstract class Participant
{
    /** Speed in pixels per second in the horizontal (x) and vertical (y) directions */
    private double speedX, speedY;

    /** Amount by which this Participant's outline is rotated in radians */
    private double rotation;

    /** Current x-coordinate of the Participant's center */
    private double x;

    /** Current y-coordinate of the Participant's center */
    private double y;

    /** Border of the Participant that is used for drawing */
    private Shape border;

    /** True if the Participant is expired */
    private boolean expired;

    /**
     * Constructs an active Participant with no velocity, rotation, or border.
     */
    protected Participant ()
    {
        speedX = 0;
        speedY = 0;
        rotation = 0;
        x = 0;
        y = 0;
        border = null;
        expired = false;
    }

    /**
     * Marks p as expired. If p is null, does nothing.
     */
    public static void expire (Participant p)
    {
        if (p != null)
        {
            p.setExpired();
        }
    }

    /**
     * Marks this participant as expired
     */
    private void setExpired ()
    {
        expired = true;
    }

    /**
     * Report whether this participant has expired.
     */
    public boolean isExpired ()
    {
        return expired;
    }

    /**
     * Sets the velocity of this participant. The speed is in pixels per frame refresh and the direction is in radians.
     */
    public void setVelocity (double speed, double direction)
    {
        direction = normalize(direction);
        speedX = Math.cos(direction) * speed;
        speedY = Math.sin(direction) * speed;
    }

    /**
     * Sets the direction, leaving the speed unchanged.
     */
    public void setDirection (double direction)
    {
        direction = normalize(direction);
        double speed = getSpeed();
        speedX = Math.cos(direction) * speed;
        speedY = Math.sin(direction) * speed;
    }

    /**
     * Sets the speed, leaving the direction unchanged.
     */
    public void setSpeed (double speed)
    {
        double direction = getDirection();
        speedX = Math.cos(direction) * speed;
        speedY = Math.sin(direction) * speed;
    }

    /**
     * Returns the direction in radians
     */
    public double getDirection ()
    {
        return normalize(Math.atan2(speedY, speedX));
    }
    
    public double getAngle(double x2, double y2) 
    {
        return normalize(Math.atan2(y2 - y, x2 - x));
    }

    /**
     * Return the speed, in pixels per frame
     */
    public double getSpeed ()
    {
        return Math.sqrt(speedX * speedX + speedY * speedY);
    }

    /**
     * Sets the rotation (in radians) of this Participant
     */
    public void setRotation (double radians)
    {
        rotation = normalize(radians);
    }

    /**
     * Rotates this Participant by delta radians.
     */
    public void rotate (double delta)
    {
        rotation = normalize(rotation + delta);
    }

    /**
     * Returns the rotation of this participant
     */
    public double getRotation ()
    {
        return rotation;
    }

    /**
     * Adjusts the angle so it is between 0 and 2 Pi
     */
    public static double normalize (double angle)
    {
        while (angle > 0)
        {
            angle -= 2 * Math.PI;
        }
        while (angle < 0)
        {
            angle += 2 * Math.PI;
        }
        return angle;
    }

    /**
     * Accelerates in the direction that the participant is rotated. Participants cannot accelerate beyond the speed
     * limit.
     */
    public void accelerate (double delta, double speedLimit)
    {
        // Compute new speeds
        speedX += delta * Math.cos(rotation);
        speedY += delta * Math.sin(rotation);

        // Enforce the speed limit
        if (getSpeed() > speedLimit)
        {
            double direction = getDirection();
            speedX = speedLimit * Math.cos(direction);
            speedY = speedLimit * Math.sin(direction);
        }
    }

    /**
     * Simulates friction by accelerating the participant opposite to its direction of motion.
     */
    public void applyFriction (double coefficientOfFriction)
    {
        if (speedX != 0 || speedY != 0)
        {
            double speed = getSpeed();
            double deltaX = coefficientOfFriction * speedX / speed;
            double deltaY = coefficientOfFriction * speedY / speed;
            speedX = (Math.abs(deltaX) > Math.abs(speedX)) ? 0 : (speedX + deltaX);
            speedY = (Math.abs(deltaY) > Math.abs(speedY)) ? 0 : (speedY + deltaY);
        }
    }

    /**
     * Sets the position of the center of the participant
     */
    public void setPosition (double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the x coordinate of the center of the participant
     */
    public double getX ()
    {
        return x;
    }

    /**
     * Gets the y coordinate of the center of the participant
     */
    public double getY ()
    {
        return y;
    }

    /**
     * Returns a Shape object that describes the outline of this Participant. The center of the Shape should be at
     * coordinate (0,0). The center is the reference used when the Shape is moved or rotated. This method is called
     * frequently, so it should be efficient. The Shape that is returned will not be modified, so it can be cached.
     */
    protected abstract Shape getOutline ();

    /**
     * Takes the appropriate action for a collision of this Participant with p.
     */
    public abstract void collidedWith (Participant p);

    /**
     * This method is called when a {@link asteroids.game.ParticipantCountdownTimer} that was constructed for this
     * Participant completes its countdown, so long as this Participant is not expired. The method should take
     * appropriate action.
     */
    public void countdownComplete (Object payload)
    {
    }

    /**
     * Moves this participant to reflect one tick of the clock.
     */
    public void move ()
    {
        // Get the original outline
        Shape original = getOutline();

        // Change the position to reflect participant motion
        x += speedX;
        y += speedY;

        // Translate and rotate the original to reflect the accumulated motion
        AffineTransform trans = AffineTransform.getTranslateInstance(x, y);
        trans.concatenate(AffineTransform.getRotateInstance(rotation));
        border = trans.createTransformedShape(original);

        // If the element has gone sufficiently far out of bounds, move it to
        // the other side of the screen. This change will take effect next time.
        Rectangle2D bounds = border.getBounds2D();
        if (bounds.getMaxX() < 0)
        {
            x += SIZE + (bounds.getMaxX() - bounds.getMinX());
        }
        if (bounds.getMinX() >= SIZE)
        {
            x += -SIZE - (bounds.getMaxX() - bounds.getMinX());
        }
        if (bounds.getMaxY() < 0)
        {
            y += SIZE + (bounds.getMaxY() - bounds.getMinY());
        }
        if (bounds.getMinY() >= SIZE)
        {
            y += -SIZE - (bounds.getMaxY() - bounds.getMinY());
        }
    }

    /**
     * Transforms the point just like the participant is transformed before it is displayed. This can be used to figure
     * out where some point of the participant is going to be located after it is transformed. (This can be useful for
     * computing the tip of a ship, for example.)
     */
    public void transformPoint (Point2D.Double point)
    {
        AffineTransform trans = AffineTransform.getTranslateInstance(x, y);
        trans.concatenate(AffineTransform.getRotateInstance(rotation));
        trans.transform(point, point);
    }

    /**
     * Reports whether this participant overlaps with p.
     */
    public boolean overlaps (Participant p)
    {
        Area a = new Area(border);
        a.intersect(new Area(p.border));
        return !a.isEmpty();
    }

    /**
     * Draws this participant
     */
    public void draw (Graphics2D g)
    {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY); 
        if (border == null)
        {
            border = getOutline();
        }
        g.draw(border);
    }
}
