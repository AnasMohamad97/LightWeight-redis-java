import java.io.IOException;
import java.util.List;

public class BlpopCommand implements Command {

    @Override
    public void execute(String[] args, CommandContext ctx) throws IOException, InterruptedException {
        if (args.length != 3) {
            ctx.encoder().errWrongNumArgs(args[0]);
            return;
        }
        String key = args[1];
        List<String> removed = ctx.database().blpop(key,Integer.parseInt(args[2]));
        if(removed.isEmpty())ctx.encoder().WriteNullArray();
        ctx.encoder().WriteBulkArray(removed);
        return;

    }
}
