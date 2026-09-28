import java.io.IOException;

public class GetCommand implements Command {
    @Override
    public void execute(String[] args, CommandContext ctx) throws IOException {
        if (args.length != 2) {
            ctx.encoder().errWrongNumArgs(args[0].toLowerCase());
            return;
        }
        CacheKey entry = ctx.keyValueMap().get(args[1]);
        String value = (entry == null) ? null : entry.getValue();
        ctx.encoder().WriteBulkString(value).flush();
    }
}
