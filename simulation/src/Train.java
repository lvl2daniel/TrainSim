/*
Name: Daniel Gonzalez
Course: CNT 4714 Fall 2023
Assignment title: Project 2 – Multi-threaded programming in Java
Date: October 8, 2023
Class: Enterprise Computing
Description: A railway switching yard simulation program.
*/
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;

public class Train implements Runnable{
    //name string for ease of use!
    public String name;
    //define variables
    public int trainNumber;
    public int inboundTrack;
    public int outboundTrack;
    public ReentrantLock lock1;
    public ReentrantLock lock2;
    public ReentrantLock lock3;
    public int firstLockInt;
    public int secondLockInt;
    public int thirdLockInt;
    public ReentrantLock[] locked = new ReentrantLock[3];
    public boolean notFinished = false;
    //construcotr which takes in the locks, the integer symbol of the locks, and the inbound/outbound tracks.
    public Train(int name, int inboundTrack, int outboundTrack, ReentrantLock lock1, ReentrantLock lock2, ReentrantLock lock3, int firstLockInt, int secondLockInt, int thirdLockInt){
        this.name = "Train " + name;
        this.trainNumber = name;
        this.inboundTrack = inboundTrack;
        this.outboundTrack = outboundTrack;
        this.lock1 = lock1;
        this.lock2 = lock2;
        this.lock3 = lock3;
        this.firstLockInt = firstLockInt;
        this.secondLockInt = secondLockInt;
        this.thirdLockInt = thirdLockInt;
    }

    //override the run method to simulate the train movement
    @Override
    public void run() {
        //keep going until the train is dispatched.
        while(!this.notFinished){
            //try locks
            //if lock is available, lock it and move on to next lock
            try{
                if(lock1.tryLock(1500, TimeUnit.MILLISECONDS)){
                    //print out that the train has locked the switch
                    System.out.println(this.name + ": HOLDS LOCK ON Switch #" + firstLockInt);               
                }
                else{
                    System.out.println(this.name + " UNABLE TO LOCK first required switch: Switch " + firstLockInt);
                    //sleep for 3 seconds to simulate train waiting for locks.
                    Thread.sleep(3000);
                    //restart the loop.
                    continue;
                }
            }
            catch(InterruptedException e){
                //catch any unexpected interrupts
                System.out.println(this.name +" UNABLE TO LOCK first required switch: Switch " + firstLockInt);
            }
            try{
                // repeat step one with the second lock
                if(lock2.tryLock(1500, TimeUnit.MILLISECONDS)){
                    System.out.println(this.name + ": HOLDS LOCK ON Switch #" + secondLockInt);
                }
                else{
                    System.out.println(this.name + " UNABLE TO LOCK second required switch: Switch " + secondLockInt);
                    System.out.println(this.name +": Releasing lock on first required switch: Switch " + firstLockInt);
                    lock1.unlock();
                    System.out.println("Train will wait...");
                    Thread.sleep(3000);
                    continue;
                }
            }
            catch(InterruptedException e){
                System.out.println(this.name + " UNABLE TO LOCK second required switch: Switch " + secondLockInt);
                System.out.println("Releasing locks on first requires switch: " + firstLockInt);
                lock1.unlock();
            }
            try{
                // repeat step one with the third lock
                if(lock3.tryLock(1500, TimeUnit.MILLISECONDS)) {
                    System.out.println(this.name + ": HOLDS LOCK ON Switch #" + thirdLockInt);
                    System.out.println(this.name + ": HOLDS ALL NEEDED SWITCH LOCKS - Train movement begins");
                    //simulate train movement for dispatch.
                    try{
                        Thread.sleep(3000);
                    }
                    catch(InterruptedException e){
                        System.out.println("Train movement interrupted");
                    }
                    //outputs symbolizing the dispatch of the train.
                    System.out.println(this.name + ": Clear of yard control");
                    System.out.println(this.name + ": Releasing all switch locks.");
                    System.out.println(this.name + ": Unlocks/releases lock on Switch " + firstLockInt);
                    //release all held locks
                    lock1.unlock();
                    System.out.println(this.name + ": Unlocks/releases lock on Switch " + secondLockInt);
                    lock2.unlock();
                    System.out.println(this.name + ": Unlocks/releases lock on Switch " + thirdLockInt);
                    lock3.unlock();
                    System.out.println(this.name + ": has been dispatched and moves on down the line out of yard control into CTC.");
                    System.out.println("@@@@@@@ " + this.name + ": DISPATCHED @@@@@@@");
                    //flag the train as dispatched which will stop the thread.
                    this.notFinished = false;
                    break;
                }
                else{
                    System.out.println(this.name + " UNABLE TO LOCK third required switch: Switch " + thirdLockInt);
                    System.out.println(this.name +": Releasing locks on first and second required switches: Switch " + firstLockInt + " and Switch " + secondLockInt);
                    lock1.unlock();
                    lock2.unlock();
                    System.out.println("Train will wait...");
                    Thread.sleep(3000);
                    continue;
                }
                //stop the thread
            }
            catch(InterruptedException e){
                System.out.println(this.name + " UNABLE TO LOCK third required switch: Switch " + thirdLockInt);
                System.out.println("Releasing locks on first and second required switches: Switch " + firstLockInt + " and Switch " + secondLockInt);
                System.out.println("Train will wait...");
                lock1.unlock();
                lock2.unlock();
                
                continue;
            }
   

        }
    }
    

}