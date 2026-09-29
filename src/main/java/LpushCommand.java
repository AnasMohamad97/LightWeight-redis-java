import java.io.IOException;
import java.util.ArrayList;

public class LpushCommand implements Command {

    @Override
    public void execute(String[] args, CommandContext ctx) throws IOException {
        if (args.length < 3) {
            ctx.encoder().errWrongNumArgs(args[0].toLowerCase());
            return;
        }
        String key = args[1];
        ArrayList<CacheKey> list = ctx.listKeyMap()
                .computeIfAbsent(key, k -> new ArrayList<>());

        for(int i = 2 ; i < args.length; i++) {
            list.addFirst(new CacheKey(args[i], args[i]));
        }
        ctx.encoder().WriteInteger(list.size()).flush();


    }
}
