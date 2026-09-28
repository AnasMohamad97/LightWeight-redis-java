import java.io.IOException;

public class SetCommand implements Command {
    @Override
    public void execute(String[] args, CommandContext ctx) throws IOException {
        if(args.length == 3) {
           CachKey cachKey = new CachKey(args[1],args[2]);
           ctx.keyValueMap().put(args[1],cachKey);
           ctx.encoder().ok();
        }else if(args.length == 5) {
            long expiryTime = (args[3].equalsIgnoreCase("PX"))?Long.parseLong(args[4]):(args[3].equalsIgnoreCase("EX"))?Long.parseLong(args[4])*1000:-1;

            if(expiryTime == -1){
                ctx.encoder().errSyntax().flush();
                return;
            }
            long timeLimit =  expiryTime+ System.currentTimeMillis();
            CachKey newCachedValue = new CachKey(args[1],args[2] , timeLimit);
            ctx.keyValueMap().put(args[1], newCachedValue);
            ctx.encoder().ok();
        }else {
            ctx.encoder().errWrongNumArgs(args[0].toLowerCase()).flush();
        }
    }
}
