package ru.itis.cloudphoto.model

sealed interface Command {

    data class ListCmd(val album: String?) : Command

    data class Upload(val album: String, val path: String) : Command

    data class Download(val album: String, val path: String) : Command

    data class Delete(val album: String, val photo: String?) : Command

    data object MkSite : Command
}
