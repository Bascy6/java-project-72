package hexlet.code.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import lombok.Setter;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UrlCheck {
    private Long id;
    private Integer statusCode;
    private String title;
    private String h1;
    private String description;
    private Long urlId;
    private LocalDateTime createdAt;

    public UrlCheck(long checkId, int statusCode, String title, String h1, LocalDateTime createdAt, long checkUrlId) {
        this.id = checkId;
        this.statusCode = statusCode;
        this.title = title;
        this.h1 = h1;
        this.createdAt = createdAt;
        this.urlId = checkUrlId;
    }

    public UrlCheck(Integer statusCode, LocalDateTime createdAt) {
        this.statusCode = statusCode;
        this.createdAt = createdAt;
    }
}
