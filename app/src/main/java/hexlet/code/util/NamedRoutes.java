package hexlet.code.util;

public class NamedRoutes {
    public static String rootPath() {
        return "/";
    }

    public static String urlsPath() {
        return "/urls";
    }

    public static String urlPath(String id) {
        return "/urls/" + id;
    }

    public static String urlPath(long id) {
        return urlPath(String.valueOf(id));
    }

    public static String urlChecksPath(String id) {
        return "/urls/" + id + "/checks";
    }

    public static String urlChecksPath(long id) {
        return urlChecksPath(String.valueOf(id));
    }
}
