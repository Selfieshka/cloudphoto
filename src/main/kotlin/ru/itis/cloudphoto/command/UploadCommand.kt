package ru.itis.cloudphoto.command

import ru.itis.cloudphoto.exception.CloudPhotoException
import ru.itis.cloudphoto.model.Command
import ru.itis.cloudphoto.service.PhotoArchive
import ru.itis.cloudphoto.service.StorageLayout
import java.nio.file.Files
import java.nio.file.Path

private const val DIR_NOT_AVAILABLE_MESSAGE = "Каталог недоступен: "
private const val NO_PHOTOS_MESSAGE = "В каталоге нет фотографий: "

class UploadCommand(
    private val archive: PhotoArchive
) {

    fun execute(command: Command.Upload) {
        val dir = Path.of(command.path)
        if (!Files.isDirectory(dir) || !Files.isReadable(dir)) throw CloudPhotoException(DIR_NOT_AVAILABLE_MESSAGE + command.path)
        val photos = findPhotos(dir)
        if (photos.isEmpty()) throw CloudPhotoException(NO_PHOTOS_MESSAGE + command.path)
        archive.createAlbum(command.album)
        photos.forEach { archive.uploadPhoto(command.album, it) }
    }

    private fun findPhotos(dir: Path): List<Path> =
        Files.list(dir).use { files ->
            files.filter { Files.isRegularFile(it) && StorageLayout.isPhotoName(it.fileName.toString()) }.toList()
        }
}
