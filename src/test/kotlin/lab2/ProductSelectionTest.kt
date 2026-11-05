package lab2

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ProductSelectionTest {
    // Original starter tests are preserved.
    @Test
    fun selectsAvailableProductsInRecommendationOrder() {
        val keyboard = Product(1, "Keyboard", true)
        val mouse = Product(2, "Mouse", false)
        val monitor = Product(3, "Monitor", true)
        val webcam = Product(4, "Webcam", true)
        val products = listOf(keyboard, mouse, monitor, webcam)
        val result = selectProducts(
            products,
            listOf(3L, 2L, 3L, 99L, 1L, 4L),
            listOf(1L)
        )

        assertEquals(listOf(monitor, webcam), result)
    }

    @Test
    fun returnsEmptyListWhenThereAreNoRecommendations() {
        val products = listOf(Product(1, "Keyboard", true))

        val result = selectProducts(products, emptyList(), emptyList())

        assertEquals(emptyList<Product>(), result)
    }

    @Test
    fun followsRecommendationsEvenWhenCatalogOrderIsDifferent() {
        val first = Product(1, "First", true)
        val second = Product(2, "Second", true)
        val third = Product(3, "Third", true)

        assertEquals(
            listOf(third, first, second),
            selectProducts(listOf(second, first, third), listOf(3L, 1L, 2L), emptyList())
        )
    }

    @Test
    fun removesRepeatedRecommendationsAndRepeatedExclusions() {
        val a = Product(1, "A", true)
        val b = Product(2, "B", true)
        val c = Product(3, "C", true)

        assertEquals(
            listOf(c, a),
            selectProducts(
                listOf(a, b, c),
                listOf(3L, 3L, 2L, 1L, 3L, 1L),
                listOf(2L, 2L, 999L, 999L)
            )
        )
    }

    @Test
    fun ignoresUnavailableExcludedAndUnknownProducts() {
        val available = Product(1, "Available", true)
        val unavailable = Product(2, "Unavailable", false)
        val excluded = Product(3, "Excluded", true)

        assertEquals(
            listOf(available),
            selectProducts(
                listOf(available, unavailable, excluded),
                listOf(Long.MAX_VALUE, 2L, 3L, 1L, Long.MIN_VALUE),
                listOf(3L, 404L)
            )
        )
    }

    @Test
    fun returnsEmptyForEmptyCatalogEvenWithRecommendations() {
        assertEquals(
            emptyList<Product>(),
            selectProducts(emptyList(), listOf(1L, 2L), listOf(2L))
        )
    }

    @Test
    fun includesAllValidRecommendationsWhenExclusionsAreEmpty() {
        val a = Product(11, "A", true)
        val b = Product(12, "B", true)

        assertEquals(listOf(b, a), selectProducts(listOf(a, b), listOf(12L, 11L), emptyList()))
    }

    @Test
    fun returnsEmptyWhenEveryRecommendationIsFiltered() {
        val unavailable = Product(1, "Unavailable", false)
        val excluded = Product(2, "Excluded", true)

        assertEquals(
            emptyList<Product>(),
            selectProducts(listOf(unavailable, excluded), listOf(1L, 2L, 999L), listOf(2L))
        )
    }

    @Test
    fun preservesInputsAndKeepsRepeatedCallsIndependent() {
        val a = Product(1, "A", true)
        val b = Product(2, "B", true)
        val products = mutableListOf(b, a)
        val recommendations = mutableListOf(1L, 2L, 1L)
        val exclusions = mutableListOf(2L)
        val originalProducts = products.toList()
        val originalRecommendations = recommendations.toList()
        val originalExclusions = exclusions.toList()

        val first = selectProducts(products, recommendations, exclusions)
        val second = selectProducts(products, recommendations, emptyList())
        val third = selectProducts(products, recommendations, exclusions)

        assertEquals(listOf(a), first)
        assertEquals(listOf(a, b), second)
        assertEquals(listOf(a), third)
        assertEquals(originalProducts, products)
        assertEquals(originalRecommendations, recommendations)
        assertEquals(originalExclusions, exclusions)
    }
}
