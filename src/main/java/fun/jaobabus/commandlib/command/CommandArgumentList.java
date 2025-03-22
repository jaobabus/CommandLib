package fun.jaobabus.commandlib.command;

import fun.jaobabus.commandlib.argument.AbstractArgument;
import fun.jaobabus.commandlib.argument.AbstractArgumentRestriction;
import fun.jaobabus.commandlib.argument.Argument;
import fun.jaobabus.commandlib.argument.ArgumentDescriptor;
import fun.jaobabus.commandlib.context.ContextualProcessor;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.commandlib.util.ParseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class CommandArgumentList
{
    public final List<ArgumentDescriptor<?, ?>> originalStream;
    public Map<String, ArgumentDescriptor<?, ?>> flags;
    public List<ArgumentDescriptor<?, ?>> arguments;
    public Map<String, ContextualProcessor<?, ?>> contextualArguments;

    public CommandArgumentList(List<ArgumentDescriptor<?, ?>> original) {
        originalStream = original;
        flags = new HashMap<>();
        arguments = new ArrayList<>();
    }

    public abstract Object newInstance();
    public abstract Class<?> getType();

    public void initContext(AbstractExecutionContext ec)
    {
        for (var arg : contextualArguments.values()) {
            try {
                arg.makeSourceContextIfAbsent(ec);
            } catch (ParseError e) {
                throw new RuntimeException(e);
            }
        }
    }

}
