import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Queue;

public class LpopCommand implements Command {

    @Override
    public void execute(String[] args, CommandContext ctx) throws IOException {
        if (args.length != 2) {
            ctx.encoder().errWrongNumArgs(args[0]);
            return;
        }
        String key = args[1];
        ArrayList<CacheKey> list = ctx.listKeyMap().get(key);
        if (list == null) {
            ctx.encoder().writeNullBulkString();
            ctx.encoder().flush();
            return;
        }
        CacheKey cacheKey = list.getFirst();
        list.removeFirst();
        ctx.listKeyMap().put(key,list);
        if (cacheKey != null) {
            ctx.encoder().WriteBulkString(cacheKey.value);
            ctx.encoder().flush();
        }
    }
}
