package hexlet.code.controller;

import hexlet.code.dto.UrlPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlChecksRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import hexlet.code.util.Utils;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static io.javalin.rendering.template.TemplateUtil.model;

public class UrlsController {
    public static void create(Context ctx) throws SQLException {
        var urlName = ctx.queryParamAsClass("name", String.class)
                .get()
                .trim()
                .toLowerCase();

        String normalizedUrlName;
        try {
            normalizedUrlName = Utils.normalizeUrlName(urlName);
        } catch (MalformedURLException | IllegalArgumentException | URISyntaxException e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("alertType", "danger");
            ctx.redirect(NamedRoutes.mainPath());
            return;
        }

        if (!UrlRepository.urlExists(normalizedUrlName)) {
            Url url = new Url(normalizedUrlName);
            UrlRepository.save(url);

            ctx.sessionAttribute("flash", "Страница успешно добавлена");
            ctx.sessionAttribute("alertType", "success");
            ctx.redirect(NamedRoutes.urlsPath());
        } else {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.sessionAttribute("alertType", "danger");
            ctx.redirect(NamedRoutes.urlsPath());
        }
    }

    public static void index(Context ctx) throws SQLException {
        List<Url> urls = UrlRepository.getEntities();
        Map<Integer, UrlCheck> allUrlsLastChecks = UrlChecksRepository.getAllUrlsLastChecks();
        UrlsPage page = new UrlsPage(urls, allUrlsLastChecks);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setAlertType(ctx.consumeSessionAttribute("alertType"));
        ctx.render("urls/index.jte", model("page", page));
    }

    public static void show(Context ctx) throws SQLException {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        Url url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Page not found"));
        List<UrlCheck> urlChecks = UrlChecksRepository.getAllChecksForUrl(id);

        UrlPage page = new UrlPage(url);
        page.setUrlChecks(urlChecks);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setAlertType(ctx.consumeSessionAttribute("alertType"));
        ctx.render("urls/show.jte", model("page", page));
    }
}
