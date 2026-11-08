package ru.itis.cloudphoto.service

object StorageLayout {

    const val ALBUMS_PREFIX = "albums/"
    const val ALBUM_MARKER = ".album"

    fun albumPrefix(album: String) = "$ALBUMS_PREFIX$album/"

    fun markerKey(album: String) = albumPrefix(album) + ALBUM_MARKER

    fun photoKey(album: String, photo: String) = albumPrefix(album) + photo

    fun albumFromPrefix(prefix: String) = prefix.removePrefix(ALBUMS_PREFIX).removeSuffix("/")

    fun photoFromKey(album: String, key: String) = key.removePrefix(albumPrefix(album))

    fun isPhotoName(name: String) =
        name.endsWith(".jpg", ignoreCase = true) || name.endsWith(".jpeg", ignoreCase = true)
}
