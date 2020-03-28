package execution;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CalculatorExecutorsBuilder implements ExecutorsBuilder {
    final Map<String, SingleExecutor> executors = new HashMap<String, SingleExecutor>();
    Properties namesToExecutors = new Properties();
    CalculatorExecutorsBuilder(InputStream config) throws IOException {
        namesToExecutors.load(config);
        Enumeration<String> names = (Enumeration<String>)namesToExecutors.propertyNames();
    }
    public SingleExecutor getExecutor(String commandName){
        SingleExecutor res = executors.get(commandName);
        if (res != null)
            return res;
        String className = namesToExecutors.getProperty(commandName);
        if (className == null)
            return null;
        try {
            res = (SingleExecutor) Class.forName("execution.singleExecutors." + className).getConstructor().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        executors.put(commandName, res);
        return res;
    }
}
