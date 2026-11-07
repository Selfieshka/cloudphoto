package ru.itis.cloudphoto.cli

import ru.itis.cloudphoto.exception.CloudPhotoException
import ru.itis.cloudphoto.model.Command
import ru.itis.cloudphoto.util.INVALID_ARGS_MESSAGE

private const val LIST = "list"
private const val UPLOAD = "upload"
private const val DOWNLOAD = "download"
private const val DELETE = "delete"
private const val MKSITE = "mksite"

private const val ALBUM = "--album"
private const val PATH = "--path"
private const val PHOTO = "--photo"
private const val CURRENT_DIR = "."

fun parseArgs(args: Array<String>): Command {
    val options = args.drop(1)
    return when (args.firstOrNull()) {
        LIST -> parseOptions(options, setOf(ALBUM)).let {
            Command.ListCmd(it[ALBUM]?.let(::checkAlbum))
        }
        UPLOAD -> parseOptions(options, setOf(ALBUM, PATH)).let {
            Command.Upload(it.album(), it[PATH] ?: CURRENT_DIR)
        }
        DOWNLOAD -> parseOptions(options, setOf(ALBUM, PATH)).let {
            Command.Download(it.album(), it[PATH] ?: CURRENT_DIR)
        }
        DELETE -> parseOptions(options, setOf(ALBUM, PHOTO)).let {
            Command.Delete(it.album(), it[PHOTO])
        }
        MKSITE -> parseOptions(options, emptySet()).let {
            Command.MkSite
        }
        else -> throw invalidArgs()
    }
}

private fun parseOptions(args: List<String>, allowed: Set<String>): Map<String, String> {
    if (args.size % 2 != 0) throw invalidArgs()
    val options = mutableMapOf<String, String>()
    args.chunked(2).forEach { (name, value) ->
        if (name !in allowed || value.isEmpty() || options.put(name, value) != null) throw invalidArgs()
    }
    return options
}

private fun Map<String, String>.album() =
    checkAlbum(this[ALBUM] ?: throw invalidArgs())

private fun checkAlbum(album: String) =
    album.takeIf { '/' !in it } ?: throw invalidArgs()

private fun invalidArgs() = CloudPhotoException(INVALID_ARGS_MESSAGE)
