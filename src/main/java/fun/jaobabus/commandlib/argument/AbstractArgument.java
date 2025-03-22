package fun.jaobabus.commandlib.argument;

import fun.jaobabus.commandlib.context.BaseArgumentContext;
import fun.jaobabus.commandlib.util.GenericGetter;
import fun.jaobabus.commandlib.util.ParseError;

import java.util.List;


public interface AbstractArgument<ArgumentType, ArgumentContext extends BaseArgumentContext>
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
    List<ArgumentType> tapComplete(String fragment, ArgumentContext context);

    /// parseSimple
    /// @param arg full string for parse
    ///
    /// Parse string to internal type
    ///
    /// @return parsed object
    ArgumentType parseSimple(String arg,
                             ArgumentContext context)
            throws ParseError;

    /// dumpSimple
    /// @param arg value to dump
    ///
    /// @return string parsable value
    String dumpSimple(ArgumentType arg,
                      ArgumentContext context);

    Class<ArgumentType> getArgumentClass();
    Class<ArgumentContext> getContextClass();

    // Helper class
    abstract class Parametrized<T, AC extends BaseArgumentContext> implements AbstractArgument<T, AC> {
        private final Class<T> argumentCLass;
        private final Class<AC> contextCLass;

        public Parametrized()
        {
            this(null, null);
        }

        public Parametrized(Class<T> clazz, Class<AC> ctxClass)
        {
            if (clazz == null)
                clazz = GenericGetter.get(getClass(), 0);
            this.argumentCLass = clazz;

            if (ctxClass == null)
                ctxClass = GenericGetter.get(getClass(), 1);
            this.contextCLass = ctxClass;
        }

        @Override
        public Class<T> getArgumentClass() {
            return argumentCLass;
        }

        @Override
        public Class<AC> getContextClass() {
            return contextCLass;
        }
    }

}
