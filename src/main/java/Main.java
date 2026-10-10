import Commands.CommandRegistry;
import SharedResources.Database;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {


  public static void main(String[] args){
    System.out.println("Logs from your program will appear here!");

        ServerSocket serverSocket = null;
        int port = 6379;

        ExecutorService threadPool = Executors.newCachedThreadPool();
        CommandRegistry commandRegistry = CommandRegistry.defaultRegistry();
        Database database = new Database();


         try {
             serverSocket = new ServerSocket(port);
             serverSocket.setReuseAddress(true);
            // connect to the server
           //  System.out.println("Waiting for connection...");
             while (!Thread.currentThread().isInterrupted()) {

               Socket clientSocket = serverSocket.accept();
              threadPool.submit(new ConnectionHandler(clientSocket,commandRegistry,database));
          }
        } catch (IOException e) {
          System.out.println("IOException: " + e.getMessage());
      }finally {
             try {
                 if (serverSocket != null) {
                     serverSocket.close();
                 }
                 threadPool.shutdown();
             } catch (IOException e) {
                 System.out.println("IOException: " + e.getMessage());
             }
         }
  }



}
