import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
