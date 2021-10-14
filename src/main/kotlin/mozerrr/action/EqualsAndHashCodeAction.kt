package mozerrr.action

import mozerrr.action.init.ActionData
import mozerrr.action.data.GenerationData
import mozerrr.action.data.PerformAction
import mozerrr.action.data.combineAll
import mozerrr.action.my.MyEqualsAction
import mozerrr.action.my.MyHashCodeAction
import mozerrr.action.utils.*
import com.intellij.openapi.actionSystem.AnActionEvent
import com.jetbrains.lang.dart.psi.DartClassDefinition

class EqualsAndHashCodeAction : BaseAnAction() {

    override fun processAction(
        event: AnActionEvent,
        actionData: ActionData,
        dartClass: DartClassDefinition
    ): PerformAction? {
        val project = actionData.project
        val declarations = selectFieldsWithDialog(project, dartClass)
            ?: return null

        val generationData = GenerationData(actionData, dartClass, declarations)

        val processActions = listOf(
            MyEqualsAction.Companion,
            MyHashCodeAction.Companion
        )
            .map { it.processAction(generationData) }

        return listOf(
            *processActions.toTypedArray()
        )
            .combineAll()
    }
}
