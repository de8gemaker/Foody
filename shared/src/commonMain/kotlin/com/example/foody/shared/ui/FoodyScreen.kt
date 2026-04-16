package com.example.foody.shared.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.interaction.MutableInteractionSource
import com.example.foody.shared.model.CartItem
import com.example.foody.shared.model.Dish
import com.example.foody.shared.model.DishUiModel
import com.example.foody.shared.model.FoodyUiState
import com.example.foody.shared.model.asPrice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodyScreen(
    uiState: FoodyUiState,
    onSearchQueryChange: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onAddDish: (String) -> Unit,
    onRemoveDish: (String) -> Unit,
    onOpenCart: () -> Unit,
    onCheckout: () -> Unit,
    onDismissSheet: () -> Unit,
    onPlaceOrder: () -> Unit,
    onDismissLatestOrder: () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            CartBottomBar(
                dishCount = uiState.cartDishCount,
                totalPrice = uiState.cartTotalPrice,
                onOpenCart = onOpenCart,
                onCheckout = onCheckout,
            )
        },
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            CategorySidebar(
                uiState = uiState,
                onCategorySelected = onCategorySelected,
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item {
                    Column {
                        Spacer(modifier = Modifier.height(12.dp))
                        StoreHeader(
                            storeName = uiState.storeName,
                            storeDistance = uiState.storeDistance,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        SearchBar(
                            value = uiState.searchQuery,
                            onValueChange = onSearchQueryChange,
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        HeroCard()
                        uiState.latestOrder?.let { latestOrder ->
                            Spacer(modifier = Modifier.height(14.dp))
                            LatestOrderCard(
                                title = latestOrder.confirmationTitle,
                                message = latestOrder.confirmationMessage,
                                onDismiss = onDismissLatestOrder,
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "今天想让主厨做点什么",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                if (uiState.visibleDishes.isEmpty()) {
                    item {
                        EmptySearchState()
                    }
                } else {
                    items(
                        items = uiState.visibleDishes,
                        key = { it.dish.id },
                    ) { dishUi ->
                        DishCard(
                            item = dishUi,
                            onAddDish = { onAddDish(dishUi.dish.id) },
                            onRemoveDish = { onRemoveDish(dishUi.dish.id) },
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(88.dp))
                }
            }
        }
    }

    if (uiState.isCartSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismissSheet,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            CartSheet(
                items = uiState.cartItems,
                totalPrice = uiState.cartTotalPrice,
                onDismiss = onDismissSheet,
                onCheckout = onCheckout,
                onAddDish = onAddDish,
                onRemoveDish = onRemoveDish,
            )
        }
    }

    if (uiState.isCheckoutSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismissSheet,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            CheckoutSheet(
                items = uiState.cartItems,
                totalPrice = uiState.cartTotalPrice,
                onDismiss = onDismissSheet,
                onPlaceOrder = onPlaceOrder,
            )
        }
    }
}

@Composable
private fun StoreHeader(
    storeName: String,
    storeDistance: String,
) {
    Column {
        Text(
            text = "咔快",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = storeName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            AssistChip(
                onClick = {},
                label = {
                    Text(
                        text = storeDistance,
                        fontSize = 12.sp,
                    )
                },
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "主厨已上线，今天也想把你喂得香香的。",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("搜一搜想吃的菜菜") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
            )
        },
        shape = RoundedCornerShape(20.dp),
        singleLine = true,
    )
}

@Composable
private fun HeroCard() {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.92f),
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "今日哄开心套餐",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "认真做饭，温柔投喂，今晚的幸福感已经悄悄预热好啦。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "🍳",
                    fontSize = 32.sp,
                )
            }
        }
    }
}

