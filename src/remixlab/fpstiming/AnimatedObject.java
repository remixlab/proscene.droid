/*******************************************************************************
 * FPSTiming (version 1.0.0)
 * Copyright (c) 2014 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *     
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package remixlab.fpstiming;

/**
 * Class implementing the main animation behavior.
 */
public class AnimatedObject implements Animatable {
	protected SeqTimer animationTimer;
	protected boolean animationStarted;
	protected long animationPeriod;
	protected TimingHandler handler;

	/**
	 * Constructs an animated object with a default {@link #animationPeriod()}
	 * of 40 milliseconds (25Hz). The handler should explicitly be defined
	 * afterwards ({@link #setTimingHandler(TimingHandler)}).
	 */
	public AnimatedObject() {
		setAnimationPeriod(40, false); // 25Hz
		stopAnimation();
	}

	/**
	 * Constructs an animated object with a default {@link #animationPeriod()}
	 * of 40 milliseconds (25Hz).
	 */
	public AnimatedObject(TimingHandler handler) {
		setTimingHandler(handler);
		setAnimationPeriod(40, false); // 25Hz
		stopAnimation();
	}

	@Override
	public void setTimingHandler(TimingHandler h) {
		handler = h;
		handler.registerAnimation(this);
		animationTimer = new SeqTimer(handler);
	}

	@Override
	public TimingHandler timingHandler() {
		return handler;
	}

	@Override
	public SeqTimer timer() {
		return animationTimer;
	}

	/**
	 * Return {@code true} when the animation loop is started.
	 * <p>
	 * The timing handler will check when {@link #isAnimationStarted()}
	 * and then called the animation callback method every
	 * {@link #animationPeriod()} milliseconds.
	 * <p>
	 * Use {@link #startAnimation()}, {@link #stopAnimation()} or
	 * {@link #toggleAnimation()} to change this value.
	 * 
	 * @see #startAnimation()
	 * @see #animate()
	 */
	@Override
	public boolean isAnimationStarted() {
		return animationStarted;
	}

	/**
	 * The animation loop period, in milliseconds. When
	 * {@link #isAnimationStarted()}, this is the delay that takes place between
	 * two consecutive iterations of the animation loop.
	 * <p>
	 * This delay defines a target frame rate that will only be achieved if your
	 * {@link #animate()} methods is fast enough.
	 * <p>
	 * Default value is 40 milliseconds (25 Hz).
	 * <p>
	 * <b>Note:</b> This value is taken into account only the next time you call
	 * {@link #startAnimation()}. If {@link #isAnimationStarted()}, you should
	 * {@link #stopAnimation()} first. See {@link #restartAnimation()} and
	 * {@link #setAnimationPeriod(long, boolean)}.
	 * 
	 * @see #setAnimationPeriod(long, boolean)
	 */
	@Override
	public long animationPeriod() {
		return animationPeriod;
	}

	/**
	 * Convenience function that simply calls
	 * {@code period(period, true)}.
	 * 
	 * @see #setAnimationPeriod(long, boolean)
	 */
	@Override
	public void setAnimationPeriod(long period) {
		setAnimationPeriod(period, true);
	}

	/**
	 * Sets the {@link #animationPeriod()}, in milliseconds. If restart is
	 * {@code true} and {@link #isAnimationStarted()} then
	 * {@link #restartAnimation()} is called.
	 * 
	 * @see #startAnimation()
	 */
	@Override
	public void setAnimationPeriod(long period, boolean restart) {
		if (period > 0) {
			animationPeriod = period;
			if (isAnimationStarted() && restart)
				restartAnimation();
		}
	}

	/**
	 * Stops animation.
	 * 
	 * @see #isAnimationStarted()
	 */
	@Override
	public void stopAnimation() {
		animationStarted = false;
		if (timer() != null)
			timer().stop();
	}

	/**
	 * Starts the animation loop.
	 * 
	 * @see #isAnimationStarted()
	 */
	@Override
	public void startAnimation() {
		animationStarted = true;
		if (timer() != null)
			timer().run(animationPeriod);
	}

	/**
	 * Restart the animation.
	 * <p>
	 * Simply calls {@link #stopAnimation()} and then {@link #startAnimation()}.
	 */
	@Override
	public void restartAnimation() {
		stopAnimation();
		startAnimation();
	}

	/**
	 * Calls {@link #startAnimation()} or {@link #stopAnimation()}, depending on
	 * {@link #isAnimationStarted()}.
	 */
	@Override
	public void toggleAnimation() {
		if (isAnimationStarted())
			stopAnimation();
		else
			startAnimation();
	}

	@Override
	public void animate() {
	}

	@Override
	public boolean invokeAnimationHandler() {
		return false;
	}
}
