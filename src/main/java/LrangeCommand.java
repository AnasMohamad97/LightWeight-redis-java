import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LrangeCommand implements Command {
    @Override
    public void execute(String[] args, CommandContext ctx) throws IOException {
        if(args.length != 4) {
            ctx.encoder().errWrongNumArgs(args[0].toLowerCase());
            return;
        }
        String key = args[1];
        int start = Integer.parseInt(args[2]);
        start = Math.max(start,0);
        int end =Integer.parseInt(args[3]);
        List<String> result = ctx.database().lrange(key,start,end);

        if(result.isEmpty()) {
            ctx.encoder().writeArrayHeader(0).flush();
            return;
        }
        ctx.encoder().writeArrayHeader(result.size()).flush();
        for(String s : result) ctx.encoder().WriteBulkString(s);
        ctx.encoder().flush();
//        int size = list.size();
//        int start = normalize(Integer.parseInt(args[2]),size);
//        start = Math.max(start,0);
//        int end = normalize(Integer.parseInt(args[3]), size);
//
//        if(start >= size|| start > end)  {
//            ctx.encoder().writeArrayHeader(0).flush();
//            return;
//        }
//        end = Math.min(end+1,size);
//        ctx.encoder().writeArrayHeader(end-start).flush();
//         for (CacheKey cacheKey : list.subList(start,end)) {
//             ctx.encoder().WriteBulkString(cacheKey.getValue());
//         }
//         ctx.encoder().flush();
    }
    public static int normalize(int index, int size) {
        return index<0?index+size:index;
    }
}
