package nachos.threads;

import nachos.machine.*;

import java.util.LinkedList;

/**
 * A <i>communicator</i> allows threads to synchronously exchange 32-bit
 * messages. Multiple threads can be waiting to <i>speak</i>,
 * and multiple threads can be waiting to <i>listen</i>. But there should never
 * be a time when both a speaker and a listener are waiting, because the two
 * threads can be paired off at this point.
 */
public class Communicator {
    /**
     * Allocate a new communicator.
     */
    public Communicator() {
        mutex = new Lock();
        speaker = new LinkedList();
        listener = new LinkedList();
    }

    /**
     * Wait for a thread to listen through this communicator, and then transfer
     * <i>word</i> to the listener.
     *
     * <p>
     * Does not return until this thread is paired up with a listening thread.
     * Exactly one listener should receive <i>word</i>.
     *
     * @param	word	the integer to transfer.
     */
    public void speak(int word) {
        mutex.acquire();
        //boolean interrupt_status = Machine.interrupt().disabled();
        if(! listener.isEmpty())
        {
           word temp = listener.removeFirst();
            //System.out.println(KThread.currentThread().getName()+ " speakes the word "+ word);
           temp.setMsg(word);
           temp.getCond().wake();
        }
        else
        {
            word temp = new word(word,mutex,KThread.currentThread().getName());
            speaker.add(temp);

            temp.getCond().sleep();

        }
        //Machine.interrupt().restore(interrupt_status);
        mutex.release();
    }

    /**
     * Wait for a thread to speak through this communicator, and then return
     * the <i>word</i> that thread passed to <tt>speak()</tt>.
     *
     * @return	the integer transferred.
     */    
    public int listen() {
        mutex.acquire();
        int msg;
        if(! speaker.isEmpty())
        {
            word temp = speaker.removeFirst();
            msg = temp.getMsg();
            System.out.println(temp.getName() +" speaks the word "+msg);
            System.out.println(KThread.currentThread().getName()+" listens the word "+ msg);
            temp.getCond().wake();
        }
        else {
            word temp = new word(-1,mutex,KThread.currentThread().getName());
            listener.add(temp);
            temp.getCond().sleep();
            msg = temp.getMsg();
           // System.out.println(KThread.currentThread().getName()+" listens the word "+ msg);
        }

        mutex.release();
	    return msg;
    }

    private LinkedList<word>speaker;
    private LinkedList<word>listener;
    private Lock mutex;
    private class word{
        private int msg;
        Condition2 cond;
        private String name;

        public word(int msg, Lock l, String name) {
            this.msg = msg;
            this.cond = new Condition2(l);
            this.name = name;
        }

        public int getMsg() {


            return msg;
        }

        public void setMsg(int msg) {

            System.out.println(KThread.currentThread().getName()+" speakes the word "+ msg);
            System.out.println(this.name +" listens the word "+msg);
            this.msg = msg;
        }

        public Condition2 getCond() {
            return cond;
        }

        public String getName() {
            return name;
        }
    }
}
