import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {
    private final Map<String, Command> commands = new HashMap<>();
    public CommandRegistry register(String name , Command command) {
        commands.put(name.toUpperCase(), command);
        return this;
    }


    public void dispatch(String[] args, CommandContext ctx) throws IOException {
        if (args.length == 0) return;
        Command command = commands.get(args[0].toUpperCase());
        if (command == null) {
            ctx.encoder().errUnknownCommand();
            return;
        }
        try {
            command.execute(args, ctx);
        }catch (NumberFormatException e) {
            ctx.encoder().errSyntax();
        }

    }

    public static CommandRegistry defaultRegistry() {
        return new CommandRegistry()
                .register("PING", new PingCommand())
                .register("ECHO", new EchoCommand())
                .register("COMMAND", new CommandCommand())
                .register("SET", new SetCommand())
                .register("GET", new GetCommand())
                .register("RPUSH", new RpushCommand())
                .register("LRANGE", new LrangeCommand());
    }
}

