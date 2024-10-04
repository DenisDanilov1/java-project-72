@import hexlet.code.dto.UrlPage
@import hexlet.code.util.NamedRoutes
@import hexlet.code.util.FormattedTime

@param UrlPage page

@template.layout.page(
page = page,
content = @`
    <section>
        <div class="container-lg mt-5">
            <h1>Сайт: ${page.getUrl().getName()}</h1>
            <table class="table table-bordered table-hover mt-3">
                <tbody>
                <tr>
                    <td>ID</td>
                    <td>${page.getUrl().getId()}</td>
                </tr>
                <tr>
                    <td>Имя</td>
                    <td>${page.getUrl().getName()}</td>
                </tr>
                <tr>
                    <td>Дата создания</td>
                    <td>${FormattedTime.format(page.getUrl().getCreatedAt())}</td>
                </tr>
                </tbody>
            </table>
            <h2 class="mt-5">Проверки</h2>
            <div class="mx-auto p-4 py-md-5">
                <form method="post" action="${NamedRoutes.urlCheckPath(page.getUrl().getId())}">
                    <button type="submit" class="btn btn-primary">Запустить проверку</button>
                </form>
            </div>

            <table class="table table-bordered table-hover mt-3">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Код ответа</th>
                    <th>title</th>
                    <th>h1</th>
                    <th>description</th>
                    <th>Последняя проверка</th>
                </tr>
                </thead>
                @if(page.getUrlChecks() != null)
                    <tbody>
                    @for (var check : page.getUrlChecks())
                        <tr>
                            <td>${check.getId()}</td>
                            <td>${check.getStatusCode()}</td>
                            <td>${check.getTitle()}</td>
                            <td>${check.getH1()}</td>
                            <td>${check.getDescription()}</td>
                            <td>${FormattedTime.format(check.getCreatedAt())}</td>
                        </tr>
                    @endfor
                    </tbody>
                @endif
            </table>
        </div>
    </section>
`
)