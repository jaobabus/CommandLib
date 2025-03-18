package fun.jaobabus.commandlib.argument.restrictions;

import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.commandlib.util.ParseError;

public class RestrictionError extends ParseError
{
    public RestrictionError(AbstractMessage msg) {
        super(msg);
    }
}
