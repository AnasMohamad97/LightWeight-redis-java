import java.io.IOException;
import java.util.ArrayList;

public class LlenCommand implements Command {
    @Override
    public void execute(String[] args, CommandContext ctx) throws IOException {
        if(args.length != 2){
            ctx.encoder().errWrongNumArgs(args[0]);
            return;
        }
        String key = args[1];
        ArrayList<CacheKey>list = ctx.listKeyMap().getOrDefault(key, new ArrayList<>(0));
        ctx.encoder().WriteInteger(list.size());
        ctx.encoder().flush();
    }
}

