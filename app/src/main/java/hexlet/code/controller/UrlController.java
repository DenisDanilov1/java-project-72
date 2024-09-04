package hexlet.code.controller;

import hexlet.code.dto.BasePage;
import hexlet.code.dto.UrlPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestException;
import org.jsoup.Jsoup;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Collections;

public class UrlController {
    public static void index(Context ctx) {
        var page = new BasePage();
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("type"));
        ctx.render("index.jte", Collections.singletonMap("page", page));
    }

    public static void create(Context ctx) throws SQLException {
        var url = ctx.formParamAsClass("url", String.class).getOrDefault(null);
        URL addedUrl = null;

        try {
            addedUrl = new URL(url);
        } catch (MalformedURLException e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("type", "danger");
            ctx.redirect("/");
        }

        if (addedUrl != null) {
            String protocol = addedUrl.getProtocol();
            String authority = addedUrl.getAuthority();
            var host = String.format("%s://%s", protocol, authority);
            var newUrl = new Url(host);

            if (UrlRepository.findByName(host).isEmpty()) {
                UrlRepository.save(newUrl);
                ctx.sessionAttribute("flash", "Страница успешно добавлена");
                ctx.sessionAttribute("type", "success");
            } else {
                ctx.sessionAttribute("flash", "Страница уже существует");
                ctx.sessionAttribute("type", "danger");
            }
            ctx.redirect("/urls");
        }
    }

    public static void showUrl(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Url not found"));

        var checks = UrlCheckRepository.getChecks(id);
        var page = new UrlPage(url, checks);

        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("type"));
        ctx.render("urls/show.jte", Collections.singletonMap("page", page));
    }

    public static void showUrls(Context ctx) throws SQLException {
        var urls = UrlRepository.getEntities();
        var checks = UrlCheckRepository.getAllChecks();
        var page = new UrlsPage(urls, checks);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("type"));
        ctx.render("urls/index.jte", Collections.singletonMap("page", page));

    }

    public static void check(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Id " + id + " not found"));
        String name = url.getName();

        try {
            HttpResponse<String> resp = Unirest.get(name).asString();
            var statusCode = resp.getStatus();
            var parse = Jsoup.parse(resp.getBody());
            var title = parse.title();
            var firstEl = parse.selectFirst("h1");
            var h1 = (firstEl == null ? null : firstEl.ownText());
            var forDesc = parse.selectFirst("meta[name=description]");
            var desc = (forDesc == null ? null : forDesc.attr("content"));
            var check = new UrlCheck(statusCode, title, h1, desc, id);

            UrlCheckRepository.save(check);
            ctx.sessionAttribute("flash", "Проверка прошла успешно");
            ctx.sessionAttribute("type", "success");
            ctx.redirect(NamedRoutes.urlPath(id));
        } catch (UnirestException e) {
            ctx.sessionAttribute("flash", "Не удалось проверить");
            ctx.sessionAttribute("type", "danger");
            ctx.redirect(NamedRoutes.urlPath(id));
        }
    }
}
