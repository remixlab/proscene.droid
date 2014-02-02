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

import java.util.ArrayList;

/**
 * A timing handler holds a {@link #timerPool()} and an {@link #animationPool()}.
 * The timer pool are all the tasks scheduled to be performed in the future
 * (one single time or periodically). The animation pool are all the objects
 * that implement an animation callback function.
 * <p>
 * FPSTiming implements single threaded timers by taking the application frame rate
 * as a clock. Each application using the library should: 1. Instantiate a single
 * TimingHandler; 2. Schedule some tasks to be executed periodically
 * ({@link #registerJob(AbstractTimerJob)} ); 3. Register some animation objects
 * ({@link #registerAnimation(Animatable)}); and, 4. Call {@link #handle()}
 * from within the application main event loop.
 */
public class TimingHandler {
	// protected boolean singleThreadedTaskableTimers;
	// T i m e r P o o l
	protected ArrayList<AbstractTimerJob> timerPool;
	public static long frameCount;
	public static float frameRate;
	protected long frameRateLastMillis;

	// A N I M A T I O N
	protected ArrayList<Animatable> animationPool;

	public static final String prettyVersion = "1.0.0";

	public static final String version = "1";
  
	/**
	 * Main constructor.
	 */
	public TimingHandler() {
		frameCount = 0;
		frameRate = 10;
		frameRateLastMillis = System.currentTimeMillis();
		// drawing timer pool
		timerPool = new ArrayList<AbstractTimerJob>();
		animationPool = new ArrayList<Animatable>();
	}

	/**
	 * CAnstructor that takes and registers an animation object.
	 */
	public TimingHandler(Animatable aObject) {
		this();
		this.registerAnimation(aObject);
	}

	/**
	 * Handler's main method. It should be called from within your main
	 * event loop. It does the following: 1. Recomputes the frame rate;
	 * 2. Executes the all timers (those in the {@link #timerPool()})
	 * callback functions; and, 3. Performs all the animated objects
	 * (those in the {@link #animationPool()}) animation functions.
	 */
	public void handle() {
		updateFrameRate();
		for (AbstractTimerJob tJob : timerPool)
			if (tJob.timer() != null)
				if (tJob.timer() instanceof SeqTaskableTimer)
					((SeqTaskableTimer) tJob.timer()).execute();
		// Animation
		for (Animatable aObj : animationPool)
			if (aObj.isAnimationStarted())
				if (aObj.timer().trigggered())
					if (!aObj.invokeAnimationHandler())
						aObj.animate();
	}

	/**
	 * Returns the timer pool.
	 */
	public ArrayList<AbstractTimerJob> timerPool() {
		return timerPool;
	}

	/**
	 * Register a task in the timer pool and creates a sequential timer for it.
	 */
	public void registerJob(AbstractTimerJob job) {
		job.setTimer(new SeqTaskableTimer(this, job));
		timerPool.add(job);
	}

	/**
	 * Register a task in the timer pool with the given timer.
	 */
	public void registerJob(AbstractTimerJob job, Timable timer) {
		job.setTimer(timer);
		timerPool.add(job);
	}

	/**
	 * Unregisters the timer. Alternatively, you may unregister
	 * the job related to this timer.
	 * 
	 * @see #unregisterJob(AbstractTimerJob)
	 */
	public void unregisterJob(SeqTaskableTimer t) {
		timerPool.remove(t.timerJob());
	}

	/**
	 * Unregisters the job. Alternatively, you may unregister
	 * the timer related to this job.
	 * 
	 * @see #unregisterJob(SeqTaskableTimer)
	 */
	public void unregisterJob(AbstractTimerJob job) {
		timerPool.remove(job);
	}

	/**
	 * Returns {@code true} if the job is registered and {@code false} otherwise.
	 */
	public boolean isJobRegistered(AbstractTimerJob job) {
		return timerPool.contains(job);
	}

	/**
	 * Recomputes the frame rate based upon the frequency at which {@link #handle()}
	 * is called from within the application main event loop. The frame rate is
	 * needed to sync all timing operations.
	 */
	protected void updateFrameRate() {
		long now = System.currentTimeMillis();
		if (frameCount > 1) {
			// update the current frameRate
			double rate = 1000.0 / ((now - frameRateLastMillis) / 1000.0);
			float instantaneousRate = (float) rate / 1000.0f;
			frameRate = (frameRate * 0.9f) + (instantaneousRate * 0.1f);
		}
		frameRateLastMillis = now;
		frameCount++;
	}

	/**
	 * Returns the approximate frame rate of the software as it executes. The
	 * initial value is 10 fps and is updated with each frame. The value is
	 * averaged (integrated) over several frames. As such, this value won't be
	 * valid until after 5-10 frames.
	 */
	public float frameRate() {
		return frameRate;
	}

	/**
	 * Returns the number of frames displayed since the program started.
	 */
	public long frameCount() {
		return frameCount;
	}

	/**
	 * Converts all registered timers to single-threaded timers.
	 */
	public void restoreTimers() {
		boolean isActive;

		for (AbstractTimerJob job : timerPool) {
			long period = 0;
			boolean rOnce = false;
			isActive = job.isActive();
			if (isActive) {
				period = job.period();
				rOnce = job.timer().isSingleShot();
			}
			job.stop();
			job.setTimer(new SeqTaskableTimer(this, job));
			if (isActive) {
				if (rOnce)
					job.runOnce(period);
				else
					job.run(period);
			}
		}

		System.out.println("single threaded timers set");
	}

	// Animation -->

	/**
	 * Returns all the animated objects registered at the handler.
	 */
	public ArrayList<Animatable> animationPool() {
		return animationPool;
	}

	/**
	 * Registers the animation object.
	 */
	public void registerAnimation(Animatable object) {
		if (object.timingHandler() != this)
			object.setTimingHandler(this);
		animationPool.add(object);
	}

	/**
	 * Unregisters the animation object.
	 */
	public void unregisterAnimation(Animatable object) {
		animationPool.remove(object);
	}

	/**
	 * Returns {@code true} if the animation object is registered
	 * and {@code false} otherwise.
	 */
	public boolean isAnimationRegistered(Animatable object) {
		return animationPool.contains(object);
	}
}
