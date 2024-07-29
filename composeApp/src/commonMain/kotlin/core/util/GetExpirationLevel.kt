package core.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun getExpirationLevel(dateString: String): Int {
    val formatters = listOf(
        DateTimeFormatter.ofPattern("d/M/yyyy"),
        DateTimeFormatter.ofPattern("d/MM/yyyy"),
        DateTimeFormatter.ofPattern("dd/M/yyyy"),
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
    )

    val expirationDate: LocalDate? = formatters.asSequence().mapNotNull { formatter ->
        try {
            LocalDate.parse(dateString, formatter)
        } catch (e: DateTimeParseException) {
            null
        }
    }.firstOrNull()

    return if (expirationDate != null) {
        val currentDate = LocalDate.now()
        when {
            expirationDate.isBefore(currentDate) -> 3
            expirationDate.isBefore(currentDate.plusMonths(1)) -> 2
            expirationDate.isAfter(currentDate.plusMonths(1)) -> 1
            else -> 0
        }
    } else {
        0
    }
}