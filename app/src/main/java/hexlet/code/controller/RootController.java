package hexlet.code.controller;

import hexlet.code.dto.BasePage;
import io.javalin.http.Context;

import static io.javalin.rendering.template.TemplateUtil.model;

public class RootController {
    public static void index(Context ctx) {
        String flashType = ctx.consumeSessionAttribute("flashType");
        String flash = ctx.consumeSessionAttribute("flash");
        var page = flash == null ? new BasePage() : new BasePage(flashType, flash);
        ctx.render("index.jte", model("page", page));
    }
}
