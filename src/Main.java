import argument.arguments.DefaultArguments;
import argument.restrictions.DefaultRestrictions;
import command.AbstractCommand;
import examples.AddCommandExample;
import examples.EchoCommandExample;
import util.AbstractExecutionContext;
import util.ParseError;

public class Main {
    public static void main(String[] args) {
        var reg = DefaultArguments.getDefaultArgumentsRegistry();
        var restReg = DefaultRestrictions.getDefaultRegistry();
        var context = new AbstractExecutionContext();

        runExample(new EchoCommandExample(reg, restReg), "-n 123 \\n", context);
        runExample(new AddCommandExample(reg, restReg), "10 20", context);
        runExample(new AddCommandExample(reg, restReg), "-s500 10 50", context);
        runExample(new AddCommandExample(reg, restReg), "10 20 -800", context);
    }

    @SuppressWarnings("unchecked")
    static void runExample(AbstractCommand<?> cmd, String cmdline, AbstractExecutionContext context)
    {
        System.out.println("Execute '" + cmdline + "'");
        try {
            var input = cmd.getParser().parseSimple(cmdline.split(" "), cmd.getArgumentList(), context);
            System.out.print(((AbstractCommand<Object>)cmd).execute(input, context).toString());
        }
        catch (ParseError e) {
            System.out.println("Error: " + e);
        }
        System.out.println();
    }
}