package vn.mocnhien.toan3

/** The four candy stations use the existing authoritative puzzle and storage APIs. */
internal class CandyStationSession(
    private val store: ProgressStore,
    lesson: Int
) {
    enum class Outcome { CHANGED, NO_CHANGE, NOT_READY, SAVE_FAILED, COMPLETE }

    @JvmField val mission: TrailMission
    private var state: IntArray
    private var completed = false

    init {
        require(supports(lesson))
        mission = TrailMission.of(lesson)
        state = mission.read(store.trailDraft(lesson))
        completed = store.trailComplete(lesson)
    }

    fun snapshot(): IntArray = state.copyOf()
    fun remainingOrValue(): Int = mission.result(state)
    fun trace(): String = mission.trace(state)
    fun canComplete(): Boolean = mission.solved(state)
    fun move(action: Int): Outcome {
        if (completed) return Outcome.NO_CHANGE
        val candidate = mission.move(state, action)
        if (candidate.contentEquals(state)) return Outcome.NO_CHANGE
        return save(candidate)
    }
    fun reset(): Outcome {
        if (completed) return Outcome.NO_CHANGE
        // Always commit a valid reset, including an unchanged initial value.
        return save(mission.initial())
    }
    private fun save(candidate: IntArray): Outcome {
        if (!store.saveTrailDraft(mission.lesson, mission.encode(candidate))) return Outcome.SAVE_FAILED
        state = candidate.copyOf()
        return Outcome.CHANGED
    }
    fun check(): Outcome {
        if (completed) return Outcome.NO_CHANGE
        if (!mission.solved(state)) return Outcome.NOT_READY
        if (!store.completeTrail(mission.lesson, mission.encode(state))) return Outcome.SAVE_FAILED
        completed = true
        return Outcome.COMPLETE
    }
    companion object {
        @JvmStatic fun supports(lesson: Int): Boolean = lesson in 26..29
        @JvmStatic fun containerName(lesson: Int): String =
            when (lesson) { 26 -> "Thuyền"; 29 -> "Rương"; else -> "Nhóm" }
        @JvmStatic fun objective(lesson: Int): String = when (lesson) {
            26 -> "Chia hàng cho thuyền và giữ phần dư"
            27 -> "Vận hành máy thu nhỏ hạt giống"
            28 -> "Ghép cầu bằng hai bước tính"
            29 -> "Chia đá quý vào các rương"
            else -> ""
        }
    }
}
