package fun.jaobabus.commandlib.command;

import fun.jaobabus.commandlib.argument.AbstractArgument;
import fun.jaobabus.commandlib.argument.AbstractArgumentRestriction;
import fun.jaobabus.commandlib.argument.Argument;
import fun.jaobabus.commandlib.argument.ArgumentDescriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class CommandArgumentList
{
    public final List<ArgumentDescriptor> originalStream;
    public Map<String, ArgumentDescriptor> flags;
    public List<ArgumentDescriptor> arguments;

    public CommandArgumentList(List<ArgumentDescriptor> original) {
        originalStream = original;
        flags = new HashMap<>();
        arguments = new ArrayList<>();
    }

    public abstract Object newInstance();
    public abstract Class<?> getType();

}
