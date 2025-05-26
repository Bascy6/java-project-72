package hexlet;

import hexlet.code.App;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static hexlet.code.App.readResourceFile;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;


public class AppTest {

    private Javalin app;
    private static MockWebServer mockWebServer;
    private static String mockServerUrl;

    private static String strToUtf8(String str) {
        return new String(str.getBytes(), UTF_8);
    }

    @BeforeAll
    public static void startMockServer() throws Exception {
        mockWebServer = new MockWebServer();
        mockServerUrl = mockWebServer.url("/").toString();
        MockResponse mockResponse = new MockResponse().setBody(readResourceFile("test.html"));
        mockWebServer.enqueue(mockResponse);
    }

    @AfterAll
    public static void shutdownMockServer() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    public final void setUp() throws SQLException, IOException {
        app = App.getApp();
    }

    @Test
    public void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.rootPath());
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body()).isNotNull();
            assertThat(response.body().string())
                    .contains(strToUtf8("Анализатор страниц"));
        });
    }

    @Test
    public void testUrlsPage() {
        JavalinTest.test(app, (server, client) -> {
            Url url = new Url();
            url.setName("https://example.com");
            UrlRepository.save(url);

            var response = client.get(NamedRoutes.urlsPath());
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body()).isNotNull();

            String responseBody = response.body().string();
            assertThat(responseBody)
                    .contains("https://example.com")
                    .contains(strToUtf8("Сайты"));
        });
    }

    @Test
    public void testUrlPage() {
        JavalinTest.test(app, (server, client) -> {
            Url url = new Url();
            url.setName("https://example.com");
            UrlRepository.save(url);

            long urlId = url.getId();

            var response = client.get(NamedRoutes.urlPath(urlId));
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body()).isNotNull();

            String responseBody = response.body().string();
            assertThat(responseBody)
                    .contains("https://example.com")
                    .contains(String.valueOf(urlId));
        });
    }

    @Test
    public void testCreateUrlWrong() {
        JavalinTest.test(app, (server, client) -> {
            var createResponse = client.post(NamedRoutes.urlsPath(), "example.com");
            assertThat(createResponse.code()).isEqualTo(400);

            Optional<Url> foundUrl = UrlRepository.findByName("example.com");
            assertThat(foundUrl).isEmpty();
        });
    }

    @Test
    public void testCreateUrlRight() {
        JavalinTest.test(app, (server, client) -> {
            var createResponse = client.post(NamedRoutes.urlsPath(), "url=https://example.com");
            assertThat(createResponse.code()).isEqualTo(200);

            Optional<Url> foundUrl = UrlRepository.findByName("https://example.com");
            assertThat(foundUrl)
                    .isPresent()
                    .hasValueSatisfying(url ->
                            assertThat(url.getName()).isEqualTo("https://example.com"));
        });
    }

    @Test
    public void testUrlChecks() throws SQLException {
        Url url = new Url(mockServerUrl);
        UrlRepository.save(url);

        JavalinTest.test(app, (server, client) -> {
            var response = client.post(NamedRoutes.urlChecksPath(url.getId()));
            assertThat(response.code()).isEqualTo(200);

            List<UrlCheck> checks = UrlCheckRepository.getEntitiesByUrlId(url.getId());
            assertThat(checks).isNotEmpty();

            UrlCheck check = checks.get(0);
            assertThat(check.getTitle()).isEqualTo("Test Title");
            assertThat(check.getH1()).isEqualTo("Test header");
            assertThat(check.getDescription()).isNull();
        });
    }
}
