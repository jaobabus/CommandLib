package fun.jaobabus.commandlib;

import fun.jaobabus.commandlib.argument.arguments.DefaultArguments;
import fun.jaobabus.commandlib.argument.restrictions.DefaultRestrictions;
import fun.jaobabus.commandlib.command.CommandBuilder;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.commandlib.util.ParseError;

import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        var reg = DefaultArguments.getDefaultArgumentsRegistry();
        var restReg = DefaultRestrictions.getDefaultRegistry();
        var context = new AbstractExecutionContext();
        context.executor = "<Main>";
        context.shortTermCache = new HashMap<>();

        var builder = new CommandBuilder<>(Commands.class);
        builder.fillOriginalStream(reg, restReg);
        var myCommands = builder.build();

        runExample(myCommands.get("echo"), "-n 123 \\n", context);
        runExample(myCommands.get("add"), "10 20", context);
        runExample(myCommands.get("add"), "-s500 10 50", context);
        runExample(myCommands.get("adds"), "10 20 -800", context);
        showComplete(myCommands.get("echo"), "-", context);
        showComplete(myCommands.get("echo"), "--", context);
        showComplete(myCommands.get("echo"), "-n", context);
        showComplete(myCommands.get("adds"), "1 1 1 ", context);
    }

    static void runExample(CommandBuilder.StandAloneCommand<AbstractExecutionContext> cmd, String cmdline, AbstractExecutionContext context)
    {
        System.out.println("Execute '" + cmdline + "'");
        try {
            System.out.print(cmd.execute(cmdline.split(" "), context).toString());
        }
        catch (ParseError e) {
            System.out.println("Error: " + e);
        }
        System.out.println();
    }

    static void showComplete(CommandBuilder.StandAloneCommand<AbstractExecutionContext> cmd, String cmdline, AbstractExecutionContext context)
    {
        String[] args = cmdline.split(" ", -1);
        System.out.println("Complete '" + cmdline + "'");
        System.out.print(cmd.tabComplete(args, context).toString());
        System.out.println();
    }
}