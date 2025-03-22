package fun.jaobabus.commandlib.argument;

import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.commandlib.util.GenericGetter;
import fun.jaobabus.commandlib.util.ParseError;

import java.util.List;


public interface AbstractArgument<ArgumentType, ExecutionContext extends AbstractExecutionContext>
{
    enum ParseMode
    {
        /// SpaceTerminated
        /// Simple parse mode (space terminated parsing)
        SpaceTerminated,

        /// TokenMode
        /// Parsing with lexing (now not support)
        TokenMode
    }

    ParseMode getParseMode();

    /// tapComplete
    /// @param fragment fragment of argument
    /// @param context execution context
    /// @return possible completes
    List<ArgumentType> tapComplete(String fragment, ExecutionContext context);

    /// parseSimple
    /// @param arg full string for parse
    ///
    /// Parse string to internal type
    ///
    /// @return parsed object
    ArgumentType parseSimple(String arg,
                             ExecutionContext context)
            throws ParseError;

    /// dumpSimple
    /// @param arg value to dump
    ///
    /// @return string parsable value
    String dumpSimple(ArgumentType arg,
                       ExecutionContext context);

    Class<ArgumentType> getArgumentClass();

    // Helper class
    abstract class Parametrized<T, EC extends AbstractExecutionContext> implements AbstractArgument<T, EC> {
        private final Class<T> argumentCLass;

        public Parametrized()
        {
            this(null);
        }

        public Parametrized(Class<T> clazz)
        {
            if (clazz == null)
                clazz = GenericGetter.get(getClass());
            this.argumentCLass = clazz;
        }

        @Override
        public Class<T> getArgumentClass() {
            return argumentCLass;
        }
    }

}
