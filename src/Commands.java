import command.Command;
import command.CommandRestriction;
import examples.AddCommandExample;
import examples.EchoCommandExample;

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
