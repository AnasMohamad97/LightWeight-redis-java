import java.io.IOException;

public interface Command {
    // args[0] is the command name itself, e.g. {"SET", "key", "value"}
    void execute(String[] args, CommandContext ctx) throws IOException, InterruptedException;
}
