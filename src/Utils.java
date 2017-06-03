import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Utils {
    public static void sleep(int msTime) {
        try {
            Thread.sleep(msTime);
        } catch (InterruptedException exception) {
            Utils.displayError("Sleep error: " + exception.getMessage());
        }
    }

    public static void displayError(String message) {
        Utils.displayError(message, null);
    }

    public static void displayError(String message, JRootPane JRootPane) {
        JOptionPane.showMessageDialog(JRootPane,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }

    public static String getClassName(Object object) {
        Class<?> enclosingClass = object.getClass().getEnclosingClass();

        if (enclosingClass != null) {
            return enclosingClass.getName();
        }

        return object.getClass().getName();
    }

    public static void invokeMethod(Object classInstance, String methodName,
        Object... parameters) {
        try {
            String className = Utils.getClassName(classInstance);
            Class<?> classObject = Class.forName(className);

            Method method
                = classObject.getDeclaredMethod(methodName,
                Utils.getParameterTypes(parameters));
            method.setAccessible(true);
            method.invoke(classInstance, parameters);
        } catch (ClassNotFoundException
                | NoSuchMethodException
                | IllegalAccessException
                | InvocationTargetException exception) {
            System.out.println("Class error: " + exception.getMessage());
        }
    }

    public static Class[] getParameterTypes(Object... parameters) {
        Class parameterTypes[] = new Class[parameters.length];
        int i = 0;

        for (Object parameter : parameters) {
            parameterTypes[i++] = parameter.getClass();
        }

        return parameterTypes;
    }
}
