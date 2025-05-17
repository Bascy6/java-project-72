package hexlet.code.dto;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
public final class UrlsPage extends BasePage {
    private List<Url> urls;
    private Map<Integer, UrlCheck> allUrlsLastChecks;
}
