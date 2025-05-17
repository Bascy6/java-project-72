package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class AppTest {

    Javalin app;

    @BeforeEach
    public final void setUp() throws IOException, SQLException {
        app = App.getApp();
    }

    @Test
    public void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");
            assertThat(response.code()).isEqualTo(200);
            assert response.body() != null;
            assertThat(response.body().string()).contains("Анализатор страниц");
        });
    }

    @Test
    public void testCreatePage() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://www.example.com";
            try (var response = client.post("/urls", requestBody)) {
                assertThat(response.code()).isEqualTo(200);
                assert response.body() != null;
                assertThat(response.body().string()).contains("https://www.example.com");
            }
        });
    }

    @Test
    public void testUrlsPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    public void testUrlPage() throws SQLException {
        var url = new Url(1, "https://www.example.com", new Timestamp(new Date().getTime()));
        UrlRepository.save(url);
        JavalinTest.test(app, (server, client) -> assertThat(client.get("/urls/" + url.getId()).code()).isEqualTo(200));
    }

    @Test
    void testUrlNotFound() {
        JavalinTest.test(app, (server, client) -> assertThat(client.get("/users/123456789").code()).isEqualTo(404));
    }
}
