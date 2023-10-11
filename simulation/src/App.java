/*
Name: Daniel Gonzalez
Course: CNT 4714 Fall 2023
Assignment title: Project 2 – Multi-threaded programming in Java
Date: October 8, 2023
Class: Enterprise Computing
Description: A railway switching yard simulation program.
*/

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class App {
    public static final int MAX_THREADS = 30;
    public static final int MAX_ALIGNMENTS = 60;
    public static void main(String[] args) throws Exception {
        //print to output.txt
        PrintStream out = new PrintStream(new FileOutputStream("output.txt"));
        System.setOut(out);
        //intialize file paths
        String csvFilePath = "theFleetFile.csv"; 
        String yardFilePath = "theYardFile.csv"; 
        //create a lock for each track
        ReentrantLock[] locks = new ReentrantLock[8];
        for(int i = 0; i < locks.length; i++){
            locks[i] = new ReentrantLock();
        }

        //read csv which gives the locks needed for train inbound on track # and outbound on track #
        //csv is in order of inbound track, 1st lock needed, 2nd lock needed, 3rd lock needed, and outbound track
        ValidTracks[][] validTracks = new ValidTracks[8][8];
        // create array of custom class validTracks.
        System.out.println("\n Fall 2023 - Project 2 - Train Movement Simulation      \n\n");
        System.out.println("\n ********** INITIALIZATION OF SIMULATION DETAILS BEGINS ********** \n\n");
        System.out.println("\n\n");
        try (BufferedReader reader = new BufferedReader(new FileReader(yardFilePath))) {
            String line; // Variable to store each line read from the CSV file
            while((line = reader.readLine()) != null){
                String[] data = line.split(",");
                if(data.length == 5){
                    int inboundTrack = Integer.parseInt(data[0]) - 1;
                    ReentrantLock firstLock = locks[Integer.parseInt(data[1])];
                    ReentrantLock secondLock = locks[Integer.parseInt(data[2])];
                    ReentrantLock thirdLock = locks[Integer.parseInt(data[3])];
                    int outboundTrack = Integer.parseInt(data[4]) - 1;
                    validTracks[inboundTrack][outboundTrack] = new ValidTracks(firstLock, secondLock, thirdLock, Integer.parseInt(data[1]), Integer.parseInt(data[2]), Integer.parseInt(data[3]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            String line; // Variable to store each line read from the CSV file
            ExecutorService threadpool = Executors.newFixedThreadPool(MAX_THREADS);



            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                if (data.length == 3) {
                    String trainNumber = data[0];
                    String inboundTrack = data[1];
                    String outboundTrack = data[2];
                    //print locks needed for train
                    try{
                        //create train object
                   
                        Train train = new Train(Integer.parseInt(trainNumber), Integer.parseInt(inboundTrack), Integer.parseInt(outboundTrack), validTracks[Integer.parseInt(inboundTrack) - 1][Integer.parseInt(outboundTrack) - 1].lock1, validTracks[Integer.parseInt(inboundTrack) - 1][Integer.parseInt(outboundTrack) - 1].lock2, validTracks[Integer.parseInt(inboundTrack) - 1][Integer.parseInt(outboundTrack) - 1].lock3, validTracks[Integer.parseInt(inboundTrack) - 1][Integer.parseInt(outboundTrack) - 1].firstLockInt, validTracks[Integer.parseInt(inboundTrack) - 1][Integer.parseInt(outboundTrack) - 1].secondLockInt, validTracks[Integer.parseInt(inboundTrack) - 1][Integer.parseInt(outboundTrack) - 1].thirdLockInt);
                        //make a thread in the threadpool for each train
                        threadpool.execute(train);
                        System.out.println();
                    } catch (Exception e){
                        System.out.println("Train # " + trainNumber + " is on permanent hold and cannot be dispatched.");
                    }
                    
                }
            }
            //shutdown the threadpool
            threadpool.shutdown();
            try{
                if(!threadpool.awaitTermination(300, TimeUnit.SECONDS)){
                    threadpool.shutdownNow();
                    //print the ending of the simulation or if simulation timeout (never happens)
                    System.out.println("$$$ SIMULATION ENDS $$$");
                }
                else{
                    System.out.println("$$$ SIMULATION ENDS $$$");
                }
            } catch (InterruptedException e){
                threadpool.shutdownNow();
                System.out.println("$$$ SIMULATION ENDS $$$");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    
        
    


    }
}

