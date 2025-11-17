import commandUtil.RespEncoder;
import commandUtil.RespParser;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ConnectionHandler implements Runnable {
    private final Socket client ;


    public  ConnectionHandler(Socket client) throws IOException {
        this.client=client;
      }
    @Override
    public void run() {
        // flushes after every print call
        PrintWriter out = null;

        BufferedReader in = null;
        // buffered reader automatically handle line breaks

        try {

            out = new PrintWriter(client.getOutputStream(), true , StandardCharsets.UTF_8);
            in = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
            RespParser respParser = new RespParser();
            RespEncoder respEncoder = new RespEncoder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
               ArrayList<String> commands = respParser.parse(inputLine);
               String encodedCommand = respEncoder.encode(commands);
               out.write(encodedCommand);
            }
        }catch (IOException e){
            System.out.println("From Socket: "+this.client.getInetAddress()+":"+this.client.getLocalPort() + " IOException " + e.getMessage());
        }
    }
}
