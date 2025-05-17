package hexlet.code.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BasePage {
    private String flash;
    private String alertType;
    private List<Error> errors;
}
