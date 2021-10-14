package mozerrr.action.data

import mozerrr.action.init.ActionData
import mozerrr.declaration.VariableDeclaration
import com.jetbrains.lang.dart.psi.DartClassDefinition

data class GenerationData(
    val actionData: ActionData,
    val dartClass: DartClassDefinition,
    val declarations: List<VariableDeclaration>
)
