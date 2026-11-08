package ru.itis.cloudphoto.site

import java.net.URLEncoder

fun escapeHtml(value: String): String =
    value.replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#39;")

fun encodeUrlSegment(value: String): String =
    URLEncoder.encode(value, Charsets.UTF_8).replace("+", "%20")
