package ru.itis.cloudphoto.command

import ru.itis.cloudphoto.model.Command
import ru.itis.cloudphoto.service.PhotoArchive

class ListCommand(
    private val archive: PhotoArchive
) {

    fun execute(command: Command.ListCmd) {
        val names = command.album?.let { album ->
            archive.requireAlbum(album)
            archive.listPhotos(album)
        } ?: archive.listAlbums()
        names.forEach(::println)
    }
}
