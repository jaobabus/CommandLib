package fun.jaobabus.commandlib.argument;

import java.util.ArrayList;
import java.util.List;


public class ArgumentDescriptor
{
    public static class Help {
        public String phrase;
        public String help;
    }

    public String name;
    public AbstractArgument<?> argument;
    public final Help help = new Help();
    public List<AbstractArgumentRestriction<?>> restrictions = new ArrayList<>();
    public Argument.Action action;
}
