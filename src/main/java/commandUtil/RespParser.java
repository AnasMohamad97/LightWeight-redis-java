package commandUtil;

import java.util.ArrayList;
import java.util.Arrays;

public class RespParser {

    String[] strings;
    public ArrayList<String> parse(String inputLine) {

        ArrayList<String> response = new ArrayList<>();
        strings = inputLine.split(" ");
        String firstCommand = strings[0];

        switch (firstCommand.toUpperCase()) {
            case "PING":
                response.add("PONG");
                break;
            case "ECHO":
                response = new ArrayList<>(Arrays.asList(strings));
                break;
            case "COMMAND":
                // redis-cli sends this on connection - just return empty array
                response.add("");
                break;
            default:
                response.add(firstCommand);
        }
        return response;

    }
}
