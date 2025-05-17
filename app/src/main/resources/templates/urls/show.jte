@import hexlet.code.model.UrlCheck
@import hexlet.code.util.NamedRoutes
@import hexlet.code.dto.UrlPage
@import hexlet.code.util.Utils

@param UrlPage page

@template.layout.page(
page = page,
content = @`

    <div class="container-lg mt-5">
        @if (page.getUrl() != null)

            <div class="text-center mb-4">
                <h2>Обзор</h2>
            </div>

            <div class="card mb-4">
                <div class="card-body">
                    <table class="table">
                        <tbody>
                        <tr>
                            <td><strong>ID</strong></td>
                            <td>${page.getUrl().getId()}</td>
                        </tr>
                        <tr>
                            <td><strong>Ссылка</strong></td>
                            <td>${page.getUrl().getName()}</td>
                        </tr>
                        <tr>
                            <td><strong>Добавлена</strong></td>
                            <td>${Utils.formatDate(page.getUrl().getCreatedAt())}</td>
                        </tr>
                        <tr>
                            <td><strong>Проверок</strong></td>
                            <td>${page.getUrlChecks().size()}</td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>

            <div class="text-center mb-4">
                <h3>Проверки</h3>
            </div>

            <div class="card">
                <div class="card-body">
                    @if (page.getUrlChecks() != null && !page.getUrlChecks().isEmpty())
                        <table class="table">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Код ответа</th>
                                <th>Заголовок</th>
                                <th>Оглавление</th>
                                <th>Описание</th>
                                <th>Дата проверки</th>
                            </tr>
                            </thead>
                            <tbody>
                            @for (UrlCheck urlCheck : page.getUrlChecks())
                                <tr>
                                    <td>${urlCheck.getId()}</td>
                                    <td>${urlCheck.getStatusCode()}</td>
                                    <td>${urlCheck.getTitle()}</td>
                                    <td>${urlCheck.getH1()}</td>
                                    <td>${urlCheck.getDescription()}</td>
                                    <td>${Utils.formatDate(urlCheck.getCreatedAt())}</td>
                                </tr>
                            @endfor
                            </tbody>
                        </table>
                    @else
                        <p class="text-muted">Нет проверок для данного URL.</p>
                    @endif
                    <form method="POST" action="${NamedRoutes.urlChecksPath(page.getUrl().getId())}" class="mt-3">
                        <button type="submit" class="btn btn-primary">Проверить</button>
                    </form>
                </div>
            </div>
        @endif
    </div>
`
)