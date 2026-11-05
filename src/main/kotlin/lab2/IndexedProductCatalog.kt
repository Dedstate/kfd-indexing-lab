package lab2

/**
 * Builds an ID index once and reuses it for independent selection requests.
 * To use a different catalog, create a new IndexedProductCatalog instance.
 */
class IndexedProductCatalog(products: List<Product>) {
    private val productsById: Map<Long, Product> = products.associateBy { it.id }

    /**
     * Returns up to [limit] eligible products, in recommendation order.
     * Stops reading recommendations as soon as [limit] products have been selected.
     */
    fun select(
        recommendedIds: List<Long>,
        excludedIds: List<Long>,
        limit: Int
    ): List<Product> {
        require(limit >= 0) { "limit must be non-negative" }
        if (limit == 0) return emptyList()

        val excluded = excludedIds.toHashSet()
        val selectedIds = HashSet<Long>()
        val result = ArrayList<Product>()

        for (id in recommendedIds) {
            if (id in excluded || id in selectedIds) continue
            val product = productsById[id] ?: continue
            if (!product.available) continue

            selectedIds.add(id)
            result.add(product)
            if (result.size == limit) break
        }

        return result
    }
}
