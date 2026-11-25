package vn.mocnhien.toan3

/** Same Java-facing API, route names, enum order and colors as the approved Java shell. */
enum class AppModule(
    @JvmField val label: String,
    @JvmField val route: String,
    @JvmField val color: Int
) {
    LEARNING("Học bài", "learn", 0xffffdce9.toInt()),
    GAME("Chơi game", "adventure", 0xffe7ddfa.toInt()),
    REWARDS("Phần thưởng", "awards", 0xffffe5ad.toInt()),
    PROGRESS("Tiến độ", "progress", 0xffd6f0e0.toInt());

    companion object {
        @JvmStatic
        fun known(route: String?): Boolean = when (route) {
            "home", "learn", "lesson", "list", "question",
            "adventure", "game", "gate", "awards",
            "progress", "review", "settings" -> true
            else -> false
        }

        @JvmStatic
        fun owner(route: String?, reviewMode: Boolean): AppModule? = when (route) {
            "learn", "lesson", "list" -> LEARNING
            "question" -> if (reviewMode) PROGRESS else LEARNING
            "adventure", "game", "gate" -> GAME
            "awards" -> REWARDS
            "progress", "review" -> PROGRESS
            else -> null
        }

        @JvmStatic
        fun back(route: String?, reviewMode: Boolean): String = when (route) {
            "lesson" -> LEARNING.route
            "list" -> "lesson"
            "question" -> if (reviewMode) "review" else "list"
            "game" -> GAME.route
            "gate" -> "game"
            "review" -> PROGRESS.route
            else -> "home"
        }
    }
}
