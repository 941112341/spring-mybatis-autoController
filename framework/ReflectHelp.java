package framework;

import java.lang.reflect.Method;
import java.util.List;

public class ReflectHelp {

	public static Method findMethod( Class<?> clazz, String methodName) {
		for (Method method : clazz.getDeclaredMethods()) {
			if (method.getName().equals(methodName)) {
				return method;
			}
		}
		return null;
	}


}
