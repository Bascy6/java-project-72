package hexlet.code.dto;

import hexlet.code.model.Url;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class UrlsPage {
    private List<Url> urls;
}
