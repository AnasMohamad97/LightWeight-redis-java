package Commands.StreamCommands;

import Commands.Command;
import SharedResources.CommandContext;

import java.io.IOException;

public class XAddCommand implements Command {
    @Override
    public void execute(String[] args, CommandContext ctx) throws IOException, InterruptedException {
        if(args.length < 4){
            ctx.encoder().errWrongNumArgs(args[0]);
            return;
        }
        String streamKey = args[1];
        String entry = args[2];
        for(int i = 3; i<args.length-1; i+=2 ){
            ctx.database().setStream(streamKey,entry,args[i],args[i+1]);
        }
        ctx.encoder().writeSimpleString(streamKey);
        ctx.encoder().flush();

    }
}
