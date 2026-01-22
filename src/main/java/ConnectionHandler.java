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
    private final RespEncoder  encoder;

    public  ConnectionHandler(Socket client) throws IOException {
        this.client=client;
        keyValueMap=new HashMap<>();
        ListKeyMap=new HashMap<>();
        encoder = new RespEncoder(client.getOutputStream());
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
                encoder.pong();
                encoder.flush();
                break;
            case "ECHO":
                if(command.length>1){
                    encoder.WriteBulkString(command[1]);
                    encoder.flush();
                }
                break;
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
                    encoder.WriteInteger(listSize);
                    encoder.flush();
                    //:1\r\n
                }else {
                    encoder.errUnknownCommand();
                    encoder.flush();
                }break;
            case "COMMAND":
                //redis-cli sends this on connection - just return empty array
                encoder.writeArrayHeader(0);
                encoder.flush();
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
                    encoder.errUnknownCommand();
                    break;
                }
                encoder.ok();
                encoder.flush();
                break;
            case "GET":
                if(keyValueMap.containsKey(command[1]) && keyValueMap.get(command[1]).getValue()!=null) {
                    String value = keyValueMap.get(command[1]).value;
                    encoder.WriteBulkString(value);
                    encoder.flush();
                }else {
                   encoder.writeNullBulkString();
                   encoder.flush();
                }
                break;
            case "LRANGE":
                if(command.length == 4) {
                    String listKey = command[1];
                    int start =  Integer.parseInt(command[2]);
                    int end =  Integer.parseInt(command[3]);
                    if(!ListKeyMap.containsKey(listKey) || ListKeyMap.get(listKey).isEmpty() || start > end || start >  ListKeyMap.get(listKey).size() ) {

                        encoder.writeArrayHeader(0); // emptyArray = "*0\r\n";
                        encoder.flush();
                    }else {
                        end = Math.min(end+1, ListKeyMap.get(listKey).size());
                        List<CachKey> list = ListKeyMap.get(listKey).subList(start, end);

                        //out.write(("*"+Math.min(list.size(),(end-start+1))+"\r\n").getBytes(StandardCharsets.UTF_8));
                       // encoder.writeArrayHeader(Math.min(list.size(),(end-start+1)));
                        System.out.println("*"+Math.min(list.size(),(end-start+1))+"\r\n");
                        encoder.WriteBulkArray(list);
//                        for (CachKey cachKey : list) {
//                            encoder.WriteBulkString(cachKey.getValue());
//                            //out.write(("$" + cachKey.getValue().length() + "\r\n" + cachKey.getValue() + "\r\n").getBytes(StandardCharsets.UTF_8));
//                            System.out.println(cachKey.getValue().length() + "\r\n" + cachKey.getValue() + "\r\n");
//                        }
                        out.flush();
                    }
                }else {
                    encoder.errWrongNumArgs();
                }
                break;
            default:
                // Unknown command - return error
                encoder.errUnknownCommand();
                break;

        }
    }

}
