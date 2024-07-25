package core.model

object Strings {
    private val en = mapOf(
        "All items" to "All items",
        "Add New Item" to "Add New Item",
        "Name" to "Name",
        "Quantity" to "Quantity",
        "Whole price" to "Whole Price",
        "Selling price" to "Selling Price",
        "Expiration date" to "Expiration Date",
        "Cancel" to "Cancel",
        "Save" to "Save",
        "Profit" to "Profit",
        "total whole price" to "Total Whole Price",
        "total selling price" to "Total Selling Price",
        "total profit         " to "Total Profit",
        "Storify" to "Storify",
        "Search.." to "Search..",
        "Day" to "Day",
        "Month" to "Month",
        "Year" to "Year",
        "+ add item" to "+ add item",
        "Select Image" to "Select Image",
    )

    private val ar = mapOf(
        "All items" to "جميع العناصر",
        "Add New Item" to "إضافة عنصر جديد",
        "Name" to "الاسم",
        "Quantity" to "الكمية",
        "Whole price" to "السعر الكامل",
        "Selling price" to "سعر البيع",
        "Expiration date" to "تاريخ انتهاء الصلاحية",
        "Cancel" to "إلغاء",
        "Save" to "حفظ",
        "Profit" to "الربح",
        "total whole price" to "إجمالي السعر الكامل",
        "total selling price" to "إجمالي سعر البيع",
        "total profit         " to "إجمالي الربح",
        "Storify" to "ستوريفاي",
        "Search.." to "بحث..",
        "+ add item" to "+ إضافة عنصر",
        "Day" to "يوم",
        "Month" to "شهر",
        "Year" to "سنة",
        "Select Image" to "اختر صورة",
    )


    private var currentLanguage = en

    fun setLanguage(language: String) {
        currentLanguage = when (language) {
            "ar" -> ar
            else -> en
        }
    }


    val String.localized: String
        get() = getString(this)


    fun getString(key: String): String {
        return currentLanguage[key] ?: key
    }
}