package Commands.Functional;

import Commands.Command;
import SharedResources.CommandContext;

import java.io.IOException;

public class EchoCommand implements Command {
    @Override
    public void execute(String[] args, CommandContext ctx) throws IOException {
        if (args.length != 2) {
            ctx.encoder().errWrongNumArgs(args[0].toLowerCase());
            return;
        }
        ctx.encoder().WriteBulkString(args[1]).flush();
    }
}
