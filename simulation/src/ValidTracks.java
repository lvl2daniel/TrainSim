import java.util.concurrent.locks.ReentrantLock;
/*
Name: Daniel Gonzalez
Course: CNT 4714 Fall 2023
Assignment title: Project 2 – Multi-threaded programming in Java
Date: October 8, 2023
Class: Enterprise Computing
Description: A railway switching yard simulation program.
*/

public class ValidTracks {
    // store the valid tracks at the indices of a 2d array at the inbound track and outbound track
    public ReentrantLock lock1;
    public ReentrantLock lock2;
    public ReentrantLock lock3;
    int firstLockInt;
    int secondLockInt;
    int thirdLockInt;
    public ReentrantLock[] locks = new ReentrantLock[3];
    //constructor for each validtracks object
    public ValidTracks(ReentrantLock lock1, ReentrantLock lock2, ReentrantLock lock3, int firstLockInt, int secondLockInt, int thirdLockInt){
        this.lock1 = lock1;
        this.lock2 = lock2;
        this.lock3 = lock3;
        this.firstLockInt = firstLockInt;
        this.secondLockInt = secondLockInt;
        this.thirdLockInt = thirdLockInt;
        locks[0] = lock1;
        locks[1] = lock2;
        locks[2] = lock3;
    }
}
