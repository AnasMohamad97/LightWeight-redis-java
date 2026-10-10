import Commands.CommandRegistry;
import SharedResources.CommandContext;
import Resps.RespEncoder;
import SharedResources.Database;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


public class ConnectionHandler implements Runnable {
    private final Socket client ;
    private OutputStream out;
    private BufferedReader in;
    CommandRegistry commandRegistry;
    Database database;
    public  ConnectionHandler(Socket client,CommandRegistry commandRegistry,Database database) throws IOException {
        this.client=client;
        this.commandRegistry=commandRegistry;
        this.database = database;
      }
    @Override
    public void run() {
        //flushes after every print call
        OutputStream out = null;
        BufferedReader in = null;
        // buffered reader automatically handle line breaks
        try {

            out = client.getOutputStream();
            in = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
            CommandContext commandContext = new CommandContext(new RespEncoder(out),database);
            String  inputLine;
            while ((inputLine = in.readLine()) != null) {

                if(inputLine.startsWith("*")){
                    int size = Integer.parseInt(inputLine.substring(1));
                    String[] args = new String[size];

                    for ( int i = 0; i < size; i++ ) {
                        String bulkStringHeader = in.readLine(); // $N($7) for example
                        String value = in.readLine();
                        args[i] = value;
                    }
                    parseCommand(args , commandContext);
                }
            }
        }catch (IOException e){
            System.out.println("From Socket: "+this.client.getInetAddress()+":"+this.client.getLocalPort() + " IOException " + e.getMessage());
        }
    }

    private void parseCommand(String[] args , CommandContext commandContext) throws IOException {
        if(args.length==0) return;
        commandRegistry.dispatch(args,commandContext);

    }

}
