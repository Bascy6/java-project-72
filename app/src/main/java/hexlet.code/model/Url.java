package hexlet.code.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
public class Url {
    private int id;

    @ToString.Include
    private String name;

    private Timestamp createdAt;

    public Url(String name) {
        this.name = name;
    }

    public Url(int id, String name, Timestamp createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }
}
