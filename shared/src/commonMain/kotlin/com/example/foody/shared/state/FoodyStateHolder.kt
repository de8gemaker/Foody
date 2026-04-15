package com.example.foody.shared.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.foody.shared.data.FakeMenuRepository
import com.example.foody.shared.model.CartItem
import com.example.foody.shared.model.CategoryUiModel
import com.example.foody.shared.model.Dish
import com.example.foody.shared.model.DishUiModel
import com.example.foody.shared.model.FoodyUiState
import com.example.foody.shared.model.OrderSummary

@Stable
class FoodyStateHolder(
    private val repository: FakeMenuRepository = FakeMenuRepository(),
) {
    private val categories = repository.categories()
    private val dishes = repository.dishes()
    private val quantities = mutableStateMapOf<String, Int>()

    var searchQuery by mutableStateOf("")
        private set

    var selectedCategoryId by mutableStateOf(categories.first().id)
        private set

    var isCartSheetVisible by mutableStateOf(false)
        private set

    var isCheckoutSheetVisible by mutableStateOf(false)
        private set

    var latestOrder by mutableStateOf<OrderSummary?>(null)
        private set

    val uiState: FoodyUiState
        get() {
            val query = searchQuery.trim()
            val filteredByQuery = dishes.filter { dish ->
                query.isBlank() || dish.matches(query)
            }
            val visibleDishes = filteredByQuery
                .filter { it.categoryId == selectedCategoryId }
                .map { dish -> DishUiModel(dish, quantities[dish.id] ?: 0) }

            val categoryModels = categories.map { category ->
                CategoryUiModel(
                    category = category,
                    dishCount = filteredByQuery.count { it.categoryId == category.id },
                    isSelected = category.id == selectedCategoryId,
                )
            }

            val cartItems = dishes.mapNotNull { dish ->
                val quantity = quantities[dish.id] ?: 0
                if (quantity > 0) CartItem(dish, quantity) else null
            }

            return FoodyUiState(
                storeName = "今晚吃点什么呀",
                storeDistance = "距离厨房 1m",
                searchQuery = searchQuery,
                categories = categoryModels,
                selectedCategoryId = selectedCategoryId,
                visibleDishes = visibleDishes,
                cartItems = cartItems,
                cartDishCount = cartItems.sumOf { it.quantity },
                cartTotalPrice = cartItems.sumOf { it.subtotal },
                isCartSheetVisible = isCartSheetVisible,
                isCheckoutSheetVisible = isCheckoutSheetVisible,
                latestOrder = latestOrder,
            )
        }

    fun onSearchQueryChange(query: String) {
        searchQuery = query
        if (uiState.visibleDishes.isNotEmpty()) {
            return
        }
        repository.categories()
            .firstOrNull { category ->
                dishes.any { it.categoryId == category.id && it.matches(query.trim()) }
            }
            ?.let { selectedCategoryId = it.id }
    }

    fun onCategorySelected(categoryId: String) {
        selectedCategoryId = categoryId
    }

    fun addDish(dishId: String) {
        val current = quantities[dishId] ?: 0
        quantities[dishId] = current + 1
    }

    fun removeDish(dishId: String) {
        val current = quantities[dishId] ?: return
        if (current <= 1) {
            quantities.remove(dishId)
        } else {
            quantities[dishId] = current - 1
        }
    }

    fun openCartSheet() {
        if (uiState.cartItems.isEmpty()) return
        isCheckoutSheetVisible = false
        isCartSheetVisible = true
    }

    fun openCheckoutSheet() {
        if (uiState.cartItems.isEmpty()) return
        isCartSheetVisible = false
        isCheckoutSheetVisible = true
    }

    fun dismissSheets() {
        isCartSheetVisible = false
        isCheckoutSheetVisible = false
    }

    fun placeOrder() {
        val items = uiState.cartItems
        if (items.isEmpty()) return

        latestOrder = OrderSummary(
            items = items,
            totalPrice = items.sumOf { it.subtotal },
            confirmationTitle = "下单成功，男朋友准备开火啦",
            confirmationMessage = "这份菜单已经悄悄加入今晚待做清单，记得顺手夸一夸主厨认真营业。",
        )
        quantities.clear()
        dismissSheets()
    }

    fun clearLatestOrder() {
        latestOrder = null
    }

    private fun Dish.matches(query: String): Boolean {
        if (query.isBlank()) return true
        return name.contains(query, ignoreCase = true) ||
            description.contains(query, ignoreCase = true) ||
            cuteNote.contains(query, ignoreCase = true)
    }
}

@Composable
@Suppress("FunctionName")
fun rememberFoodyStateHolder(
    repository: FakeMenuRepository = FakeMenuRepository(),
): FoodyStateHolder = remember(repository) {
    FoodyStateHolder(repository)
}
