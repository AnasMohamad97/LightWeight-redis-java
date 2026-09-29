import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Queue;

public class LpopCommand implements Command {

    @Override
    public void execute(String[] args, CommandContext ctx) throws IOException {
        if (args.length < 2) {
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
        int bound = args.length==2 ?1:Integer.parseInt(args[2]);
        bound = Math.min(bound, list.size());
        ArrayList<CacheKey> removed = new ArrayList<>(bound);
        for (int i = 0; i < bound; i++) {
            removed.add(list.getFirst());
            list.removeFirst();
        }
        ctx.encoder().WriteBulkArray(removed);
        ctx.listKeyMap().put(key,list);
    }
}
