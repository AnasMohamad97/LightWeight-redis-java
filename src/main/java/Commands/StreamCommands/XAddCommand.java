package Commands.StreamCommands;

import Commands.Command;
import SharedResources.CommandContext;

import java.io.IOException;
import java.util.HashMap;

public class XAddCommand implements Command {
    private Integer lastMillisecondsTime = 0;
    private Integer lastSequenceNumber = 0;
    private HashMap<Integer,Integer> EntryIDs = new HashMap<>();
    @Override
    public void execute(String[] args, CommandContext ctx) throws IOException, InterruptedException {
        if(args.length < 4){
            ctx.encoder().errWrongNumArgs(args[0]);
            return;
        }
        String streamKey = args[1];
        String entry = args[2];
      String [] split = entry.split("-");
      if(split[split.length-1].equalsIgnoreCase("*")){
          Integer MilliSeconds = Integer.parseInt(split[0]);
          if(EntryIDs.containsKey(MilliSeconds)){
              entry = entry.replace("*",String.valueOf(EntryIDs.get(MilliSeconds)+1));
          }else if(!MilliSeconds.equals(0)){
              entry = entry.replace("*","0");

          }else {
              entry = entry.replace("*","1");
          }

      }
      if( validateEntryValue(entry, ctx)) {
          for (int i = 3; i < args.length - 1; i += 2) {
              ctx.database().setStream(streamKey, entry, args[i], args[i + 1]);
          }
          ctx.encoder().WriteBulkString(entry);
          ctx.encoder().flush();
      }
    }
    public boolean validateEntryValue(String entry , CommandContext ctx) throws IOException, InterruptedException {
        String[] split = entry.split("-");
        Integer milliseconds = Integer.parseInt(split[0]);
        Integer sequenceNumber = Integer.parseInt(split[1]);
        if(!EntryIDs.containsKey(milliseconds)){
            EntryIDs.put(milliseconds,sequenceNumber);
            return true;
        }else if(EntryIDs.get(milliseconds) < sequenceNumber){
            EntryIDs.replace(milliseconds,sequenceNumber);
            return true;
        }else {
            if((split[0].equalsIgnoreCase("0")) &&
                    (split[1].equalsIgnoreCase("0") )){

                ctx.encoder().writeError("ERR The ID specified in XADD must be greater than 0-0");
            }else ctx.encoder().writeError("ERR The ID specified in XADD is equal or smaller than the target stream top item");
        }
        return false;
    }

}
