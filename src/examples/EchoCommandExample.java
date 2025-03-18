package examples;

import argument.Argument;
import argument.Flag;
import argument.arguments.ArgumentRegistry;
import argument.restrictions.ArgumentRestrictionRegistry;
import command.AbstractCommand;
import util.AbstractExecutionContext;
import util.AbstractMessage;

public class EchoCommandExample extends AbstractCommand.Parametrized<EchoCommandExample.EchoArguments>
{
    public static class EchoArguments
    {
        @Flag(action = Flag.Action.StoreTrue)
        public Boolean n;

        @Argument(vararg = true)
        public String[] messages;
    }

    public EchoCommandExample(ArgumentRegistry reg, ArgumentRestrictionRegistry restRegistry) {
        super(reg, restRegistry);
    }

    @Override
    public AbstractMessage execute(EchoArguments input, AbstractExecutionContext context) {
        String msg =
                String.join(" ", input.messages)
                + (input.n ? "" : "\n");

        return new AbstractMessage() {
            @Override
            public String toString() {
                return msg;
            }
        };
    }

}
