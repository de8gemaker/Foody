package com.example.foody.shared.state

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FoodyStateHolderTest {
    @Test
    fun search_switches_to_first_matching_category() {
        val stateHolder = FoodyStateHolder()

        stateHolder.onSearchQueryChange("布丁")

        assertEquals("dessert", stateHolder.uiState.selectedCategoryId)
        assertEquals(1, stateHolder.uiState.visibleDishes.size)
        assertEquals("焦糖布丁啵啵杯", stateHolder.uiState.visibleDishes.first().dish.name)
    }

    @Test
    fun cart_total_updates_with_add_and_remove() {
        val stateHolder = FoodyStateHolder()

        stateHolder.addDish("braised-pork")
        stateHolder.addDish("braised-pork")
        stateHolder.addDish("corn-ribs-soup")
        stateHolder.removeDish("braised-pork")

        assertEquals(2, stateHolder.uiState.cartDishCount)
        assertEquals(54.0, stateHolder.uiState.cartTotalPrice)
        assertTrue(stateHolder.uiState.cartItems.any { it.dish.id == "braised-pork" && it.quantity == 1 })
        assertTrue(stateHolder.uiState.cartItems.any { it.dish.id == "corn-ribs-soup" && it.quantity == 1 })
    }
}
