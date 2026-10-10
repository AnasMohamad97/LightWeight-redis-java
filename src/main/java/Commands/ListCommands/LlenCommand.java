package Commands.ListCommands;

import Commands.Command;
import SharedResources.CommandContext;

import java.io.IOException;

public class LlenCommand implements Command {
    @Override
    public void execute(String[] args, CommandContext ctx) throws IOException {
        if(args.length != 2){
            ctx.encoder().errWrongNumArgs(args[0]);
            return;
        }
        String key = args[1];
        ctx.encoder().WriteInteger(ctx.database().llen(key));
        ctx.encoder().flush();
    }
}

