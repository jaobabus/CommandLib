package fun.jaobabus.commandlib.examples;

import fun.jaobabus.commandlib.argument.Argument;
import fun.jaobabus.commandlib.argument.arguments.ArgumentRegistry;
import fun.jaobabus.commandlib.argument.restrictions.ArgumentRestrictionRegistry;
import fun.jaobabus.commandlib.command.AbstractCommand;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.commandlib.util.AbstractMessage;

public class EchoCommandExample extends AbstractCommand.Parametrized<EchoCommandExample.EchoArguments>
{
    public static class EchoArguments
    {
        @Argument(action = Argument.Action.FlagStoreTrue)
        public Boolean n;

        @Argument(action = Argument.Action.VarArg)
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
