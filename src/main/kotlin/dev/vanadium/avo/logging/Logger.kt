package dev.vanadium.avo.logging

interface Logger {
    fun error(message: Any)
    fun warn(message: Any)
    fun info(message: Any)
    fun debug(message: Any)
}

sealed class DefaultLogger {
    object NoLogger : DefaultLogger(), Logger {
        override fun error(message: Any) = Unit
        override fun warn(message: Any) = Unit
        override fun info(message: Any) = Unit
        override fun debug(message: Any) = Unit
    }

    object TinyLogLogger : DefaultLogger(), Logger {
        override fun error(message: Any) {
            org.tinylog.Logger.error(message)
        }

        override fun warn(message: Any) {
            org.tinylog.Logger.warn(message)
        }

        override fun info(message: Any) {
            org.tinylog.Logger.info(message)
        }

        override fun debug(message: Any) {
            org.tinylog.Logger.debug(message)
        }
    }
}