package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlChecksRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AppTest {
    private static final String HTML_PATH = "src/test/resources/index.html";
    private static MockWebServer mockWebServer;
    private static Javalin app;
    private static String urlName;

    private static String getContentOfHtmlFile() throws IOException {
        var path = Paths.get(HTML_PATH);
        var lines = Files.readAllLines(path);
        return String.join("\n", lines);
    }

    @BeforeAll
    public static void generalSetUp() throws IOException {
        mockWebServer = new MockWebServer();
        urlName = mockWebServer.url("/").toString();
        var mockResponse = new MockResponse().setBody(getContentOfHtmlFile());
        mockWebServer.enqueue(mockResponse);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
        app.stop();
    }

    @BeforeEach
    public final void setUp() throws IOException, SQLException {
        app = App.getApp();
    }

    @Test
    public void tesRootPage() {
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
    public void testCreateInvalidPage() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=ya.ru";
            try (var response = client.post("/urls", requestBody)) {
                assertThat(response.code()).isEqualTo(200);
                assert response.body() != null;
                assertThat(response.body().string()).doesNotContain("ya.ru");
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

    @Test
    public void testCheckUrl() {
        JavalinTest.test(app, (server, client) -> {
            Url mockUrl = new Url(urlName);
            UrlRepository.save(mockUrl);

            try (var response = client.post(NamedRoutes.urlChecksPath(String.valueOf(mockUrl.getId())))) {
                assertThat(response.code()).isEqualTo(200);

                List<UrlCheck> urlChecks = UrlChecksRepository.getAllChecksForUrl(mockUrl.getId());
                assertThat(urlChecks.size()).isEqualTo(1);

                UrlCheck lastUrlCheck = UrlChecksRepository.getAllChecksForUrl(mockUrl.getId()).getFirst();
                assertThat(lastUrlCheck.getUrlId()).isEqualTo(1);
                assertThat(lastUrlCheck.getStatusCode()).isEqualTo(200);
                assertThat(lastUrlCheck.getCreatedAt()).isToday();
                assertThat(lastUrlCheck.getTitle()).contains("HTML test page");
                assertThat(lastUrlCheck.getH1()).contains("The best test page for all possible scenarios");
                assertThat(lastUrlCheck.getDescription()).contains("Discover this test HTML page tailored for web "
                        + "applications testing. Featuring headers, paragraphs, title and meta data, ideal for "
                        + "evaluating functionality.");
            }
        });
    }
}
