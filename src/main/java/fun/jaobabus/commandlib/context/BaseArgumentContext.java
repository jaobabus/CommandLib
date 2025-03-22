package fun.jaobabus.commandlib.context;

import java.util.Set;
import java.util.HashSet;

public class BaseArgumentContext
{
    Set<String> dependencyArguments;
    Set<String> appliedDependencyArguments = new HashSet<>();
}
