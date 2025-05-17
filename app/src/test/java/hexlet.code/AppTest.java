package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlChecksRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.utils.NamedRoutes;
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
            assertThat(response.body()).isNotNull();
            assertThat(response.body().string()).contains("Анализатор страниц");
        });
    }

    @Test
    public void testCreatePage() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://www.example.com";
            try (var response = client.post("/urls", requestBody)) {
                assertThat(response.code()).isEqualTo(200);
                assertThat(response.body()).isNotNull();
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
                assertThat(response.body()).isNotNull();
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
        var url = new Url("https://www.example.com");
        UrlRepository.save(url);

        JavalinTest.test(app, (server, client) -> {
            try (var response = client.get("/urls/" + url.getId())) {
                assertThat(response.code()).isEqualTo(200);
            }
        });
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

            try (var response = client.post(NamedRoutes.urlChecksPath(mockUrl.getId()))) {
                assertThat(response.code()).isEqualTo(200);

                List<UrlCheck> urlChecks = UrlChecksRepository.getAllChecksForUrl(mockUrl.getId());
                assertThat(urlChecks.size()).isEqualTo(1);

                UrlCheck lastUrlCheck = UrlChecksRepository.getAllChecksForUrl(mockUrl.getId()).getFirst();

                assertThat(lastUrlCheck.getStatusCode()).isEqualTo(200);
                assertThat(lastUrlCheck.getCreatedAt()).isToday();
                assertThat(lastUrlCheck.getTitle()).contains("title");
                assertThat(lastUrlCheck.getH1()).contains("Level 1 header");
                assertThat(lastUrlCheck.getDescription()).contains("some description");
            }
        });
    }
}
