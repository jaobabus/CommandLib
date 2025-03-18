package command;

import util.AbstractExecutionContext;
import util.ParseError;

import java.util.List;


public interface AbstractCommandParser<ArgumentList>
{
    /// parseSimple
    /// @param args split by space command line
    /// @return parsed internal argument list
    ArgumentList parseSimple(String[] args,
                             CommandArgumentList arguments,
                             AbstractExecutionContext context) throws ParseError;
    /// tabCompleteSimple
    /// @param args split by space command line, last arg is target for complete
    /// @return possible completes
    List<String> tabCompleteSimple(String[] args,
                                   CommandArgumentList arguments,
                                   AbstractExecutionContext context);

}
