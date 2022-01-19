package nachos.threads;

import nachos.machine.*;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */
public class Alarm {
    /**
     * Allocate a new Alarm. Set the machine's timer interrupt handler to this
     * alarm's callback.
     *
     * <p><b>Note</b>: Nachos will not function correctly with more than one
     * alarm.
     */
    public Alarm() {
	Machine.timer().setInterruptHandler(new Runnable() {
		public void run() { timerInterrupt(); }
	    });
    }

    /**
     * The timer interrupt handler. This is called by the machine's timer
     * periodically (approximately every 500 clock ticks). Causes the current
     * thread to yield, forcing a context switch if there is another thread
     * that should be run.
     */
    public void timerInterrupt() {
	    boolean interrupt_status = Machine.interrupt().disabled();
        while (!queue.isEmpty()) {
            if (queue.peek().getWake_time() <= Machine.timer().getTime())
                queue.poll().getT().ready();
            else
                break;
        }
        Machine.interrupt().restore(interrupt_status);
        KThread.yield();
    }

    /**
     * Put the current thread to sleep for at least <i>x</i> ticks,
     * waking it up in the timer interrupt handler. The thread must be
     * woken up (placed in the scheduler ready set) during the first timer
     * interrupt where
     *
     * <p><blockquote>
     * (current time) >= (WaitUntil called time)+(x)
     * </blockquote>
     *
     * @param	x	the minimum number of clock ticks to wait.
     *
     * @see	//nachos.machine.Timer#getTime()
     */
    public void waitUntil(long x) {
	// for now, cheat just to get something working (busy waiting is bad)
	long wakeTime = Machine.timer().getTime() + x;
	boolean interrupt_status = Machine.interrupt().disable();
	queue.add(new sleeper(KThread.currentThread(),wakeTime));
	KThread.sleep();
	Machine.interrupt().restore(interrupt_status);
    }
    private PriorityQueue<sleeper> queue =new PriorityQueue(new timeComparator());

    private class sleeper{
        private KThread t;
        private long wake_time;

        public sleeper(KThread t, long wake_time) {
            this.t = t;
            this.wake_time = wake_time;
        }

        public KThread getT() {
            return t;
        }

        public long getWake_time() {
            return wake_time;
        }
    }

    private class timeComparator implements Comparator<sleeper> {

        @Override
        public int compare(sleeper s1,sleeper s2) {
            if (s1.getWake_time() < s2.getWake_time())
                return -1;
            else if (s1.getWake_time() > s2.getWake_time())
                return 1;
            return 0;

        }

    }

}
