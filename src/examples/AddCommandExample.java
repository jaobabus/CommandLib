package examples;

import argument.ArgumentRestriction;
import argument.arguments.ArgumentRegistry;
import argument.Flag;
import argument.Argument;
import argument.restrictions.ArgumentRestrictionRegistry;
import command.AbstractCommand;
import util.AbstractExecutionContext;
import util.AbstractMessage;

public class AddCommandExample extends AbstractCommand.Parametrized<AddCommandExample.AddArguments>
{
    public static class AddArguments
    {
        @Flag(action = Flag.Action.StoreValue)
        @ArgumentRestriction(restriction = "IntRange 0 0x7FFFFFFFFFFFFFFF")
        public Long s = 0L;

        @Argument()
        public Long a;

        @Argument()
        public Long b;

        @Argument(optional = true)
        @ArgumentRestriction(restriction = "IntRange -0x7FFFFFFFFFFFFFFF -1")
        public Long neg = 0L;
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
