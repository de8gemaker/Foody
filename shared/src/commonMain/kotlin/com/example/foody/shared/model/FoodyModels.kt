package com.example.foody.shared.model

import kotlin.math.roundToInt

data class MenuCategory(
    val id: String,
    val title: String,
    val subtitle: String,
    val emoji: String,
)

data class Dish(
    val id: String,
    val categoryId: String,
    val name: String,
    val description: String,
    val cuteNote: String,
    val price: Double,
    val imageUrl: String,
    val emoji: String,
    val badge: String? = null,
)

data class CartItem(
    val dish: Dish,
    val quantity: Int,
) {
    val subtotal: Double = dish.price * quantity
}

data class CategoryUiModel(
    val category: MenuCategory,
    val dishCount: Int,
    val isSelected: Boolean,
)

data class DishUiModel(
    val dish: Dish,
    val quantity: Int,
)

data class OrderSummary(
    val items: List<CartItem>,
    val totalPrice: Double,
    val confirmationTitle: String,
    val confirmationMessage: String,
)

data class FoodyUiState(
    val storeName: String,
    val storeDistance: String,
    val searchQuery: String,
    val categories: List<CategoryUiModel>,
    val selectedCategoryId: String,
    val visibleDishes: List<DishUiModel>,
    val cartItems: List<CartItem>,
    val cartDishCount: Int,
    val cartTotalPrice: Double,
    val isCartSheetVisible: Boolean,
    val isCheckoutSheetVisible: Boolean,
    val latestOrder: OrderSummary?,
)

internal fun Double.asPrice(): String {
    val hasDecimals = (this * 100).roundToInt() % 100 != 0
    return if (hasDecimals) "¥%.1f".format(this) else "¥%.0f".format(this)
}
