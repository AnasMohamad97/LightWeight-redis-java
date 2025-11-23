import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class ConnectionHandler implements Runnable {
    private final Socket client ;
    private HashMap<String,String> keyValueMap;


    public  ConnectionHandler(Socket client) throws IOException {
        this.client=client;
        keyValueMap=new HashMap<>();
      }
    @Override
    public void run() {
        // flushes after every print call
        OutputStream out = null;
        BufferedReader in = null;
        // buffered reader automatically handle line breaks
        try {

            out = client.getOutputStream();
            in = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
            String  inputLine;
            while ((inputLine = in.readLine()) != null) {

                if(inputLine.startsWith("*")){
                    int size = Integer.parseInt(inputLine.substring(1));
                    String[] command = new String[size];

                    for ( int i = 0; i < size; i++ ) {
                        String bulkStringHeader = in.readLine(); // $N($7) for example
                        String value = in.readLine();
                        command[i] = value;
                    }//*3\r\n$3\r\nSET\r\n$5\r\nmykey\r\n$5\r\nhello\r\n
                    parseCommand(command , out);
                }
            }
        }catch (IOException e){
            System.out.println("From Socket: "+this.client.getInetAddress()+":"+this.client.getLocalPort() + " IOException " + e.getMessage());
        }
    }

    private void parseCommand(String[] command , OutputStream out) throws IOException {
        if(command.length==0) return;

        String cmd = command[0].toUpperCase();

        switch (cmd){
            case "PING":
                out.write(("+PONG\r\n".getBytes(StandardCharsets.UTF_8)));
                out.flush();
                break;
            case "ECHO":
                if(command.length>1){
                    String response = "$" + command[1].length() + "\r\n" + command[1] + "\r\n";
                    out.write(response.getBytes(StandardCharsets.UTF_8));
                    out.flush();
                }break;
            case "COMMAND":
                //redis-cli sends this on connection - just return empty array
                out.write("*0\r\n".getBytes());
                out.flush();
                break;
            case "SET":
                if(command.length>1){
                    keyValueMap.put(command[1], command[2]);
                }else {
                    out.write("-ERR unknown command\r\n".getBytes());
                    out.flush();
                    break;
                }
                out.write(("+OK\r\n".getBytes(StandardCharsets.UTF_8)));
                out.flush();
                break;
            case "GET":
                if(keyValueMap.containsKey(command[1])){
                    String response ="+"+keyValueMap.get(command[1])+"\r\n";
                    out.write(response.getBytes(StandardCharsets.UTF_8));
                    out.flush();
                }else {
                    out.write("$-1\r\n".getBytes(StandardCharsets.UTF_8));
                    out.flush();
                }
                break;
            default:
                // Unknown command - return error
                out.write("-ERR unknown command\r\n".getBytes());
                out.flush();
                break;

        }
    }

}
