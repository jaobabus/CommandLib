package fun.jaobabus.commandlib.examples;

import fun.jaobabus.commandlib.argument.Argument;
import fun.jaobabus.commandlib.argument.ArgumentRestriction;
import fun.jaobabus.commandlib.argument.arguments.ArgumentRegistry;
import fun.jaobabus.commandlib.argument.restrictions.ArgumentRestrictionRegistry;
import fun.jaobabus.commandlib.command.AbstractCommand;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.commandlib.util.AbstractMessage;

public class AddCommandExample extends AbstractCommand.Parametrized<AddCommandExample.AddArguments, AbstractExecutionContext>
{
    public static class AddArguments
    {
        @Argument(action = Argument.Action.FlagStoreValue, defaultValue = "0")
        @ArgumentRestriction(restriction = "IntRange 0 0x7FFFFFFFFFFFFFFF")
        public Long s;

        @Argument()
        public Long a;

        @Argument()
        public Long b;

        @Argument(action = Argument.Action.Optional, defaultValue = "0")
        @ArgumentRestriction(restriction = "IntRange -0x7FFFFFFFFFFFFFFF -1")
        public Long neg;

        @Argument(action = Argument.Action.Optional)
        @ArgumentRestriction(restriction = "StringRange 1 2 3")
        public String k;
    }

    public AddCommandExample(ArgumentRegistry reg, ArgumentRestrictionRegistry restRegistry) {
        super(reg, restRegistry);
    }

    @Override
    public AbstractMessage execute(AddArguments input, AbstractExecutionContext context) {
        String msg = String.valueOf(input.a + input.b - input.neg - input.s);

        return new AbstractMessage() {
            @Override
            public String toString() {
                return msg;
            }
        };
    }

}
