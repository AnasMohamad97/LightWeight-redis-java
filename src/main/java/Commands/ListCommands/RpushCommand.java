package Commands.ListCommands;

import Commands.Command;
import SharedResources.CommandContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RpushCommand implements Command {
    @Override
    public void execute(String[] args, CommandContext ctx) throws IOException {
        if (args.length < 3) {
            ctx.encoder().errWrongNumArgs(args[0].toLowerCase());
            return;
        }
        String listKey = args[1];
        List<String> list = new ArrayList<>(Arrays.asList(args).subList(2, args.length));
        int size = ctx.database().rpush(listKey,list);
        ctx.encoder().WriteInteger(size).flush();
    }
}
