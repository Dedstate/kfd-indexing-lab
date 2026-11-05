package lab2

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class IndexedProductCatalogTest {
    @Test
    fun zeroLimitDoesNotReadRecommendations() {
        val catalog = IndexedProductCatalog(listOf(Product(1, "A", true)))
        val recommendations = CountingRecommendations(listOf(1L, 1L, 1L))

        assertEquals(emptyList<Product>(), catalog.select(recommendations, emptyList(), 0))
        assertEquals(0, recommendations.readCount)
    }

    @Test
    fun stopsReadingRecommendationsOnceLimitIsReached() {
        val a = Product(1, "A", true)
        val b = Product(2, "B", true)
        val catalog = IndexedProductCatalog(listOf(a, b))
        val recommendations = CountingRecommendations(listOf(999L, 1L, 1L, 2L, 42L, 2L, 1L))

        assertEquals(listOf(a), catalog.select(recommendations, emptyList(), 1))
        assertEquals(2, recommendations.readCount)
        assertEquals(7, recommendations.size)
    }

    @Test
    fun returnsAllEligibleProductsWhenLimitIsGreaterThanTheirCount() {
        val a = Product(1, "A", true)
        val b = Product(2, "B", false)
        val c = Product(3, "C", true)
        val catalog = IndexedProductCatalog(listOf(a, b, c))

        assertEquals(
            listOf(c, a),
            catalog.select(listOf(2L, 3L, 3L, 404L, 1L), emptyList(), 10)
        )
    }

    @Test
    fun usesRequestLocalExclusionsAndSelectionState() {
        val a = Product(1, "A", true)
        val b = Product(2, "B", true)
        val catalog = IndexedProductCatalog(listOf(a, b))

        assertEquals(listOf(a), catalog.select(listOf(1L, 2L), listOf(2L), 10))
        assertEquals(listOf(b, a), catalog.select(listOf(2L, 1L, 2L), emptyList(), 10))
        assertEquals(listOf(a), catalog.select(listOf(1L, 1L), emptyList(), 10))
    }

    @Test
    fun creatingIndexForNewCatalogUsesNewProductValues() {
        val oldProduct = Product(1, "Old", true)
        val newProduct = Product(1, "New", true)
        var catalog = IndexedProductCatalog(listOf(oldProduct))

        assertEquals(listOf(oldProduct), catalog.select(listOf(1L), emptyList(), 1))
        catalog = IndexedProductCatalog(listOf(newProduct))
        assertEquals(listOf(newProduct), catalog.select(listOf(1L), emptyList(), 1))
    }

    @Test
    fun cachedIndexDoesNotDependOnSubsequentChangesToInputCatalog() {
        val a = Product(1, "A", true)
        val source = mutableListOf(a)
        val catalog = IndexedProductCatalog(source)
        source.clear()

        assertEquals(listOf(a), catalog.select(listOf(1L), emptyList(), 1))
    }

    /** Counts actually accessed elements, not the list's declared size. */
    private class CountingRecommendations(private val ids: List<Long>) : AbstractList<Long>() {
        var readCount: Int = 0
            private set

        override val size: Int get() = ids.size

        override fun get(index: Int): Long {
            readCount++
            return ids[index]
        }
    }
}
