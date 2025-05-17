package hexlet.code.model;

import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Getter
@Setter
public final class UrlCheck {
    private int id;
    private int statusCode;
    private String title;
    private String h1;
    private String description;
    private long urlId;
    private Timestamp createdAt;

    public UrlCheck(long urlId, int statusCode) {
        this.statusCode = statusCode;
        this.urlId = urlId;
    }
}
