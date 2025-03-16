package argument.restrictions;

import util.AbstractMessage;
import util.ParseError;

public class RestrictionError extends ParseError
{
    public RestrictionError(AbstractMessage msg) {
        super(msg);
    }
}
