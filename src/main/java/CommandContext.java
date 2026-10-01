import java.util.ArrayList;
import java.util.HashMap;

public class CommandContext {
    private final RespEncoder encoder;
    private final Database database;

    public CommandContext(RespEncoder encoder, Database database) {
        this.encoder = encoder;
        this.database = database;

    }

    public RespEncoder encoder() { return encoder; }
    public Database database() { return database; }
   }
