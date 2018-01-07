package sounds;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.IOException;
import javax.sound.sampled.*;
import javax.swing.*;

/**
 * Demonstrates how to put sound files into a project so that they will be included when the project is exported, and
 * demonstrates how to play sounds.
 * 
 * @author Joe Zachary
 */
@SuppressWarnings("serial")
public class SoundDemo extends JFrame implements ActionListener
{
    /**
     * Launches a simple sound demo application
     */
    public static void main (String[] args)
    {
        SoundDemo demo = new SoundDemo();
        demo.setVisible(true);
    }

    /** A Clip that, when played, sounds like a weapon being fired */
    private Clip fireClip;

    /** A Clip that, when played repeatedly, sounds like a small saucer flying */
    private Clip smallSaucerClip;

    /**
     * Creates the demo.
     */
    public SoundDemo ()
    {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.PAGE_AXIS));
        JPanel fire = new JPanel();
        JButton fireButton = new JButton("Fire");
        fireButton.addActionListener(this);
        fireButton.setActionCommand("fire");
        fire.add(fireButton);
        buttons.add(fire);
        JPanel saucer = new JPanel();
        JButton saucerButton = new JButton("Small Saucer");
        saucerButton.setActionCommand("small saucer");
        saucerButton.addActionListener(this);
        saucer.add(saucerButton);
        buttons.add(saucer);
        setContentPane(buttons);
        pack();

        // We create the clips in advance so that there will be no delay
        // when we need to play them back. Note that the actual wav
        // files are stored in the "sounds" project.
        fireClip = createClip("/sounds/fire.wav");
        smallSaucerClip = createClip("/sounds/saucerSmall.wav");
    }

    /**
     * Creates an audio clip from a sound file.
     */
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
     * Plays sounds depending on which button was clicked.
     */
    @Override
    public void actionPerformed (ActionEvent e)
    {
        // We "rewind" the fireClip and play it.
        if (e.getActionCommand().equals("fire") && fireClip != null)
        {
            if (fireClip.isRunning())
            {
                fireClip.stop();
            }
            fireClip.setFramePosition(0);
            fireClip.start();
        }

        // We "rewind" the smallSaucerClip and play it ten times in a row.  To loop
        // continuously, pass Clip.LOOP_CONTINUOUSLY as the parameter.
        else if (e.getActionCommand().equals("small saucer") && smallSaucerClip != null)
        {
            if (smallSaucerClip.isRunning())
            {
                smallSaucerClip.stop();
            }
            smallSaucerClip.setFramePosition(0);
            smallSaucerClip.loop(10);            
        }
    }
}
