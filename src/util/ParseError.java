package util;

public class ParseError extends Throwable {

    public ParseError(AbstractMessage message) {
        this.message = message;
    }

    AbstractMessage message;

    @Override
    public String toString()
    {
        if (message != null)
            return message.toString();
        else
            return "<null>";
    }

}
