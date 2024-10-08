package hexlet.code.controller;

import hexlet.code.dto.UrlPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import io.javalin.http.NotFoundResponse;
import io.javalin.validation.ValidationException;

import static io.javalin.rendering.template.TemplateUtil.model;

public class UrlController {

    public static void create(Context ctx) throws SQLException {
        String path = ctx.formParam("url");
        try {
            var urlPath = new URI(path).toURL();
            var name = urlPath.getProtocol() + "://" + urlPath.getAuthority();
            var url = new Url(name);
            UrlRepository.save(url);
            ctx.redirect("/urls");
            ctx.sessionAttribute("flash", "Страница успешно добавлена");
            ctx.sessionAttribute("flashType", "success");

        } catch (ValidationException e) {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.sessionAttribute("flashType", "info");
            ctx.status(422);
            ctx.redirect("/");
        } catch (MalformedURLException | URISyntaxException | IllegalArgumentException e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flashType", "danger");
            ctx.status(422);
            ctx.redirect("/");
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
            ctx.sessionAttribute("flash", "Ошибка БД");
            ctx.sessionAttribute("flashType", "danger");
            ctx.redirect(NamedRoutes.urlsPath());
        }
    }
}
