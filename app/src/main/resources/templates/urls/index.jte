@import hexlet.code.dto.UrlsPage
@import hexlet.code.util.NamedRoutes
@import hexlet.code.util.FormattedTime

@param UrlsPage page

@template.layout.page(
page = page,
content = @`
    <h1>Все сайты</h1>
    <table class="table table-striped">
        <thead>
        <tr>
            <th>ID</th>
            <th>Имя</th>
            <th>Последняя проверка</th>
            <th>Код ответа</th>
        </tr>
        </thead>
            <tbody>
            @for (var url : page.getUrls())
                <tr>
                    <td>${url.getId()}</td>

                    <td>
                        <a href="${NamedRoutes.urlPath(url.getId())}">
                            ${url.getName()}
                        </a>
                    </td>

                    <td>
                        <a>${page.getLastUrlChecks().get(url.getId()) == null ? "" :
                             FormattedTime.format(page.getLastUrlChecks().get(url.getId()).getCreatedAt())}
                        </a>
                    </td>

                    <td>
                        <a>${page.getLastUrlChecks().get(url.getId()) == null ? "" :
                             String.valueOf(page.getLastUrlChecks().get(url.getId()).getStatusCode())}
                        </a>
                    </td>
                </tr>
            @endfor
            </tbody>
    </table>
`
)