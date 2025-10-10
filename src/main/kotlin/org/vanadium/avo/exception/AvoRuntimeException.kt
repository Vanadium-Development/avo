package org.vanadium.avo.exception

class AvoRuntimeException(override val message: String) : RuntimeException(message)