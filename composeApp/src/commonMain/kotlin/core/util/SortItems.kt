package core.util

import core.model.Item

fun List<Item>.sortItems(it: String): List<Item> {
    val items = this

    val firstChar = it.substring(0, 1)
    val restOfString = it.substring(1)

    val list = when (restOfString) {
        "Name" -> items.sortedBy { it.name }
        "Quantity" -> items.sortedBy { it.quantity }
        "Whole price" -> items.sortedBy { it.wholePrice }
        "Selling price" -> items.sortedBy { it.sellingPrice }
        "Profit" -> items.sortedBy { it.profit }
        "Expiration date" -> items.sortedBy { it.expirationDate }
        else -> items
    }

    return if (firstChar == "-") list.reversed() else list
}