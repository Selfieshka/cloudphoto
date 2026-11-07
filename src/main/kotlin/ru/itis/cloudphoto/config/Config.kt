package ru.itis.cloudphoto.config

import ru.itis.cloudphoto.exception.CloudPhotoException
import ru.itis.cloudphoto.util.ENV_NOT_SET_MESSAGE

data class Config(
    val bucket: String,
    val accessKeyId: String,
    val secretAccessKey: String
)

fun loadConfig() = Config(
    bucket = requireEnv("BUCKET"),
    accessKeyId = requireEnv("AWS_ACCESS_KEY_ID"),
    secretAccessKey = requireEnv("AWS_SECRET_ACCESS_KEY")
)

private fun requireEnv(name: String): String =
    System.getenv(name)?.takeIf { it.isNotEmpty() } ?: throw CloudPhotoException(ENV_NOT_SET_MESSAGE)
