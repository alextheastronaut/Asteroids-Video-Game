package asteroids.game;

import static asteroids.game.Constants.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.Iterator;
import javax.swing.*;
import asteroids.participants.Ship;

/**
 * The area of the display in which the game takes place.
 */
@SuppressWarnings("serial")
public class Screen extends JPanel
{
    /** Legend that is displayed across the screen */
    private String legend;

    /** The labels */
    private String score, level;

    /** The number of lives */
    private int lives;

    /** Game controller */
    private Controller controller;

    /**
     * Creates an empty screen
     */
    public Screen (Controller controller)
    {
        this.controller = controller;
        legend = score = level = "";
        lives = 0;
        setPreferredSize(new Dimension(SIZE, SIZE));
        setMinimumSize(new Dimension(SIZE, SIZE));
        setBackground(Color.black);
        setForeground(Color.white);
        setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 120));
        setFocusable(true);
    }

    /**
     * Set the legend
     */
    public void setLegend (String legend)
    {
        this.legend = legend;
    }

    public void setLabels (int score, int level, int lives)
    {
        this.score = "Score: " + score;
        this.level = "Level: " + (level + 1);
        this.lives = lives;
    }

    /**
     * Paint the participants onto this panel
     */
    @Override
    public void paintComponent (Graphics graphics)
    {
        // Use better resolution
        Graphics2D g = (Graphics2D) graphics;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Do the default painting
        super.paintComponent(g);

        // Draw each participant in its proper place
        Iterator<Participant> iter = controller.getParticipants();
        while (iter.hasNext())
        {
            iter.next().draw(g);
        }
        Path2D.Double poly = new Path2D.Double();
        poly.moveTo(21, 0);
        poly.lineTo(-21, 12);
        poly.lineTo(-14, 10);
        poly.lineTo(-14, -10);
        poly.lineTo(-21, -12);
        poly.closePath();
        Shape ship = poly;

        AffineTransform trans = AffineTransform.getTranslateInstance(LABEL_HORIZONTAL_OFFSET,
                LABEL_VERTICAL_OFFSET + SHIP_HEIGHT);
        trans.concatenate(AffineTransform.getRotateInstance(-Math.PI / 2));
        ship = trans.createTransformedShape(ship);

        // Draw the legend across the middle of the panel
        int size = g.getFontMetrics().stringWidth(legend);
        g.drawString(legend, (SIZE - size) / 2, SIZE / 2);

        // Draw the score across the middle of the panel
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
        g.drawString(score, LABEL_HORIZONTAL_OFFSET, LABEL_VERTICAL_OFFSET);

        // Draw the score across the middle of the panel
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
        int size2 = g.getFontMetrics().stringWidth(level);
        g.drawString(level, SIZE - LABEL_HORIZONTAL_OFFSET - size2, LABEL_VERTICAL_OFFSET);

        for (int i = 0; i < lives; i++)
        {
            AffineTransform trans1 = AffineTransform.getTranslateInstance(i * (SHIP_WIDTH + SHIP_SEPARATION), 0);
            Shape border = trans1.createTransformedShape(ship);
            g.draw(border);
        }
    }
}
