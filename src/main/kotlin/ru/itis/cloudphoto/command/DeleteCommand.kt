package ru.itis.cloudphoto.command

import ru.itis.cloudphoto.exception.CloudPhotoException
import ru.itis.cloudphoto.model.Command
import ru.itis.cloudphoto.service.PhotoArchive

private const val PHOTO_NOT_FOUND_MESSAGE = "В альбоме нет фотографии с именем "

class DeleteCommand(
    private val archive: PhotoArchive
) {

    fun execute(command: Command.Delete) {
        archive.requireAlbum(command.album)
        val photo = command.photo
        if (photo == null) {
            archive.deleteAlbum(command.album)
            return
        }
        if (!archive.photoExists(command.album, photo)) throw CloudPhotoException(PHOTO_NOT_FOUND_MESSAGE + photo)
        archive.deletePhoto(command.album, photo)
    }
}
