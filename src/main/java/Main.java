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
        Socket clientSocket = null;
        int port = 6379;

        ExecutorService threadPool = Executors.newCachedThreadPool();

         try {

             serverSocket = new ServerSocket(port);
             serverSocket.setReuseAddress(true);
             clientSocket = serverSocket.accept(); // connect to the server

             while (!Thread.currentThread().isInterrupted()) {
              threadPool.submit(new ConnectionHandler(clientSocket));
          }
        } catch (IOException e) {
          System.out.println("IOException: " + e.getMessage());
      } finally {
          try {
            if (clientSocket != null) {
              clientSocket.close();
            }
          } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
          }
        }
  }
}
