package hexlet.code.controller;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class UrlChecksController {

    public static void create(Context context) throws SQLException {
        Long urlId = context.pathParamAsClass("id", Long.class).get();
        Url url = UrlRepository.findById(urlId)
                .orElseThrow(() -> new NotFoundResponse("Url with id" + urlId + "not found"));

        try {
            HttpResponse<String> response = Unirest.get(url.getName()).asString();

            Document document = Jsoup.parse(response.getBody());
            int statusCode = response.getStatus();
            String title = document.title();
            String h1 = document.select("h1").text();
            String description = document.select("meta[name=description]").attr("content");

            UrlCheckRepository.save(UrlCheck.builder()
                    .statusCode(statusCode)
                    .title(title)
                    .h1(h1)
                    .description(description)
                    .createdAt(LocalDateTime.now())
                    .urlId(urlId)
                    .build());

            context.sessionAttribute("flash", "Страница успешно проверена");
            context.sessionAttribute("flashType", "success");

        } catch (UnirestException e) {
            context.sessionAttribute("flash", "Некорректный адрес");
            context.sessionAttribute("flashType", "danger");
        } catch (SQLException e) {
            context.sessionAttribute("flash", "Ошибка в работе СУБД: " + e.getMessage());
            context.sessionAttribute("flashType", "danger");
        } catch (Exception e) {
            context.sessionAttribute("flash", "Произошла ошибка: " + e.getMessage());
            context.sessionAttribute("flashType", "danger");
        }

        context.redirect(NamedRoutes.urlPath(urlId));
        Unirest.shutDown();
    }
}
