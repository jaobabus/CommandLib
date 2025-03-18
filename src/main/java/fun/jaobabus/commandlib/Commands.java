package fun.jaobabus.commandlib;

import fun.jaobabus.commandlib.command.Command;
import fun.jaobabus.commandlib.examples.AddCommandExample;
import fun.jaobabus.commandlib.examples.EchoCommandExample;

public class Commands
{
    // @CommandRestriction(restriction = "perm plugin.echo.execute")
    @Command
    EchoCommandExample echo;

    // @CommandRestriction(restriction = "perm plugin.add.execute")
    @Command
    AddCommandExample add;

    @Command(aliasOf = "add")
    AddCommandExample adds;

}
