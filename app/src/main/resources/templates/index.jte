@import hexlet.code.dto.BasePage
@import hexlet.code.util.NamedRoutes

@param BasePage page

@template.layout.page(
page = page,
content = @`
    <head>
        <meta charset="utf-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.0/dist/css/bootstrap.min.css"
              rel="stylesheet"
              integrity="sha384-KyZXEAg3QhqLMpG8r+8fhAXLRk2vvoC2f3B09zVXn8CA5QIVfZOJ3BCsw2P0p/We"
              crossorigin="anonymous">
        <title>SEO-Analyzer</title>
    </head>
    <body>
    <div class="mx-auto p-4 py-md-5">
        <main>
            <h1>Анализатор страниц</h1>
            <form action="${NamedRoutes.urlsPath()}" method="post" class="rss-form text-body">
                <div>
                    <input id="url-input" autofocus type="text" required name="url"
                           aria-label="url" class="form-control w-100" placeholder="cсылка"
                           autocomplete="off">
                    <label for="url-input">Пример: https://ru.hexlet.io/</label>
                </div>
                <div>
                    <button type="submit">Проверить</button>
                </div>
            </form>
        </main>
    </div>
    </body>
`
)