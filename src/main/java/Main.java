import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {


  public static void main(String[] args){
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");

      //Uncomment the code below to pass the first stage
        ServerSocket serverSocket = null;
        int port = 6379;

        ExecutorService threadPool = Executors.newCachedThreadPool();

         try {
             serverSocket = new ServerSocket(port);
             serverSocket.setReuseAddress(true);
            // connect to the server
           //  System.out.println("Waiting for connection...");
             while (!Thread.currentThread().isInterrupted()) {

               Socket clientSocket = serverSocket.accept();
              threadPool.submit(new ConnectionHandler(clientSocket));
              clientSocket.close();
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
