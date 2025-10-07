package org.vanadium.avo.exception

import java.lang.RuntimeException

class LexerException(override val message: String) : RuntimeException(message)