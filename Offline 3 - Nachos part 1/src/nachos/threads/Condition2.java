package nachos.threads;

import java.util.LinkedList;
import nachos.machine.*;

/**
 * An implementation of condition variables that disables interrupt()s for
 * synchronization.
 *
 * <p>
 * You must implement this.
 *
 * @see	nachos.threads.Condition
 */
public class Condition2 {
    /**
     * Allocate a new condition variable.
     *
     * @param	conditionLock	the lock associated with this condition
     *				variable. The current thread must hold this
     *				lock whenever it uses <tt>sleep()</tt>,
     *				<tt>wake()</tt>, or <tt>wakeAll()</tt>.
     */
    public Condition2(Lock conditionLock) {
	this.conditionLock = conditionLock;
    }

    /**
     * Atomically release the associated lock and go to sleep on this condition
     * variable until another thread wakes it using <tt>wake()</tt>. The
     * current thread must hold the associated lock. The thread will
     * automatically reacquire the lock before <tt>sleep()</tt> returns.
     */
    public void sleep() {
	Lib.assertTrue(conditionLock.isHeldByCurrentThread());
        queue.add(KThread.currentThread());
        boolean interrupt_status = Machine.interrupt().disable();
	conditionLock.release();
        KThread.sleep();
	conditionLock.acquire();
        Machine.interrupt().restore(interrupt_status);

    }

    /**
     * Wake up at most one thread sleeping on this condition variable. The
     * current thread must hold the associated lock.
     */
    public void wake() {
	Lib.assertTrue(conditionLock.isHeldByCurrentThread());
        boolean interrupt_status = Machine.interrupt().disable();
        if(!queue.isEmpty()){
            KThread t =queue.removeFirst();
            t.ready();
            //ThreadedKernel.scheduler.setPriority(t,7);
            }
        Machine.interrupt().restore(interrupt_status);
    }

    /**
     * Wake up all threads sleeping on this condition variable. The current
     * thread must hold the associated lock.
     */
    public void wakeAll() {
	Lib.assertTrue(conditionLock.isHeldByCurrentThread());
        boolean interrupt_status = Machine.interrupt().disable();
        while(! queue.isEmpty())
        {
            queue.removeFirst().ready();
        }
        Machine.interrupt().restore(interrupt_status);
    }

    private Lock conditionLock;

    private LinkedList<KThread> queue = new LinkedList();
}
