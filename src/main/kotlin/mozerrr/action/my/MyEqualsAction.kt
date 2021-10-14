package mozerrr.action.my

import mozerrr.action.StaticActionProcessor
import mozerrr.action.data.GenerationData
import mozerrr.action.data.PerformAction
import mozerrr.declaration.isNullable
import mozerrr.declaration.variableName
import mozerrr.ext.psi.extractClassName
import mozerrr.ext.psi.findMethodsByName
import mozerrr.templater.EqualsTemplateParams
import mozerrr.templater.NamedVariableTemplateParamImpl
import mozerrr.templater.TemplateConstants
import mozerrr.templater.createEqualsTemplate
import com.intellij.codeInsight.template.TemplateManager
import com.jetbrains.lang.dart.psi.DartClassDefinition

class MyEqualsAction {

    companion object : StaticActionProcessor {

        private fun createDeleteCall(dartClass: DartClassDefinition): (() -> Unit)? {
            val equals = dartClass.findMethodsByName(TemplateConstants.EQUALS_OPERATOR_METHOD_NAME)
                .firstOrNull()
                ?: return null

            return { equals.delete() }
        }

        override fun processAction(generationData: GenerationData): PerformAction? {
            val (actionData, dartClass, declarations) = generationData

            val project = actionData.project

            val templateManager = TemplateManager.getInstance(project)
            val dartClassName = dartClass.extractClassName()

            val template = createEqualsTemplate(
                templateManager,
                EqualsTemplateParams(
                    className = dartClassName,
                    variables = declarations.map {
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
