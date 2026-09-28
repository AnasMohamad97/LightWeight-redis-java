import java.io.IOException;

// redis-cli sends COMMAND on connect; an empty array is enough for it.
public class CommandCommand implements Command {
    @Override
    public void execute(String[] args, CommandContext ctx) throws IOException {
        ctx.encoder().writeArrayHeader(0).flush();
    }
}
