import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Queue;

public class LpopCommand implements Command {

    @Override
    public void execute(String[] args, CommandContext ctx) throws IOException {
        if (args.length < 2) {
            ctx.encoder().errWrongNumArgs(args[0]);
            return;
        }
        String key = args[1];
        int bound = args.length==2?1:Integer.parseInt(args[2]);
        List<String> removed = ctx.database().lpop(key,bound);
        if (removed == null) {
            ctx.encoder().writeNullBulkString();
            ctx.encoder().flush();
            return;
        }
        if(bound==1) {
            ctx.encoder().WriteBulkString(removed.getFirst());
            return;
        }
        ctx.encoder().WriteBulkArray(removed);
    }
}
