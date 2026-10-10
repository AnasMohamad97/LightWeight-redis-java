package Commands.ListCommands;

import Commands.Command;
import SharedResources.CommandContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LpushCommand implements Command {

    @Override
    public void execute(String[] args, CommandContext ctx) throws IOException {
        if (args.length < 3) {
            ctx.encoder().errWrongNumArgs(args[0].toLowerCase());
            return;
        }
        String key = args[1];
        List<String> list = new ArrayList<>(Arrays.asList(args).subList(2, args.length));
        ctx.encoder().WriteInteger(ctx.database().lpush(key, list)).flush();

    }
}