@Composable
private fun CategorySidebar(
    uiState: FoodyUiState,
    onCategorySelected: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .width(94.dp)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
            .padding(vertical = 10.dp, horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        uiState.categories.forEach { categoryModel ->
            val shape = RoundedCornerShape(18.dp)
            val interactionSource = remember(categoryModel.category.id) { MutableInteractionSource() }
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                    ) { onCategorySelected(categoryModel.category.id) },
                shape = shape,
                color = if (categoryModel.isSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
                tonalElevation = if (categoryModel.isSelected) 1.dp else 0.dp,
                shadowElevation = if (categoryModel.isSelected) 2.dp else 0.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(text = categoryModel.category.emoji, fontSize = 20.sp)
                    Text(
                        text = categoryModel.category.title,
                        fontSize = 13.sp,
                        fontWeight = if (categoryModel.isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (categoryModel.isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                    Text(
                        text = "${categoryModel.dishCount}份",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun DishCard(
    item: DishUiModel,
    onAddDish: () -> Unit,
    onRemoveDish: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            DishIllustration(dish = item.dish)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                item.dish.badge?.let { badge ->
                    Text(
                        text = badge,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.18f))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Text(
                    text = item.dish.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = item.dish.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = item.dish.cuteNote,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = item.dish.price.asPrice(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    QuantityStepper(
                        quantity = item.quantity,
                        onAdd = onAddDish,
                        onRemove = onRemoveDish,
                    )
                }
            }
        }
    }
}

@Composable
private fun DishIllustration(dish: Dish) {
    Box(
        modifier = Modifier
            .size(92.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.tertiary),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = dish.emoji,
                fontSize = 34.sp,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "香香出锅",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
            )
        }
    }
}

@Composable
private fun QuantityStepper(
    quantity: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
) {
    if (quantity <= 0) {
        Button(
            onClick = onAdd,
            shape = CircleShape,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 0.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("点它")
        }
    } else {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            RoundIconButton(
                icon = Icons.Default.Remove,
                onClick = onRemove,
            )
            Text(
                text = quantity.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            RoundIconButton(
                icon = Icons.Default.Add,
                onClick = onAdd,
            )
        }
    }
}

@Composable
private fun RoundIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .size(32.dp)
            .clickable(onClick = onClick),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
        contentColor = MaterialTheme.colorScheme.primary,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun EmptySearchState() {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(text = "🥺", fontSize = 36.sp)
            Text(
                text = "这会儿还没搜到合适的小菜",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "试试换个关键词，或者去别的分类看看，说不定惊喜正躲在那里。",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun CartBottomBar(
    dishCount: Int,
    totalPrice: Double,
    onOpenCart: () -> Unit,
    onCheckout: () -> Unit,
) {
    Surface(
        tonalElevation = 8.dp,
        shadowElevation = 10.dp,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.navigationBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = dishCount > 0, onClick = onOpenCart)
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (dishCount > 0) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = if (dishCount > 0) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Column {
                    Text(
                        text = if (dishCount > 0) "${dishCount}份想吃的已经在购物车里啦" else "先挑几道想吃的菜菜吧",
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = if (dishCount > 0) totalPrice.asPrice() else "还没开始点单",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Button(
                onClick = onCheckout,
                enabled = dishCount > 0,
                shape = RoundedCornerShape(18.dp),
            ) {
                Text("去下单")
            }
        }
    }
}

@Composable
private fun CartSheet(
    items: List<CartItem>,
    totalPrice: Double,
    onDismiss: () -> Unit,
    onCheckout: () -> Unit,
    onAddDish: (String) -> Unit,
    onRemoveDish: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        SheetTitle(
            title = "购物车里有点香",
            subtitle = "再确认一下今晚想被投喂的快乐菜单。",
            onDismiss = onDismiss,
        )
        items.forEach { item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.dish.name,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = item.subtotal.asPrice(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                QuantityStepper(
                    quantity = item.quantity,
                    onAdd = { onAddDish(item.dish.id) },
                    onRemove = { onRemoveDish(item.dish.id) },
                )
            }
            HorizontalDivider()
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("合计", fontWeight = FontWeight.Bold)
            Text(totalPrice.asPrice(), fontWeight = FontWeight.Bold)
        }
        Button(
            onClick = onCheckout,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
        ) {
            Text("继续去下单")
        }
    }
}

@Composable
private fun CheckoutSheet(
    items: List<CartItem>,
    totalPrice: Double,
    onDismiss: () -> Unit,
    onPlaceOrder: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        SheetTitle(
            title = "确认下单",
            subtitle = "这一单会直接变成男朋友的做饭任务清单。",
            onDismiss = onDismiss,
        )
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)),
            shape = RoundedCornerShape(22.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = "今晚点单备注",
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "少一点匆忙，多一点认真翻炒；记得把好吃的第一口先端给女朋友。",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        items.forEach { item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("${item.dish.name} x${item.quantity}")
                Text(item.subtotal.asPrice())
            }
        }
        HorizontalDivider()
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("总计", fontWeight = FontWeight.Bold)
            Text(totalPrice.asPrice(), fontWeight = FontWeight.Bold)
        }
        Button(
            onClick = onPlaceOrder,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
        ) {
            Text("确认下单，开始做饭")
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun LatestOrderCard(
    title: String,
    message: String,
    onDismiss: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.16f),
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.22f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
private fun SheetTitle(
    title: String,
    subtitle: String,
    onDismiss: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        TextButton(onClick = onDismiss) {
            Text("稍后")
        }
    }
}
