package Commands.ListCommands;

import Commands.Command;
import SharedResources.CommandContext;

import java.io.IOException;
import java.util.List;

public class BlpopCommand implements Command {

    @Override
    public void execute(String[] args, CommandContext ctx) throws IOException, InterruptedException {
        if (args.length != 3) {
            ctx.encoder().errWrongNumArgs(args[0]);
            return;
        }
        String key = args[1];
         long timeout = (long) (Double.parseDouble(args[2])*1000);

        List<String> removed = ctx.database().blpop(key , timeout);
        if(removed.isEmpty()){
            ctx.encoder().WriteNullArray().flush();
            return;
        }
        ctx.encoder().WriteBulkArray(removed).flush();


    }
}
