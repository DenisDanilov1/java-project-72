package hexlet.code.controller;


import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.sql.SQLException;


public class CheckController {
    public static void check(Context ctx) throws SQLException {

        Long urlId = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlRepository.find(urlId)
                .orElseThrow(() -> new NotFoundResponse("url не найден"));
        if (url == null) {
            throw new NotFoundResponse("Url not found");
        }

        try {
            HttpResponse<String> response = Unirest.get(url.getName()).asString();
            int statusCode = response.getStatus();
            Document document = Jsoup.parse(response.getBody());
            String title = document.title();
            var h1temp = document.selectFirst("h1");
            String h1 = h1temp == null ? "" : h1temp.text();
            var descriptionTemp = document.selectFirst("meta[name=description]");
            String description = descriptionTemp == null ? "" : descriptionTemp.attr("content");
            var urlCheck = new UrlCheck(statusCode, title, h1, description, urlId);
            Unirest.shutDown();
            UrlCheckRepository.save(urlCheck);

            ctx.sessionAttribute("flash", "Страница успешно проверена");
            ctx.sessionAttribute("flashType", "success");

        } catch (Exception e) {
            ctx.sessionAttribute("flash", "Неверный URL");
            ctx.sessionAttribute("flashType", "danger");
        }
        ctx.redirect(NamedRoutes.urlPath(urlId));
    }
}