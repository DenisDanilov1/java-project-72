@import hexlet.code.dto.UrlPage
@import hexlet.code.util.NamedRoutes
@import hexlet.code.util.FormattedTime

@param UrlPage page

@template.layout.page(
page = page,
content = @`
    <div class="mb-3">
        <h1>Название сайта: ${page.getUrl().getName()}</h1>
    </div>
    <div class="mb-3">
        ID : ${page.getUrl().getId()}
    </div>
    <div class="mb-3">
        Имя : ${page.getUrl().getName()}
    </div>
    <div class="mb-3">
        Дата создания : ${FormattedTime.format(page.getUrl().getCreatedAt())}
    </div>

    <h2>Проверки</h2>
    <div class="mx-auto p-4 py-md-5">
        <form method="post" action="${NamedRoutes.urlCheckPath(page.getUrl().getId())}">
            <button type="submit" class="btn btn-primary">Запустить проверку</button>
        </form>
    </div>

    <table class="table table-striped">
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
        @if(page.getChecks() != null)
            <tbody>
            @for (var check : page.getChecks())
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
`
)