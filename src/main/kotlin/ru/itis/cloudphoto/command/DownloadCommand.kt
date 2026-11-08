package ru.itis.cloudphoto.command

import ru.itis.cloudphoto.exception.CloudPhotoException
import ru.itis.cloudphoto.model.Command
import ru.itis.cloudphoto.service.PhotoArchive
import java.nio.file.Files
import java.nio.file.Path

private const val DIR_NOT_WRITABLE_MESSAGE = "Каталог недоступен для сохранения файлов: "

class DownloadCommand(
    private val archive: PhotoArchive
) {

    fun execute(command: Command.Download) {
        archive.requireAlbum(command.album)
        val dir = Path.of(command.path)
        if (!Files.isDirectory(dir) || !Files.isWritable(dir)) throw CloudPhotoException(DIR_NOT_WRITABLE_MESSAGE + command.path)
        archive.listPhotos(command.album).forEach { photo ->
            archive.downloadPhoto(command.album, photo, dir.resolve(photo))
        }
    }
}
