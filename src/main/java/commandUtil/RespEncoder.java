package commandUtil;

import java.util.ArrayList;

public class RespEncoder {

    public String encode(ArrayList<String> commands) {
        String firstCommand = commands.get(0);
        switch (firstCommand) {
            case "PING":
                return "+PONG\r\n";
            case "ECHO":
                int len  = commands.get(1).length();
                return "$" + len + "\r\n" + commands.get(1) + "\r\n";
            default:
                return "$-1\r\n";
        }
    }

}
