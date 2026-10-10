package Commands;

import Commands.Functional.CommandCommand;
import Commands.Functional.EchoCommand;
import Commands.Functional.PingCommand;
import Commands.Functional.TypeCommand;
import Commands.StreamCommands.XAddCommand;
import Commands.StringCommads.GetCommand;
import Commands.StringCommads.SetCommand;
import SharedResources.CommandContext;
import Commands.ListCommands.*;

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
        } catch (InterruptedException e) {
           ctx.encoder().writeError(e.getMessage());
        }

    }

    public static CommandRegistry defaultRegistry() {
        return new CommandRegistry()
                .register("XADD", new XAddCommand(){})
                .register("Type", new TypeCommand() {})
                .register("BLPOP", new BlpopCommand() {})
                .register("LPOP", new LpopCommand())
                .register("LLEN" , new LlenCommand())
                .register("LPUSH", new LpushCommand())
                .register("PING", new PingCommand())
                .register("ECHO", new EchoCommand())
                .register("COMMAND", new CommandCommand())
                .register("SET", new SetCommand())
                .register("GET", new GetCommand())
                .register("RPUSH", new RpushCommand())
                .register("LRANGE", new LrangeCommand());

    }
}

