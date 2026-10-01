import java.io.IOException;

public class GetCommand implements Command {
    @Override
    public void execute(String[] args, CommandContext ctx) throws IOException {
        if (args.length != 2) {
            ctx.encoder().errWrongNumArgs(args[0].toLowerCase());
            return;
        }
        ctx.encoder().WriteBulkString(ctx.database().get(args[1])).flush();
    }
}
