package ru.itis.cloudphoto.site

import ru.itis.cloudphoto.service.StorageLayout

fun albumPageName(number: Int) = "album$number.html"

fun indexPage(albums: List<String>): String {
    val content = if (albums.isEmpty()) {
        "    <p>Фотоархив пустой.</p>"
    } else {
        albums.mapIndexed { index, album ->
            "        <li><a href=\"${albumPageName(index + 1)}\">${escapeHtml(album)}</a></li>"
        }.joinToString("\n", prefix = "    <ul>\n", postfix = "\n    </ul>")
    }
    return """
        |<!doctype html>
        |<html>
        |    <head>
        |        <title>Фотоархив</title>
        |    </head>
        |<body>
        |    <h1>Фотоархив</h1>
        |$content
        |</body>
        |
    """.trimMargin()
}

fun albumPage(album: String, photos: List<String>): String =
    if (photos.isEmpty()) emptyAlbumPage(escapeHtml(album)) else filledAlbumPage(album, photos)

fun errorPage(): String = """
    |<!doctype html>
    |<html>
    |    <head>
    |        <title>Фотоархив</title>
    |    </head>
    |<body>
    |    <h1>Ошибка</h1>
    |    <p>Ошибка при доступе к фотоархиву. Вернитесь на <a href="index.html">главную страницу</a> фотоархива.</p>
    |</body>
    |</html>
    |
""".trimMargin()

private fun emptyAlbumPage(title: String): String = """
    |<!doctype html>
    |<html>
    |    <head>
    |        <title>Фотоальбом "$title"</title>
    |    </head>
    |    <body>
    |        <h1>Фотоальбом "$title"</h1>
    |        <p>Фотоальбом пустой.</p>
    |        <p>Вернуться на <a href="index.html">главную страницу</a> фотоархива</p>
    |    </body>
    |</html>
    |
""".trimMargin()

private fun filledAlbumPage(album: String, photos: List<String>): String {
    val title = escapeHtml(album)
    val images = photos.joinToString("\n") { photo ->
        "            <img src=\"${photoUrl(album, photo)}\" data-title=\"${escapeHtml(photo)}\">"
    }
    return """
        |<!doctype html>
        |<html>
        |    <head>
        |        <title>Фотоальбом "$title"</title>
        |        <link rel="stylesheet" type="text/css" href="https://cdnjs.cloudflare.com/ajax/libs/galleria/1.6.1/themes/classic/galleria.classic.min.css" />
        |        <style>
        |            .galleria{ width: 960px; height: 540px; background: #000 }
        |        </style>
        |        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
        |        <script src="https://cdnjs.cloudflare.com/ajax/libs/galleria/1.6.1/galleria.min.js"></script>
        |        <script src="https://cdnjs.cloudflare.com/ajax/libs/galleria/1.6.1/themes/classic/galleria.classic.min.js"></script>
        |    </head>
        |    <body>
        |        <h1>Фотоальбом "$title"</h1>
        |        <div class="galleria">
        |$images
        |        </div>
        |        <p>Вернуться на <a href="index.html">главную страницу</a> фотоархива</p>
        |        <script>
        |            (function() {
        |                Galleria.run('.galleria');
        |            }());
        |        </script>
        |    </body>
        |</html>
        |
    """.trimMargin()
}

private fun photoUrl(album: String, photo: String) =
    StorageLayout.ALBUMS_PREFIX + encodeUrlSegment(album) + "/" + encodeUrlSegment(photo)
