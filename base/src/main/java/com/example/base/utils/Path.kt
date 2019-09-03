package com.example.base.utils

import android.net.Uri

class Path(
    stringPath: String
) {

    companion object {

        @JvmStatic
        val main = Path("/")

    }

    val pathComponents: List<String>
    val parameters: Map<String, List<String>>

    init {
        val uri = Uri.parse("https://example.com/$stringPath")
        pathComponents = uri.pathSegments
        parameters = uri.queryParameterNames.associate { key -> key to uri.getQueryParameters(key)!! }
    }

    fun getParameter(key: String): String? {
        return parameters[key]?.firstOrNull()
    }

    fun matches(appPath: Path): Boolean {
        if (pathComponents.isEmpty()) {
            return appPath.pathComponents.isEmpty()
        }
        if (appPath.pathComponents.isEmpty()) {
            return false
        }

        var pathI = 0
        var wildcardConsumed = false
        for (appPathI in appPath.pathComponents.indices) {
            if (pathI == pathComponents.size) {
                return false
            }

            val pathComponent = pathComponents[pathI]
            val appPathComponent = appPath.pathComponents[appPathI]

            if (pathComponent == appPathComponent) {
                pathI++
                continue
            }

            if (pathComponent == "*") {
                var nextPathComponent: String? = null
                if (pathI + 1 < pathComponents.size) {
                    nextPathComponent = pathComponents[pathI + 1]
                }
                if (nextPathComponent != null && nextPathComponent == appPathComponent) {
                    pathI += 2
                    continue
                } else {
                    wildcardConsumed = true
                    continue
                }
            }

            return false
        }

        return pathI == pathComponents.size || pathI == pathComponents.size - 1 && pathComponents[pathI] == "*" && wildcardConsumed
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Path

        if (pathComponents != other.pathComponents) return false
        if (parameters != other.parameters) return false

        return true
    }

    override fun hashCode(): Int {
        var result = pathComponents.hashCode()
        result = 31 * result + parameters.hashCode()
        return result
    }

    override fun toString(): String {
        val paramsString = if (parameters.isEmpty()) {
            "/"
        } else {
            parameters.map { "${it.key}=${it.value}" }.joinToString(separator = "&", prefix = "?")
        }

        return if (pathComponents.isEmpty()) {
            paramsString
        } else {
            pathComponents.joinToString(separator = "/", prefix = "/", postfix = paramsString)
        }
    }

}
