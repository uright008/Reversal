package net.optifine.cache

import org.apache.logging.log4j.LogManager
import java.util.regex.Pattern

object OptifineRegexCache {
	val cache: MutableMap<String, Pattern> = mutableMapOf()
	val illegalRegexes = mutableSetOf<String>()
	val logger = LogManager.getLogger()
	val neverRegex = Pattern.compile("$.")

	fun onResourcePackReload() {
		cache.clear()
	}

	private fun compilePattern(regex: String): Pattern {
		return try {
			Pattern.compile(regex)
		} catch (ex: Exception) {
			logger.error("Invalid regex $regex in optifine resource pack", ex)
			illegalRegexes.add(regex)
			neverRegex
		}
	}

	fun matchesRegex(str: String, regex: String): Boolean {
		val pattern = cache.computeIfAbsent(regex, ::compilePattern)
		return pattern.matcher(str).matches()
	}


}