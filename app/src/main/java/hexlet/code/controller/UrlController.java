package hexlet.code.controller;

import hexlet.code.dto.UrlPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;

import java.net.URI;
import java.net.URL;
import java.sql.SQLException;
import io.javalin.http.NotFoundResponse;

import static io.javalin.rendering.template.TemplateUtil.model;

public class UrlController {

    public static void create(Context ctx) throws SQLException {

        var inputUrl = ctx.formParam("url");
        URL parsedUrl;
        try {
            var uri = new URI(inputUrl);
            parsedUrl = uri.toURL();
        } catch (Exception e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flashType", "danger");
            ctx.redirect(NamedRoutes.rootPath());
            return;
        }

        var name = parsedUrl.getProtocol() + "://" + parsedUrl.getAuthority();
        var urlObj = new Url(name);
        if (UrlRepository.findByName(name).isPresent()) {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.sessionAttribute("flashType", "info");
            ctx.redirect(NamedRoutes.rootPath());
        } else {
            UrlRepository.save(urlObj);
            ctx.sessionAttribute("flash", "Страница успешно добавлена");
            ctx.sessionAttribute("flashType", "success");
            ctx.redirect(NamedRoutes.urlsPath());
        }
    }

    public static void index(Context ctx) throws SQLException {

        var urls = UrlRepository.getEntities();
        var page = new UrlsPage(urls, UrlCheckRepository.getLastChecks());
        page.setFlashType(ctx.consumeSessionAttribute("flashType"));
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        ctx.render("urls/index.jte", model("page", page));
    }

    public static void show(Context ctx) {
        try {
            var id = ctx.pathParamAsClass("id", Long.class).get();
            var url = UrlRepository.find(id)
                    .orElseThrow(() -> new NotFoundResponse("Url " + id + " not found"));
            var urlChecks = UrlCheckRepository.getChecks(id);
            var page = new UrlPage(url, urlChecks);
            page.setFlashType(ctx.consumeSessionAttribute("flashType"));
            page.setFlash(ctx.consumeSessionAttribute("flash"));
            ctx.render("urls/show.jte", model("page", page));

        } catch (SQLException e) {
            ctx.sessionAttribute("flashType", "danger");
            ctx.sessionAttribute("flash", "Ошибка БД");
            ctx.redirect(NamedRoutes.urlsPath());
        }
    }
}
