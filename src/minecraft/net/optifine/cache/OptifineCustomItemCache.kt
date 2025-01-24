package net.optifine.cache

import net.minecraft.item.ItemStack
import net.optifine.CustomItemProperties
import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference

object OptifineCustomItemCache {

	val referenceQueue = ReferenceQueue<ItemStack>()

	class CacheKeyReference(val cacheKey: CacheKey, itemStack: ItemStack) :
		WeakReference<ItemStack>(itemStack, referenceQueue)

	class CacheKey(itemStack: ItemStack, val type: Int) {
		val hashCode = System.identityHashCode(itemStack) * 31 + type
		val ref = CacheKeyReference(this, itemStack)

		override fun equals(other: Any?): Boolean {
			if (other === this) return true
			if (other !is CacheKey) return false
			return ref.get() === other.ref.get() && type == other.type
		}

		override fun hashCode(): Int {
			return hashCode
		}

		fun isPresent(): Boolean {
			return ref.get() != null
		}
	}

	data class CacheStats(
		var cacheHits: Int = 0,
		var cacheMisses: Int = 0,
		var insertions: Int = 0,
		var size: Int = 0,
		var removals: Int = 0,
	)

	private var map = mutableMapOf<CacheKey, CustomItemProperties?>()
	private val cacheSizeHistory = Histogram<CacheStats>(1000)
	private var cacheStats = CacheStats()

	fun onResourcePackReload() {
		map.clear()
	}

	fun onTick() {
		var removeCount = 0
		while (true) {
			val ref = referenceQueue.poll() as CacheKeyReference? ?: break
			removeCount++
			map.remove(ref.cacheKey)
		}
		cacheStats.size = map.size
		cacheStats.removals = removeCount
		cacheSizeHistory.append(cacheStats)
		cacheStats = CacheStats()
	}

	@JvmStatic
	fun retrieveCacheHit(
		itemStack: ItemStack,
		type: Int,
	): CustomItemProperties? {
		val key = CacheKey(itemStack, type)
		if (!map.containsKey(key)) {
			cacheStats.cacheMisses++
			return null;
		}
		cacheStats.cacheHits++
		return map[key]
	}

	@JvmStatic
	fun storeCustomItemProperties(itemStack: ItemStack, type: Int, cip: CustomItemProperties) {
		map[CacheKey(itemStack, type)] = cip
		cacheStats.insertions++
	}

	@JvmStatic
	fun storeNoCustomItemProperties(itemStack: ItemStack, type: Int) {
		map[CacheKey(itemStack, type)] = null
		cacheStats.insertions++
	}
}