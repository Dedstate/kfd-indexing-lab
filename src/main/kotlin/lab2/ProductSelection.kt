package lab2

/**
 * Returns available, non-excluded products in the order of their first recommendation.
 * The inputs are not modified.
 */
fun selectProducts(
    products: List<Product>,
    recommendedIds: List<Long>,
    excludedIds: List<Long>
): List<Product> = IndexedProductCatalog(products).select(
    recommendedIds = recommendedIds,
    excludedIds = excludedIds,
    limit = Int.MAX_VALUE
)
