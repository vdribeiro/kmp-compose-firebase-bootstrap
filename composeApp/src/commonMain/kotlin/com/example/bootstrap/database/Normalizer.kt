package com.example.bootstrap.database

/**
 * Convert string to lowercase non-accented characters.
 * E.g. "Äèëù Öéïêç Üîô à l'aÿ âû" returns "aeeu oeiec uio a l'ay au"
 */
internal expect fun String.normalizeToAscii(): String
