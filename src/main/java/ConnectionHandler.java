import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class ConnectionHandler implements Runnable {
    private final Socket client ;
    private final HashMap<String,CachKey> keyValueMap;
    private final HashMap<String , ArrayList<CachKey>>ListKeyMap;


    public  ConnectionHandler(Socket client) throws IOException {
        this.client=client;
        keyValueMap=new HashMap<>();
        ListKeyMap=new HashMap<>();
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
            case "RPUSH":
                if(command.length>1){
                    String listKey = command[1];
                    CachKey cachKey=new CachKey(listKey,command[2]);
                    if(ListKeyMap.containsKey(listKey)){
                        ListKeyMap.get(listKey).add(cachKey);
                    }else {
                        ArrayList<CachKey> list=new ArrayList<>();
                        list.add(cachKey);
                        ListKeyMap.put(listKey,list);
                    }
                    for(int i = 3 ; i < command.length ; ++i){
                        cachKey=new CachKey(listKey,command[i]);
                        ListKeyMap.get(listKey).add(cachKey);
                    }
                    int listSize = ListKeyMap.get(listKey).size();
                    out.write((":"+listSize+"\r\n").getBytes(StandardCharsets.UTF_8));
                    out.flush();
                    //:1\r\n
                }else {
                    out.write("-ERR unknown command\r\n".getBytes());
                    out.flush();
                }break;
            case "COMMAND":
                //redis-cli sends this on connection - just return empty array
                out.write("*0\r\n".getBytes());
                out.flush();
                break;
            case "SET":
                if(command.length ==3 ){
                    CachKey newCachedValue = new CachKey(command[1],command[2]);
                    keyValueMap.put(command[1], newCachedValue);
                }else if(command.length == 5){
                    long expiryTime = (command[3].equals("PX"))?Long.parseLong(command[4]):(command[3].equals("EX"))?Long.parseLong(command[4])*1000:Long.parseLong(command[4]);
                    long timeLimit =  expiryTime+ System.currentTimeMillis();
                    CachKey newCachedValue = new CachKey(command[1],command[2] , timeLimit);
                    keyValueMap.put(command[1], newCachedValue);
                }else {
                    out.write("-ERR unknown command\r\n".getBytes());
                    out.flush();
                    break;
                }
                out.write(("+OK\r\n".getBytes(StandardCharsets.UTF_8)));
                out.flush();
                break;
            case "GET":
                if(keyValueMap.containsKey(command[1]) && keyValueMap.get(command[1]).getValue()!=null) {
                    String value = keyValueMap.get(command[1]).value;
                    String response ="$"+value.length()+"\r\n"+value+"\r\n";
                    out.write(response.getBytes(StandardCharsets.UTF_8));
                    out.flush();
                }else {
                    out.write("$-1\r\n".getBytes(StandardCharsets.UTF_8));
                    out.flush();
                }
                break;
            case "LRANGE":
                if(command.length > 1 ) {
                    String listKey = command[1];
                    int start =  Integer.parseInt(command[2]);
                    int end =  Integer.parseInt(command[3]);
                    if(!ListKeyMap.containsKey(listKey) || ListKeyMap.get(listKey).isEmpty() || start > end || start >  ListKeyMap.get(listKey).size()  ) {
                        String  emptyArray = "*0\r\n";
                        System.out.println(emptyArray);
                        out.write(emptyArray.getBytes(StandardCharsets.UTF_8));
                        out.flush();
                    }else {
                        end = Math.min(end, ListKeyMap.get(listKey).size());
                        List<CachKey> list = ListKeyMap.get(listKey).subList(start, end+1);

                        out.write(("*"+list.size()+"\r\n").getBytes(StandardCharsets.UTF_8));
                        for(int i = start ; i <= end ; ++i ) {
                            out.write(("$" + list.get(i).getValue().length() + "\r\n" + list.get(i).getValue() + "\r\n").getBytes(StandardCharsets.UTF_8));
                        }
                        out.flush();
                    }
                }else {
                    out.write("-ERR wrong number of arguments\r\n".getBytes());
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
