import java.io.IOException;

public class PingCommand implements Command {
    @Override
    public void execute(String[] args, CommandContext ctx) throws IOException {
        ctx.encoder().pong();
    }
}
