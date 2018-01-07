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
public class Enhanced extends Controller implements KeyListener, ActionListener
{
    private Ship ship;
    protected Missile mis;
    
    public Enhanced () 
    {
        ship = getShip();
    }

    @Override
    protected void shootBullet() 
    {
        addParticipant(getMissile()); 
    }
//    
//    @Override
//    protected void placeShip () 
//    {
//    }
}
