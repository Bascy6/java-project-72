@import hexlet.code.model.Url
@import hexlet.code.util.NamedRoutes
@import hexlet.code.dto.UrlsPage
@import hexlet.code.util.Utils

@param UrlsPage page

@template.layout.page(
page = page,
content = @`
    <div class="container-lg mt-5">
        @if (page.getUrls() != null)
            <div class="text-left mb-4">
                <h2>Сайты</h2>
            </div>

            <div class="card">
                <div class="card-body">
                    <table class="table table-striped">
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Ссылка</th>
                            <th>Добавлена</th>
                            <th>Последняя проверка</th>
                            <th>Статус</th>
                        </tr>
                        </thead>
                        <tbody>
                        @for (Url url : page.getUrls())
                            <tr>
                                <td><a href="${NamedRoutes.urlPath(url.getId())}">${url.getId()}</a></td>
                                <td>${url.getName()}</td>
                                <td>${Utils.formatDate(url.getCreatedAt())}</td>
                                <td>
                                    @if (page.getUrlChecks().get(url.getId()) == null)
                                        <span class="text-muted">Проверок не было</span>
                                    @else
                                        ${Utils.formatDate(page.getUrlChecks().get(url.getId()).getCreatedAt())}
                                    @endif
                                </td>
                                <td>
                                    @if (page.getUrlChecks().get(url.getId()) == null)
                                        <span class="text-muted">Проверок не было</span>
                                    @else
                                        ${page.getUrlChecks().get(url.getId()).getStatusCode()}
                                    @endif
                                </td>
                            </tr>
                        @endfor
                        </tbody>
                    </table>
                </div>
            </div>
        @else
            <div class="alert alert-info" role="alert">
                Нет добавленных сайтов.
            </div>
        @endif
    </div>
`
)
