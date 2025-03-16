package argument;

import command.AbstractCommand;
import util.AbstractExecutionContext;
import util.AbstractMessage;
import util.GenericGetter;
import util.ParseError;

import java.lang.reflect.ParameterizedType;
import java.util.List;


public interface AbstractArgument<ArgumentType>
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

    /* getPhase, getShortUsage, getHelp used for help
      Expected usage
          Help:
            echo [-n] <string>...
          Usage:
            echo -n Some string
          Arguments:
            -n - No newline
            <string> - Any string
     */

    // example: '<string>'
    String getPhase();

    // example: 'Some string'
    String getShortUsage();

    // example: 'Any string'
    AbstractMessage getHelp();

    /// tapComplete
    /// @param fragment fragment of argument
    /// @param context execution context
    /// @return possible completes
    List<String> tapComplete(String fragment, AbstractExecutionContext context);

    /// parseSimple
    /// @param arg full string for parse
    ///
    /// Parse string to internal type
    ///
    /// @return parsed object
    ArgumentType parseSimple(String arg,
                             AbstractExecutionContext context)
            throws ParseError;

    Class<ArgumentType> getArgumentClass();

    // Helper class
    abstract class Parametrized<T> implements AbstractArgument<T> {
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
