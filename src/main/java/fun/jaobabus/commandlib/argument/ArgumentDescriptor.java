package fun.jaobabus.commandlib.argument;

import fun.jaobabus.commandlib.util.AbstractExecutionContext;

import java.util.ArrayList;
import java.util.List;


public class ArgumentDescriptor<T, ExecutionContext extends AbstractExecutionContext>
{
    public static class Help {
        public String phrase;
        public String help;
    }

    public String name;
    public AbstractArgument<T, ExecutionContext> argument;
    public final Help help = new Help();
    public List<AbstractArgumentRestriction<T>> restrictions = new ArrayList<>();
    public Argument.Action action;
    public String defaultValue;
}
