package fun.jaobabus.commandlib.command;

import fun.jaobabus.commandlib.argument.AbstractArgument;
import fun.jaobabus.commandlib.argument.AbstractArgumentRestriction;
import fun.jaobabus.commandlib.argument.Argument;
import fun.jaobabus.commandlib.argument.ArgumentDescriptor;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class CommandArgumentList<ExecutionContext extends AbstractExecutionContext>
{
    public final List<ArgumentDescriptor<?, ExecutionContext>> originalStream;
    public Map<String, ArgumentDescriptor<?, ExecutionContext>> flags;
    public List<ArgumentDescriptor<?, ExecutionContext>> arguments;

    public CommandArgumentList(List<ArgumentDescriptor<?, ExecutionContext>> original) {
        originalStream = original;
        flags = new HashMap<>();
        arguments = new ArrayList<>();
    }

    public abstract Object newInstance();
    public abstract Class<?> getType();

}
