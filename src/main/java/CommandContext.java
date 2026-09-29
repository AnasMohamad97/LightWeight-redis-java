import java.util.ArrayList;
import java.util.HashMap;

public class CommandContext {
    private final RespEncoder encoder;
    private final HashMap<String, CacheKey> keyValueMap;
    private final HashMap<String, ArrayList<CacheKey>> listKeyMap;


    public CommandContext(RespEncoder encoder,
                          HashMap<String, CacheKey> keyValueMap,
                          HashMap<String, ArrayList<CacheKey>> listKeyMap) {
        this.encoder = encoder;
        this.keyValueMap = keyValueMap;
        this.listKeyMap = listKeyMap;
    }

    public RespEncoder encoder() { return encoder; }
    public HashMap<String, CacheKey> keyValueMap() { return keyValueMap; }
    public HashMap<String, ArrayList<CacheKey>> listKeyMap() { return listKeyMap; }
}
