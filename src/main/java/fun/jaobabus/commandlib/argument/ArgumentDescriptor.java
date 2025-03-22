package fun.jaobabus.commandlib.argument;

import fun.jaobabus.commandlib.context.BaseArgumentContext;
import fun.jaobabus.commandlib.context.ContextualProcessor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class ArgumentDescriptor<T, ArgumentContext extends BaseArgumentContext>
{
    public static class Help {
        public String phrase;
        public String help;
    }

    public String name;
    public AbstractArgument<T, ArgumentContext> argument;
    public final Help help = new Help();
    public List<AbstractArgumentRestriction<T>> restrictions = new ArrayList<>();
    public Argument.Action action;
    public String defaultValue;
    public ContextualProcessor<T, ArgumentContext> processor;
    public Field field;
}
