package framework;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PackageLoader {

    public static List<Class<?>> load(String resource) throws FileNotFoundException, ClassNotFoundException {
        check(resource);
        URL url = PackageLoader.class.getClassLoader().getResource(resource.replaceAll("\\.", "/"));
        String prefix = resource.substring(0, resource.lastIndexOf("."));
        List<Class<?>> list = new ArrayList<>();
        try {
            if (url == null) {
                throw new FileNotFoundException("找不到" + resource);
            }
            File file = new File(url.toURI());
            return loadFile(prefix, file, list);
        } catch (URISyntaxException e) {
            throw new FileNotFoundException(e.getReason());
        }
    }

    private static List<Class<?>> loadFile(String prefix, File file, List<Class<?>> list) throws ClassNotFoundException {
        if (file.isFile()) {
            String name = file.getName();
            String simpleName = name.substring(0, name.lastIndexOf("."));
            Class<?> clazz = Class.forName(prefix + "." + simpleName);
            list.add(clazz);
        } else if (file.isDirectory()) {
            for (File f : Objects.requireNonNull(file.listFiles())) {
                loadFile(prefix + "." + file.getName(), f, list);
            }
        }
        return list;
    }

    public static List<Class<?>> getClass(String resource) throws FileNotFoundException, ClassNotFoundException {
        List<Class<?>> clazz = load(resource);
        return clazz.stream()
                .filter(aClass -> aClass.isAnnotationPresent(ResponseBody.class) ||
                        aClass.isAnnotationPresent(RestController.class))
                .collect(Collectors.toList());
    }


    private static void check(String resource) {
        if (resource == null || !resource.matches("(\\w+\\.)*\\w+")) {
            throw new IllegalArgumentException("不合法的包名");
        }
    }

    public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException {
        List<Class<?>> clazz = load("com.dao");
        System.out.println(clazz);
    }
}
