package fun.jaobabus.commandlib;

import fun.jaobabus.commandlib.command.Command;
import fun.jaobabus.commandlib.examples.AddCommandExample;
import fun.jaobabus.commandlib.examples.EchoCommandExample;

public class Commands
{
    // @CommandRestriction(restriction = "perm plugin.echo.execute")
    @Command
    public EchoCommandExample echo;

    // @CommandRestriction(restriction = "perm plugin.add.execute")
    @Command
    public AddCommandExample add;

    @Command(aliasOf = "add")
    public AddCommandExample adds;

}
