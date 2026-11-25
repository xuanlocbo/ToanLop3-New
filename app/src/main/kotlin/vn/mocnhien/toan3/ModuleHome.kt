package vn.mocnhien.toan3

import android.widget.LinearLayout

/** Native home page migrated to Kotlin; identical layout, routes and shared state. */
internal class ModuleHome(context: ModuleContext, container: LinearLayout) : ModulePage(context, container) {
    fun show(lastId: String?) {
        val hero = ui.card(NativeUi.WHITE)
        ui.add(body, hero)
        val row = if (ui.sideBySide()) ui.row() else ui.column()
        ui.add(hero, row)

        val illustration = ui.column()
        if (ui.sideBySide()) ui.weight(row, illustration, 1f) else ui.add(row, illustration)
        ui.banner(illustration, 0, true, if (ui.compact()) 54 else 100)

        val welcome = ui.column()
        if (ui.sideBySide()) ui.weight(row, welcome, 1.35f) else ui.add(row, welcome)
        ui.label(welcome, "Vương quốc toán học", if (ui.compact()) 21 else 26, true)
        ui.caption(welcome, "Bài 26–44  •  Cùng Thỏ Hồng")

        val modules = AppModule.entries
        val columns = if (ui.widthDp() >= 400) 2 else 1
        val subtitles = listOf("Kiến thức và bài tập", "19 màn phiêu lưu",
            "Sao, huy hiệu và kho báu", "Kết quả và ôn câu sai")

        for (start in modules.indices step columns) {
            val cards = ui.row()
            ui.add(body, cards)
            for (i in start until minOf(modules.size, start + columns)) {
                val module = modules[i]
                val button = ui.button(module.label, module.color) { go(module.route) }
                button.setContentDescription(module.label)
                button.setTextSize(if (ui.compact()) 21f else 23f)
                button.setMinHeight(ui.dp(if (ui.compact()) 52f else 68f))
                if (ui.compact()) ui.weight(cards, button, 1f)
                else {
                    val card = ui.card(module.color)
                    ui.weight(cards, card, 1f)
                    ui.add(card, button)
                    ui.caption(card, subtitles[i])
                }
            }
        }

        val resume = ctx.gameResume()
        val last = lastId?.let { bank.questions[it] }
        if (resume >= 0 || last != null) {
            val resumeCard = ui.card(NativeUi.WHITE)
            ui.add(body, resumeCard)
            ui.label(resumeCard, "Tiếp tục", 19, true)
            if (last != null) {
                ui.action(resumeCard, "Học tiếp · Bài ${last.optInt("lesson")}", NativeUi.PALE) {
                    openQuestion(last, false)
                }
            }
            if (resume >= 0) {
                ui.action(resumeCard, "Chơi tiếp · Bài $resume", NativeUi.LAV) { openAdventure(resume) }
            }
        }
        ui.caption(body, "${completed()} / ${bank.questions.size} mục đã hoàn thành")
    }
}
