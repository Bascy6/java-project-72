package hexlet.code.controller;

import hexlet.code.dto.LinkPage;
import hexlet.code.dto.UrlPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;

import java.net.URI;
import java.net.URL;
import java.sql.SQLException;

import java.util.Map;

import io.javalin.http.NotFoundResponse;
import static io.javalin.rendering.template.TemplateUtil.model;

public class UrlsController {

    public static void build(Context context) {
        var page = new LinkPage();
        page.setFlash(context.consumeSessionAttribute("flash"));
        page.setLink(context.consumeSessionAttribute("link"));
        context.render("build.jte", model("page", page));
    }


    public static void index(Context context) {
        UrlsPage page = new UrlsPage();
        try {
            page.setUrls(UrlRepository.getEntities());

            Map<Long, UrlCheck> lastChecks = UrlCheckRepository.findLastChecksForAllUrls();
            page.setUrlChecks(lastChecks);

            context.render("urls/index.jte", model("page", page));
        } catch (SQLException e) {
            page.setFlash("Ошибка в работе СУБД");
            context.render("urls/index.jte", model("page", page));
        }
    }

    public static void show(Context context) {
        UrlPage page = new UrlPage();
        try {
            Long id = context.pathParamAsClass("id", Long.class).get();
            page.setUrl(UrlRepository.findById(id)
                    .orElseThrow(() -> new NotFoundResponse("URL с id = " + id + " не найден")));
            page.setUrlChecks(UrlCheckRepository.getEntitiesByUrlId(id));

            page.setFlash(context.consumeSessionAttribute("flash"));
            context.render("urls/show.jte", model("page", page));
        } catch (NotFoundResponse e) {
            context.sessionAttribute("flash", "URL не найден" + e.getMessage());
            context.redirect(NamedRoutes.rootPath());
        } catch (SQLException e) {
            context.sessionAttribute("flash", "Ошибка в работе СУБД: " + e.getMessage());
            page.setFlash(context.consumeSessionAttribute("flash"));
            context.render("urls/show.jte", model("page", page));
        } catch (Exception e) {
            context.sessionAttribute("flash", "Произошла ошибка: " + e.getMessage());
            page.setFlash(context.consumeSessionAttribute("flash"));
            context.render("urls/show.jte", model("page", page));
        }
    }

    public static void create(Context context) {
        String link = context.formParamAsClass("url", String.class).get().toLowerCase().trim();
        context.sessionAttribute("link", link);

        try {
            URL linkUrl = new URI(link).toURL();
            link = linkUrl.getProtocol() + "://" + linkUrl.getHost()
                    + (linkUrl.getPort() != -1 ? ":" + linkUrl.getPort() : "");

            if (UrlRepository.findByName(link).isPresent()) {
                context.sessionAttribute("flash", "Ссылка уже содержится");
                context.redirect(NamedRoutes.rootPath());
                return;
            }
            UrlRepository.save(new Url(link));
            context.sessionAttribute("flash", "Ссылка успешно добавлена");
            context.consumeSessionAttribute("link");
            context.redirect(NamedRoutes.urlsPath());

        } catch (SQLException e) {
            context.sessionAttribute("flash", "Ошибка в работе СУБД");
            context.redirect(NamedRoutes.rootPath());
        } catch (Exception e) {
            context.sessionAttribute("flash", "Неверная ссылка");
            context.redirect(NamedRoutes.rootPath());
        }
    }

}
