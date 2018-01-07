/**
 * A {@link asteroids.game.Participant} class that implements one of these interfaces
 * "marks" its objects as being "destroyers" of the objects
 * of some other Participant class.  (Note that all of the interfaces in 
 * this package are are completely empty interfaces that contain no methods.  
 * Interfaces of this type are called "Markers".)
 * 
 * For example, suppose that the {@link asteroids.participants.Ship} class implements the 
 * {@link asteroids.destroyers.AsteroidDestroyer} interface.  In
 * doing so, it is saying that a {@link asteroids.participants.Ship} object should destroy an 
 * {@link asteroids.participants.Asteroid} object when the two
 * objects collide during a game.
 * 
 * Each participant class should implement as many of the destroyer interfaces as is
 * appropriate.  The code that controls the progress of a game uses this information
 * to determine what to do when two participants collide.
 */
package asteroids.destroyers;