package hexlet.code.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class Utils {

    public static String normalizeUrlName(String urlName) throws
            IllegalArgumentException,
            MalformedURLException,
            URISyntaxException {
        URI inputUri = new URI(urlName);
        URL inputUrl = inputUri.toURL();

        return String.format(
                "%s://%s%s",
                inputUrl.getProtocol(),
                inputUrl.getHost(),
                (inputUrl.getPort() == -1 ? "" : ":" + inputUrl.getPort()));
    }
}
