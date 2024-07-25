package core.util.ext

fun String.flip(): String {
    return if (this == "+") "-" else "+"
}