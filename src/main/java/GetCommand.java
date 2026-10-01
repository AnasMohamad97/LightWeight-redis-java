import java.io.IOException;

public class GetCommand implements Command {
    @Override
    public void execute(String[] args, CommandContext ctx) throws IOException {
        if (args.length != 2) {
            ctx.encoder().errWrongNumArgs(args[0].toLowerCase());
            return;
        }
        String result = ctx.database().get(args[1]);
        if (result == null) {
            ctx.encoder().writeNullBulkString();
            return;
        }
        ctx.encoder().WriteBulkString(result).flush();
    }
}
