package org.semanticweb.rulewerk.core.reasoner;

/*-
 * #%L
 * Rulewerk Examples
 * %%
 * Copyright (C) 2018 - 2020 Rulewerk Developers
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class for keeping CPU and system times. The class has a number of features
 * that can be used to measure and aggregate times across many threads and many
 * methods.
 * 
 * @implNote This file originates from the ELK Reasoner, where more extensive thread-aware
 * timing was required. The file contains commented out functions from that source that
 * could be used to activate those features here.
 * 
 * @author Markus Kroetzsch
 */
public class Timer {

	//private static Logger LOGGER = LoggerFactory.getLogger(Timer.class);

	/** Flag for indicating that no times should be taken (just count runs). */
	public static final int RECORD_NONE = 0x00000000;
	/** Flag for indicating that CPU time should be taken. */
	public static final int RECORD_CPUTIME = 0x00000001;
	/** Flag for indicating that wall clock time should be taken. */
	public static final int RECORD_WALLTIME = 0x00000002;
	/** Flag for indicating that all supported times should be taken. */
	public static final int RECORD_ALL = RECORD_CPUTIME | RECORD_WALLTIME;

	static final ThreadMXBean tmxb = ManagementFactory.getThreadMXBean();

	static final ConcurrentHashMap<Timer, Timer> registeredTimers = new ConcurrentHashMap<Timer, Timer>();

	protected final String name;
	protected final long threadId;
	protected final int todoFlags;

	protected long currentStartCpuTime = -1;
	protected long currentStartWallTime = -1;
	protected boolean isRunning = false;
	protected long totalCpuTime = 0;
	protected long totalWallTime = 0;
	protected int measurements = 0;
	protected int threadCount = 0;

	/**
	 * Constructor. Every timer is identified by three things: a string name, an
	 * integer for flagging its tasks (todos), and a thread id (long).
	 * 
	 * Tasks can be flagged by a disjunction of constants like RECORD_CPUTIME and
	 * RECORD_WALLTIME. Only times for which an according flag is set will be
	 * recorded.
	 * 
	 * The thread id can be the actual id of the thread that is measured, or 0
	 * (invalid id) to not assign the timer to any thread. In this case, no CPU time
	 * measurement is possible since Java does not allow us to measure the total CPU
	 * time across all threads.
	 * 
	 * @param name
	 * @param todoFlags
	 * @param threadId
	 */
	public Timer(String name, int todoFlags, long threadId) {
		this.name = name;
		this.todoFlags = todoFlags;
		this.threadId = threadId;

		if (!tmxb.isThreadCpuTimeEnabled()) {
			tmxb.setThreadCpuTimeEnabled(true);
		}
	}

	public Timer(String name) {
		this(name, RECORD_ALL, Thread.currentThread().getId());
	}

	/**
	 * 
	 * @param name
	 * @param todoFlags
	 * @return a new {@link Timer} for the current thread
	 */
	static public Timer getTimerForCurrentThread(String name, int todoFlags) {
		return new Timer(name, todoFlags, Thread.currentThread().getId());
	}

	/**
	 * Get the total recorded CPU time in nanoseconds.
	 * 
	 * @return recorded CPU time in nanoseconds
	 */
	public long getTotalCpuTime() {
		return totalCpuTime;
	}

	public long getAvgCpuTime() {
		return totalCpuTime > 0 && measurements > 0 ? totalCpuTime / measurements : -1;
	}

	/**
	 * Get the string name of the timer.
	 * 
	 * @return string name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the ID of the thread for which this timer was created.
	 * 
	 * @return thread ID
	 */
	public long getThreadId() {
		return threadId;
	}

	/**
	 * Get the total recorded wall clock time in nanoseconds.
	 * 
	 * @return recorded wall time in nanoseconds
	 */
	public long getTotalWallTime() {
		return totalWallTime;
	}

	public long getAvgWallTime() {
		return totalWallTime > 0 && measurements > 0 ? totalWallTime / measurements : -1;
	}

	/**
	 * Return true if the timer is running.
	 * 
	 * @return true if running
	 */
	public boolean isRunning() {
		return isRunning;
	}

