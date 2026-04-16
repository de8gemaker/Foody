package com.example.foody.shared.app

import androidx.compose.runtime.Composable
import com.example.foody.shared.state.rememberFoodyStateHolder
import com.example.foody.shared.ui.FoodyScreen
import com.example.foody.shared.ui.FoodyTheme

@Composable
fun FoodyApp() {
    val stateHolder = rememberFoodyStateHolder()

    FoodyTheme {
        FoodyScreen(
            uiState = stateHolder.uiState,
            onSearchQueryChange = stateHolder::onSearchQueryChange,
            onCategorySelected = stateHolder::onCategorySelected,
            onAddDish = stateHolder::addDish,
            onRemoveDish = stateHolder::removeDish,
            onOpenCart = stateHolder::openCartSheet,
            onCheckout = stateHolder::openCheckoutSheet,
            onDismissSheet = stateHolder::dismissSheets,
            onPlaceOrder = stateHolder::placeOrder,
            onDismissLatestOrder = stateHolder::clearLatestOrder,
        )
    }
}
