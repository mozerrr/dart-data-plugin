package mozerrr.action.my

import mozerrr.action.StaticActionProcessor
import mozerrr.action.data.GenerationData
import mozerrr.action.data.PerformAction
import mozerrr.declaration.isNullable
import mozerrr.declaration.variableName
import mozerrr.ext.psi.findChildrenByType
import mozerrr.templater.HashCodeTemplateParams
import mozerrr.templater.NamedVariableTemplateParamImpl
import mozerrr.templater.TemplateConstants
import mozerrr.templater.createHashCodeTemplate
import com.intellij.codeInsight.template.TemplateManager
import com.jetbrains.lang.dart.psi.DartClassDefinition
import com.jetbrains.lang.dart.psi.DartGetterDeclaration

class MyHashCodeAction {

    companion object : StaticActionProcessor {

        private fun createDeleteCall(dartClass: DartClassDefinition): (() -> Unit)? {
            val hashCode = dartClass.findChildrenByType<DartGetterDeclaration>()
                .filter { it.name == TemplateConstants.HASHCODE_NAME }
                .firstOrNull()
                ?: return null

            return { hashCode.delete() }
        }

        override fun processAction(generationData: GenerationData): PerformAction? {
            val (actionData, dartClass, declarations) = generationData

            val project = actionData.project

            val templateManager = TemplateManager.getInstance(project)

            val template = createHashCodeTemplate(
                templateManager = templateManager,
                params = HashCodeTemplateParams(
                    declarations.map {
                        NamedVariableTemplateParamImpl(
                            it.variableName,
                            isNullable = it.isNullable
                        )
                    }
                )
            )

            return PerformAction(
                createDeleteCall(dartClass),
                template
            )
        }
    }
}