	/**
	 * Start the timer.
	 */
	public synchronized void start() {
		if ((todoFlags & RECORD_CPUTIME) != 0) {
			currentStartCpuTime = getThreadCpuTime(threadId);
		} else {
			currentStartCpuTime = -1;
		}
		if ((todoFlags & RECORD_WALLTIME) != 0) {
			currentStartWallTime = System.nanoTime();
		} else {
			currentStartWallTime = -1;
		}
		isRunning = true;
	}

	/**
	 * Stop the timer (if running) and reset all recorded values.
	 */
	public synchronized void reset() {
		currentStartCpuTime = -1;
		currentStartWallTime = -1;
		totalCpuTime = 0;
		totalWallTime = 0;
		measurements = 0;
		isRunning = false;
		threadCount = 0;
	}

	/**
	 * Stop the timer and return the CPU time that has passed since it had last been
	 * started. The total time (both system and CPU) of all start-stop cycles is
	 * recorded with the timer.
	 * 
	 * @return CPU time that the timer was running, or -1 if timer not running or
	 *         CPU time unavailable for other reasons
	 */
	public synchronized long stop() {
		long totalTime = -1;

		if ((todoFlags & RECORD_CPUTIME) != 0 && (currentStartCpuTime != -1)) {
			long cpuTime = getThreadCpuTime(threadId);
			if (cpuTime != -1) { // may fail if thread already dead
				totalTime = cpuTime - currentStartCpuTime;
				totalCpuTime += totalTime;
			}
		}

		if ((todoFlags & RECORD_WALLTIME) != 0 && (currentStartWallTime != -1)) {
			long wallTime = System.nanoTime();
			totalWallTime += wallTime - currentStartWallTime;
		}

		if (isRunning) {
			measurements += 1;
			isRunning = false;
		}

		currentStartWallTime = -1;
		currentStartCpuTime = -1;

		return totalTime;
	}

//	/**
//	 * Print logging information for the timer. The log only shows the recorded time
//	 * of the completed start-stop cycles. If the timer is still running, then it
//	 * will not be stopped to add the currently measured time to the output but a
//	 * warning will be logged.
//	 * 
//	 */
//	public void log() {
//		if (LOGGER.isInfoEnabled()) {
//			String timerLabel;
//			if (threadId != 0) {
//				timerLabel = name + " (thread " + threadId + ")";
//			} else if (threadCount > 1) {
//				timerLabel = name + " (over " + threadCount + " threads)";
//			} else {
//				timerLabel = name;
//			}
//
//			if (todoFlags == RECORD_NONE) {
//				LOGGER.info("Timer " + timerLabel + " recorded " + measurements + " run(s), no times taken");
//			} else {
//				String labels = "";
//				String values = "";
//				String separator;
//
//				if ((todoFlags & RECORD_CPUTIME) != 0 && threadId != 0) {
//					labels += "CPU";
//					values += totalCpuTime / 1000000;
//					separator = "/";
//				} else {
//					separator = "";
//				}
//				if ((todoFlags & RECORD_WALLTIME) != 0) {
//					labels += separator + "Wall";
//					values += separator + totalWallTime / 1000000;
//				}
//				if ((todoFlags & RECORD_CPUTIME) != 0 && threadId != 0) {
//					labels += "/CPU avg";
//					values += "/" + (float) (totalCpuTime) / measurements / 1000000;
//				}
//				if ((todoFlags & RECORD_WALLTIME) != 0) {
//					labels += "/Wall avg";
//					values += "/" + (float) (totalWallTime) / measurements / 1000000;
//				}
//				if (threadCount > 1) {
//					if ((todoFlags & RECORD_CPUTIME) != 0 && threadId != 0) {
//						labels += "/CPU per thread";
//						values += "/" + (float) (totalCpuTime) / threadCount / 1000000;
//					}
//					if ((todoFlags & RECORD_WALLTIME) != 0) {
//						labels += "/Wall per thread";
//						values += "/" + (float) (totalWallTime) / threadCount / 1000000;
//					}
//				}
//
//				LOGGER.info(
//						"Time for " + timerLabel + " for " + measurements + " run(s) " + labels + " (ms): " + values);
//			}
//
//			if (isRunning) {
//				LOGGER.warn("Timer " + timerLabel + " logged while it was still running");
//			}
//		}
//	}
//
//	/**
//	 * Start a timer of the given string name for all todos and the current thread.
//	 * If no such timer exists yet, then it will be newly created.
//	 * 
//	 * @param timerName the name of the timer
//	 */
//	public static void startNamedTimer(String timerName) {
//		getNamedTimer(timerName).start();
//	}
//
//	/**
//	 * Start a timer of the given string name for the current thread. If no such
//	 * timer exists yet, then it will be newly created.
//	 * 
//	 * @param timerName the name of the timer
//	 * @param todoFlags
//	 */
//	public static void startNamedTimer(String timerName, int todoFlags) {
//		getNamedTimer(timerName, todoFlags).start();
//	}
//
//	/**
//	 * Start a timer of the given string name for the current thread. If no such
//	 * timer exists yet, then it will be newly created.
//	 * 
//	 * @param timerName the name of the timer
//	 * @param todoFlags
//	 * @param threadId  of the thread to track, or 0 if only system clock should be
//	 *                  tracked
//	 */
//	public static void startNamedTimer(String timerName, int todoFlags, long threadId) {
//		getNamedTimer(timerName, todoFlags, threadId).start();
//	}
//
//	/**
//	 * Stop a timer of the given string name for all todos and the current thread.
//	 * If no such timer exists, -1 will be returned. Otherwise the return value is
//	 * the CPU time that was measured.
//	 * 
//	 * @param timerName the name of the timer
//	 * @return CPU time if timer existed and was running, and -1 otherwise
//	 */
//	public static long stopNamedTimer(String timerName) {
//		return stopNamedTimer(timerName, RECORD_ALL, Thread.currentThread().getId());
//	}
//
//	/**
//	 * Stop a timer of the given string name for the current thread. If no such
//	 * timer exists, -1 will be returned. Otherwise the return value is the CPU time
//	 * that was measured.
//	 * 
//	 * @param timerName the name of the timer
//	 * @param todoFlags
//	 * @return CPU time if timer existed and was running, and -1 otherwise
//	 */
//	public static long stopNamedTimer(String timerName, int todoFlags) {
//		return stopNamedTimer(timerName, todoFlags, Thread.currentThread().getId());
//	}
//
//	/**
//	 * Stop a timer of the given string name for the given thread. If no such timer
//	 * exists, -1 will be returned. Otherwise the return value is the CPU time that
//	 * was measured.
//	 * 
//	 * @param timerName the name of the timer
//	 * @param todoFlags
//	 * @param threadId  of the thread to track, or 0 if only system clock should be
//	 *                  tracked
//	 * @return CPU time if timer existed and was running, and -1 otherwise
//	 */
//	public static long stopNamedTimer(String timerName, int todoFlags, long threadId) {
//		Timer key = new Timer(timerName, todoFlags, threadId);
//		if (registeredTimers.containsKey(key)) {
//			return registeredTimers.get(key).stop();
//		} else {
//			return -1;
//		}
//	}
//
//	/**
//	 * Reset a timer of the given string name for all todos and the current thread.
//	 * If no such timer exists yet, then it will be newly created.
//	 * 
//	 * @param timerName the name of the timer
//	 */
//	public static void resetNamedTimer(String timerName) {
//		getNamedTimer(timerName).reset();
//	}
//
//	/**
//	 * Reset a timer of the given string name for the current thread. If no such
//	 * timer exists yet, then it will be newly created.
//	 * 
//	 * @param timerName the name of the timer
//	 * @param todoFlags
//	 */
//	public static void resetNamedTimer(String timerName, int todoFlags) {
//		getNamedTimer(timerName, todoFlags).reset();
//	}
//
//	/**
//	 * Reset a timer of the given string name for the given thread. If no such timer
//	 * exists yet, then it will be newly created.
//	 * 
//	 * @param timerName the name of the timer
//	 * @param todoFlags
//	 * @param threadId  of the thread to track, or 0 if only system clock should be
//	 *                  tracked
//	 */
//	public static void resetNamedTimer(String timerName, int todoFlags, long threadId) {
//		getNamedTimer(timerName, todoFlags, threadId).reset();
//	}
//
//	/**
//	 * Get a timer of the given string name that takes all possible times (todos)
//	 * for the current thread. If no such timer exists yet, then it will be newly
//	 * created.
//	 * 
//	 * @param timerName the name of the timer
//	 * @return timer
//	 */
//	public static Timer getNamedTimer(String timerName) {
//		return getNamedTimer(timerName, RECORD_ALL, Thread.currentThread().getId());
//	}
//
//	/**
//	 * Returns all registered timers
//	 * 
//	 * @return an iterable collection of named timers
//	 */
//	public static Iterable<Timer> getNamedTimers() {
//		return registeredTimers.keySet();
//	}
//
//	/**
//	 * Get a timer of the given string name and todos for the current thread. If no
//	 * such timer exists yet, then it will be newly created.
//	 * 
//	 * @param timerName the name of the timer
//	 * @param todoFlags
//	 * @return timer
//	 */
//	public static Timer getNamedTimer(String timerName, int todoFlags) {
//		return getNamedTimer(timerName, todoFlags, Thread.currentThread().getId());
//	}
//
//	/**
//	 * Get a timer of the given string name for the given thread. If no such timer
//	 * exists yet, then it will be newly created.
//	 * 
//	 * @param timerName the name of the timer
//	 * @param todoFlags
//	 * @param threadId  of the thread to track, or 0 if only system clock should be
//	 *                  tracked
//	 * @return timer
//	 */
//	public static Timer getNamedTimer(String timerName, int todoFlags, long threadId) {
//		Timer key = new Timer(timerName, todoFlags, threadId);
//		Timer previous = registeredTimers.putIfAbsent(key, key);
//		if (previous != null) {
//			return previous;
//		}
//		// else
//		return key;
//	}
//
//	/**
//	 * Collect the total times measured by all known named timers of the given name.
//	 * 
//	 * @param timerName
//	 * @return timer
//	 */
//	public static Timer getNamedTotalTimer(String timerName) {
//		long totalCpuTime = 0;
//		long totalSystemTime = 0;
//		int measurements = 0;
//		int threadCount = 0;
//		int todoFlags = RECORD_NONE;
//		Timer previousTimer = null;
//		for (Map.Entry<Timer, Timer> entry : registeredTimers.entrySet()) {
//			if (entry.getValue().name.equals(timerName)) {
//				previousTimer = entry.getValue();
//				threadCount += 1;
//				totalCpuTime += previousTimer.totalCpuTime;
//				totalSystemTime += previousTimer.totalWallTime;
//				measurements += previousTimer.measurements;
//				todoFlags |= previousTimer.todoFlags;
//			}
//		}
//
//		if (threadCount == 1) {
//			return previousTimer;
//		} else {
//			Timer result = new Timer(timerName, todoFlags, 0);
//			result.totalCpuTime = totalCpuTime;
//			result.totalWallTime = totalSystemTime;
//			result.measurements = measurements;
//			result.threadCount = threadCount;
//			return result;
//		}
//	}
//
//	public static void logAllNamedTimers(String timerName) {
//		for (Map.Entry<Timer, Timer> entry : registeredTimers.entrySet()) {
//			if (entry.getValue().name.equals(timerName)) {
//				entry.getValue().log();
//			}
//		}
//	}
//
//	@Override
//	public int hashCode() {
//		// Jenkins hash, see http://www.burtleburtle.net/bob/hash/doobs.html and also
//		// http://en.wikipedia.org/wiki/Jenkins_hash_function.
//		int hash = name.hashCode();
//		hash += (hash << 10);
//		hash ^= (hash >> 6);
//		hash += Long.valueOf(threadId).hashCode();
//		hash += (hash << 10);
//		hash ^= (hash >> 6);
//		hash += Integer.valueOf(todoFlags).hashCode();
//		hash += (hash << 10);
//		hash ^= (hash >> 6);
//
//		hash += (hash << 3);
//		hash ^= (hash >> 11);
//		hash += (hash << 15);
//		return hash;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj) {
//			return true;
//		} else if (obj == null) {
//			return false;
//		} else if (getClass() != obj.getClass()) {
//			return false;
//		} else if (threadId == ((Timer) obj).threadId && todoFlags == ((Timer) obj).todoFlags
//				&& name.equals(((Timer) obj).name)) {
//			return true;
//		} else {
//			return false;
//		}
//	}

	protected static long getThreadCpuTime(long threadId) {
		if (threadId == 0) { // generally invalid
			return 0;
		} else {
			return tmxb.getThreadCpuTime(threadId);
		}
	}

}
