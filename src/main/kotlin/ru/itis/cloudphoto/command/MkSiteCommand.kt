package ru.itis.cloudphoto.command

import ru.itis.cloudphoto.service.PhotoArchive
import ru.itis.cloudphoto.site.albumPage
import ru.itis.cloudphoto.site.albumPageName
import ru.itis.cloudphoto.site.errorPage
import ru.itis.cloudphoto.site.indexPage

private const val INDEX_DOCUMENT = "index.html"
private const val ERROR_DOCUMENT = "error.html"

class MkSiteCommand(
    private val archive: PhotoArchive
) {

    fun execute() {
        archive.enablePublicRead()
        archive.configureWebsite(INDEX_DOCUMENT, ERROR_DOCUMENT)
        val albums = archive.listAlbums()
        albums.forEachIndexed { index, album ->
            archive.putHtml(albumPageName(index + 1), albumPage(album, archive.listPhotos(album)))
        }
        archive.putHtml(INDEX_DOCUMENT, indexPage(albums))
        archive.putHtml(ERROR_DOCUMENT, errorPage())
    }
}
