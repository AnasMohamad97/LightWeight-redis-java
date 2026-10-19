package Commands.StreamCommands;

import Commands.Command;
import SharedResources.CommandContext;

import java.io.IOException;
import java.util.HashMap;

public class XAddCommand implements Command {
    private Long lastMillisecondsTime = 0L;
    private Integer lastSequenceNumber = 0;
    private HashMap<Long,Integer> EntryIDs = new HashMap<>();
    @Override
    public void execute(String[] args, CommandContext ctx) throws IOException, InterruptedException {
        if(args.length < 4){
            ctx.encoder().errWrongNumArgs(args[0]);
            return;
        }
        String streamKey = args[1];
        String entry = args[2];
      String [] split = entry.split("-");
      if(entry.equalsIgnoreCase("*")){
          Long newMillisecondsTime = System.currentTimeMillis();
          EntryIDs.put(newMillisecondsTime,0);
          lastMillisecondsTime = newMillisecondsTime;
          entry = newMillisecondsTime+"-0";
          for (int i = 3; i < args.length - 1; i += 2) {
              ctx.database().setStream(streamKey, entry, args[i], args[i + 1]);
          }
          ctx.encoder().WriteBulkString(entry);
          ctx.encoder().flush();
      }
      else if(split[split.length-1].equalsIgnoreCase("*")){
          Long MilliSeconds = Long.parseLong(split[0]);
          if(EntryIDs.containsKey(MilliSeconds)){
              entry = entry.replace("*",String.valueOf(EntryIDs.get(MilliSeconds)+1));
              EntryIDs.replace(MilliSeconds,EntryIDs.get(MilliSeconds)+1);
          }else if(!MilliSeconds.equals(0L)){
              entry = entry.replace("*","0");
              EntryIDs.put(MilliSeconds,0);

          }else {
              entry = entry.replace("*","1");
              EntryIDs.put(MilliSeconds,1);
          }
          for (int i = 3; i < args.length - 1; i += 2) {
              ctx.database().setStream(streamKey, entry, args[i], args[i + 1]);
          }
          ctx.encoder().WriteBulkString(entry);
          ctx.encoder().flush();

      }
     else  if( validateEntryValue(entry, ctx)) {
          for (int i = 3; i < args.length - 1; i += 2) {
              ctx.database().setStream(streamKey, entry, args[i], args[i + 1]);
          }
          ctx.encoder().WriteBulkString(entry);
          ctx.encoder().flush();
      }
    }
    public boolean validateEntryValue(String entry , CommandContext ctx) throws IOException, InterruptedException {
        String[] split = entry.split("-");
        if(Integer.parseInt(split[0]) > lastMillisecondsTime){
            lastMillisecondsTime = Long.parseLong(split[0]);
            lastSequenceNumber = Integer.parseInt(split[1]);
            return true;
        }else if(Long.parseLong(split[0]) == lastMillisecondsTime &&
        Integer.parseInt(split[1]) > lastSequenceNumber){
            lastSequenceNumber = Integer.parseInt(split[1]);
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
