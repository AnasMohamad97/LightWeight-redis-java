package Commands.Functional;

import Commands.Command;
import SharedResources.CommandContext;

import java.io.IOException;

public class TypeCommand implements Command {
    @Override
    public void execute(String[] args, CommandContext ctx) throws IOException, InterruptedException {
        if (args.length != 2) {
            ctx.encoder().errWrongNumArgs(args[0]);
            return;
        }
        String key = args[1];
        ctx.encoder().writeSimpleString(ctx.database().getType(key)).flush();
    }
}
