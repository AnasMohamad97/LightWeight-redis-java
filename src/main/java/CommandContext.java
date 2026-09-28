import java.util.ArrayList;
import java.util.HashMap;

public class CommandContext {
    private final RespEncoder encoder;
    private final HashMap<String, CachKey> keyValueMap;
    private final HashMap<String, ArrayList<CachKey>> listKeyMap;

    public CommandContext(RespEncoder encoder,
                          HashMap<String, CachKey> keyValueMap,
                          HashMap<String, ArrayList<CachKey>> listKeyMap) {
        this.encoder = encoder;
        this.keyValueMap = keyValueMap;
        this.listKeyMap = listKeyMap;
    }

    public RespEncoder encoder() { return encoder; }
    public HashMap<String, CachKey> keyValueMap() { return keyValueMap; }
    public HashMap<String, ArrayList<CachKey>> listKeyMap() { return listKeyMap; }
}
