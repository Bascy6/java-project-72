package hexlet.code.dto;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class UrlsPage extends BasePage {
    private List<Url> urls;
    private Map<Long, UrlCheck> urlChecks = new HashMap<>();
}
