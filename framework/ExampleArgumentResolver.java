package framework;

import com.alibaba.fastjson.util.TypeUtils;
import com.github.pagehelper.PageHelper;
import org.springframework.core.MethodParameter;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ExampleArgumentResolver implements HandlerMethodArgumentResolver {


	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameter().getName().equals("example");
	}

	private static Pattern pattern = Pattern.compile("\\$\\d?(\\w+)");

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) throws Exception {
		Class<?> exampleClass = parameter.getParameterType();
		Object example = exampleClass.newInstance();
		Method method = ReflectionUtils.findMethod(exampleClass, "createCriteria");
		if (method == null) {
			throw new NoSuchMethodException(exampleClass.getName() + "找不到createCriteria方法");
		}

		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
		Map<Integer, List<Map.Entry<String, String[]>>> map = getCollect(request);
		map.values()
				.forEach(entries -> {
					Object criteria = Objects.requireNonNull(ReflectionUtils.invokeMethod(method, example));

					entries.forEach(stringEntry -> {
						String name = actuallyName(stringEntry.getKey());
						String methodName = "and" + StringUtils.capitalize(name);
						Method var = ReflectHelp.findMethod(criteria.getClass(), methodName);
						if (var == null) {
							var = ReflectHelp.findMethod(criteria.getClass(), methodName + "EqualTo");
						}

						if (var == null) {
							throw new RuntimeException("找不到" + methodName + "方法");
						}

						Class<?>[] classes = var.getParameterTypes();
						String[] paramArray = stringEntry.getValue();
						int len = classes.length;
						if (len == 2) {
							Class<?> type = getParameterizeType(exampleClass, name);
							String[] strings = paramArray[0].split(",");
							if (strings.length < 2) {
								throw new IllegalArgumentException("缺少参数");
							}
							Object arg0 = TypeUtils.castToJavaBean(strings[0], type);
							Object arg1 = TypeUtils.castToJavaBean(strings[1], type);
							ReflectionUtils.invokeMethod(var, criteria, arg0, arg1);
						} else if (len == 0) {
							ReflectionUtils.invokeMethod(var, criteria);
						} else if (len == 1) {
							Class<?> clazz = classes[0];
							Object arg;
							if (clazz.isAssignableFrom(List.class)) {
								Class<?> type = getParameterizeType(exampleClass, name);
								String[] arr = paramArray[0].split(",");
								int var2 = arr.length;
								List<Object> list = new ArrayList<>();
								for (int i = 0; i < var2; i++) {
									list.add(TypeUtils.castToJavaBean(arr[i], type));
								}
								arg = list;
							} else {
								arg = TypeUtils.castToJavaBean(paramArray[0],  clazz);
							}
							ReflectionUtils.invokeMethod(var, criteria, arg);
						}

					});

					Method or = Objects.requireNonNull(ReflectionUtils.findMethod(exampleClass, "or", criteria.getClass()));
					ReflectionUtils.invokeMethod(or, example, criteria);
				});

		String order = request.getParameter("order");
		String distinct = request.getParameter("distinct");
		if (order != null) {
			Method setOrder = ReflectionUtils.findMethod(exampleClass, "setOrderByClause", String.class);
			ReflectionUtils.invokeMethod(setOrder, example, order);
		}

		if (distinct != null) {
			Method setDistinct = ReflectionUtils.findMethod(exampleClass, "setDistinct", boolean.class);
			ReflectionUtils.invokeMethod(setDistinct, example, Boolean.valueOf(distinct));
		}
		startPage(request);
		return example;
	}

	// clazz 是example 的class， name 带有in
	private Class<?> getParameterizeType(Class<?> clazz, String name)  {
		String load = clazz.getName();
		load = load.substring(0, load.lastIndexOf("."));
		load = load + "." + clazz.getSimpleName().replace("Example", "");
		try {
			clazz = Class.forName(load);
			return clazz.getDeclaredField(name.substring(0, name.length() - 2)).getType();
		} catch (NoSuchFieldException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private String actuallyName(String parameter) {
		Matcher matcher = pattern.matcher(parameter);
		if (matcher.find()) {
			return matcher.group(1);
		}
		throw new RuntimeException("获取真实参数名异常" + parameter);
	}


	private Map<Integer, List<Map.Entry<String, String[]>>> getCollect(HttpServletRequest request) {
		return request.getParameterMap()
				.entrySet().stream().filter(stringEntry -> pattern.matcher(stringEntry.getKey()).matches())
				.collect(Collectors.groupingBy(stringEntry -> {
					String name = stringEntry.getKey();
					char c = name.charAt(1);
					if (Character.isAlphabetic(c)) {
						return 0;
					}
					return Character.getNumericValue(c);
				}));
	}

	private void startPage(HttpServletRequest request) {
		String pageNumStr = request.getParameter("pageNum");
		String pageSizeStr = request.getParameter("pageSize");
		if (pageNumStr != null && pageSizeStr != null) {
			int pageNum = Integer.valueOf(pageNumStr);
			int pageSize = Integer.valueOf(pageSizeStr);
			PageHelper.startPage(pageNum, pageSize);
		}
	}
}
