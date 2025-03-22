package fun.jaobabus.commandlib.command;

import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.commandlib.util.ParseError;

import java.util.List;


public interface AbstractCommandParser<ArgumentList, ExecutionContext extends AbstractExecutionContext>
{
    /// parseSimple
    /// @param args split by space command line
    /// @return parsed internal argument list
    ArgumentList parseSimple(String[] args,
                             CommandArgumentList arguments,
                             ExecutionContext context) throws ParseError;
    /// tabCompleteSimple
    /// @param args split by space command line, last arg is target for complete
    /// @return possible completes
    List<String> tabCompleteSimple(String[] args,
                                   CommandArgumentList arguments,
                                   ExecutionContext context);

}
