package Commands.Functional;

import Commands.Command;
import SharedResources.CommandContext;

import java.io.IOException;

public class PingCommand implements Command {
    @Override
    public void execute(String[] args, CommandContext ctx) throws IOException {
        ctx.encoder().pong();
    }
}
