package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static hexlet.code.repository.UrlRepository.getEntities;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

public class AppTest {

    private static MockWebServer mockWebServer;
    private final String realUrl = "https://example.com";
    private final String fakeUrl = "hexlet.io";
    Javalin app;

    @BeforeAll
    public static void beforeAll() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }
    @BeforeEach
    public final void setUp() throws IOException, SQLException {
        app = App.getApp();
    }

    @AfterAll
    public static void afterAll() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Analyzer");
        });
    }

    @Test
    public void testIndex() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    public void testUrlNotFound() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/999999");
            assertThat(response.code()).isEqualTo(404);
        });
    }

    @Test
    public void testUrlPage() {
        JavalinTest.test(app, (server, client) -> {
            var url = new Url(realUrl);
            UrlRepository.save(url);
            var response = client.get("/urls/" + url.getId());
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("https://example.com");
        });
    }

    @Test
    public void testUrl() {
        JavalinTest.test(app, (server, client) -> {
            String body = "url=" + realUrl;
            var response = client.post("/urls", body);
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string().contains("https://example.com"));
            assertThat(getEntities().size()).isEqualTo(1);
            assertThat(getEntities().get(0).getName()).isEqualTo("https://example.com");
        });
    }

    @Test
    public void testInvalidUrl() {
        JavalinTest.test(app, (server, client) -> {
            String body = "url=" + fakeUrl;
            var response = client.post("/urls", body);
            assertThat(response.code()).isEqualTo(200);
            assertThat(getEntities().size()).isEqualTo(0);
        });
    }

    @Test
    public void testChecks() throws IOException, SQLException {
        var page = Files.readString(Paths.get("./src/test/resources/test.html"));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(page));
        var path = mockWebServer.url("/").toString();
        Url url = new Url(path);
        UrlRepository.save(url);

        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls/" + url.getId() + "/checks");
            assertThat(response.code()).isEqualTo(200);
            var check = UrlCheckRepository.getCheck(url.getId()).get();
            assertThat(check.getTitle()).isEqualTo("Title");
            assertThat(check.getH1()).isEqualTo("h1");
            assertThat(check.getDescription()).isEqualTo("description");
            assertThat(check.getStatusCode()).isEqualTo(200);
        });
    }
}
