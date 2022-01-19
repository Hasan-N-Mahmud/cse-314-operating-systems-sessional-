package nachos.proj1;
import nachos.machine.*;
import nachos.threads.*;

public class test {
    public static void initiate_test()
    {
       new Join_test().perform();
       new Condition_test().perform();
       new Alarm_test().perform();
       new Communicator_test().perform();
    }

}
class Join_test{
    public Join_test() {
    }

    public void perform()
    {
        System.out.println("-------- Test for phase 1 task 1 ---------");
        System.out.println(" ");
        KThread t0 = new KThread(new PingTest(0)).setName("Thread 0");
        t0.fork();
        System.out.println("forking thread 0 and joining...");
        t0.join();
        System.out.println("Joined on Thread 0...");

        System.out.println("Calling join on a finished thread...");
        t0.join();
        System.out.println("returned...");

        /*KThread.currentThread().join();

        KThread t1 = new KThread(new PingTest(1)).setName("Thread 1");
        t1.join();
*/

        new KThread(new PingTest(1)).fork();
        new KThread(new PingTest(2)).fork();
        new PingTest(999).run();

        KThread t1 = new KThread(new PingTest(3)).setName("Thread 3");
        KThread t2= new KThread(new PingTest(4)).setName("Thread 4");
        t1.fork();
        t2.fork();
        t1.join();
        t2.join();

        new PingTest(999).run();

        System.out.println("--------------------------------------");
    }
    private static class PingTest implements Runnable {
        PingTest(int which) {
            this.which = which;
        }

        public void run() {
            for (int i = 0; i < 5; i++) {
                System.out.println("*** thread " + which + " looped "
                        + i + " times");
                KThread.yield();

            }
        }
        private int which;
    }
}
class Condition_test{
    public void perform() {
        /*System.out.println("-------- Test for phase 1 task 2 ---------");
        System.out.println(" ");
        Lock l=new Lock();
        Condition2 c = new Condition2(l);
        l.acquire();
        new KThread(new PingTest(0,c)).setName("Thread 0").fork();
        new KThread(new PingTest(1,c)).setName("Thread 1").fork();
        new KThread(new PingTest(2,c)).setName("Thread 2").fork();
        new KThread(new PingTest(3,c)).setName("Thread 3").fork();
        new KThread(new PingTest(4,c)).setName("Thread 4").fork();

        c.sleep();

        System.out.println("---------------------------------------");

    */
    }
        private int which;
        private Condition2 con;
}
class Alarm_test{
    public void perform()
    {
        System.out.println("-------- Test for phase 1 task 3 ---------");
        System.out.println(" ");

        long t1=50000;
        long t2=100000;
        long t3=60000;
        Alarm a=new Alarm();
        KThread thr1 =new KThread(new AlarmTest(a, t1)).setName("Thread 1");
        KThread thr2 =new KThread(new AlarmTest(a, t2)).setName("Thread 2");
        KThread thr3 =new KThread(new AlarmTest(a, t3)).setName("Thread 3");

        thr1.fork();
        thr2.fork();
        thr3.fork();
        a.waitUntil(150000);
        System.out.println(KThread.currentThread().getName()+" wakes up at "+Machine.timer().getTime());

    }

    private static class AlarmTest implements Runnable{
        private Alarm a;
        private long t;

        public AlarmTest(Alarm a, long which) {
            this.a = a;
            this.t = which;
        }

        @Override
        public void run() {
            System.out.println(KThread.currentThread().getName()+" calles wakeuntil at "+Machine.timer().getTime());
            a.waitUntil(t);
            System.out.println(KThread.currentThread().getName()+" wakes up at "+Machine.timer().getTime());
        }
    }
}
class Communicator_test{

    public Communicator_test (){
        this.com = new Communicator();
    }

    public void perform()
    {
        System.out.println("-------- Test for phase 1 task 4 ---------");
        System.out.println(" ");

        KThread l1=new KThread(new ComTest(com)).setName("Listener Thread 1");
        KThread l2=new KThread(new ComTest(com)).setName("Listener Thread 2");
        KThread s1=new KThread(new ComTest(com)).setName("Speaker Thread 1");
        KThread s2=new KThread(new ComTest(com)).setName("Speaker Thread 2");
        KThread s3=new KThread(new ComTest(com)).setName("Speaker Thread 3");

        l1.fork();
        l2.fork();
        s1.fork();
        s2.fork();
        s3.fork();

        l1.join();
        l2.join();
        s1.join();
        s2.join();
        s3.join();


        System.out.println("-----------------------------------------------");
    }
    private Communicator com;

    private static class ComTest implements Runnable{
        public ComTest(Communicator com) {
            this.com = com;

        }

        @Override
        public void run() {
            String  str[]=KThread.currentThread().getName().split(" ");
            id=Integer.parseInt(str[2]);
            if(KThread.currentThread().getName().contains("Speaker"))
            {
                for(int i=0;i<2;i++)
                {
                    KThread.yield();
                    //System.out.println(KThread.currentThread().getName()+ " speakes the word "+ (id*10+i));
                    com.speak(id*10+i);

                    KThread.yield();
                }

            }
            else
            {
                /*int w = com.listen();
                System.out.println(KThread.currentThread().getName()+ " listens the word "+w);*/
                for(int i=0;i<3;i++)
                {
                    KThread.yield();
                    int w = com.listen();
                    //System.out.println(KThread.currentThread().getName()+" listens the word "+ w);
                    KThread.yield();
                }
            }



        }
        private Communicator com;
        private int id;

    }
}
