package core.util

fun String.flip(): String {
    return if (this == "+") "-" else "+"
}