package asteroids.game;

import javax.swing.*;
import java.awt.event.*;

/**
 * Provides objects that wait for a certain amount of time to pass before making a callback to a Participant's
 * countdownComplete method.
 */
public class ParticipantCountdownTimer implements ActionListener
{
    /** Payload that is passed to the countdownComplete method when it is called */
    private Object payload;

    /** Internal timer */
    private Timer timer;

    /** Participant to be notified when time expires */
    private Participant participant;

    /**
     * Constructs an object that waits for the given number of milliseconds to pass before invoking the
     * countdownComplete method on p, passing null as its parameter. The call is not made if p has expired.
     */
    public ParticipantCountdownTimer (Participant p, int msecs)
    {
        this(p, null, msecs);
    }

    /**
     * Constructs an object that waits for the given number of milliseconds to pass before invoking the
     * countdownComplete method on p, passing the payload as its parameter. The call is not made if p has expired.
     */
    public ParticipantCountdownTimer (Participant p, Object payload, int msecs)
    {
        this.participant = p;
        this.payload = payload;
        timer = new Timer(msecs, this);
        timer.start();
    }

    /**
     * When the interval has passed, stops the timer and makes the callback, as long as the participant has not expired.
     */
    @Override
    public void actionPerformed (ActionEvent e)
    {
        timer.stop();
        if (!participant.isExpired())
        {
            participant.countdownComplete(payload);
        }
    }
}
