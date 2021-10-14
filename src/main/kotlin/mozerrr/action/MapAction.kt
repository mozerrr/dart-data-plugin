package mozerrr.action

import mozerrr.action.init.ActionData
import mozerrr.action.data.GenerationData
import mozerrr.action.data.PerformAction
import mozerrr.action.utils.createMapDeleteCall
import mozerrr.action.utils.selectFieldsWithDialog
import mozerrr.configuration.ConfigurationDataManager
import mozerrr.declaration.fullTypeName
import mozerrr.declaration.isNullable
import mozerrr.declaration.variableName
import mozerrr.ext.psi.extractClassName
import mozerrr.templater.AliasedVariableTemplateParam
import mozerrr.templater.AliasedVariableTemplateParamImpl
import mozerrr.templater.MapTemplateParams
import mozerrr.templater.createMapTemplate
import com.intellij.codeInsight.template.TemplateManager
import com.intellij.openapi.actionSystem.AnActionEvent
import com.jetbrains.lang.dart.psi.DartClassDefinition

class MapAction : BaseAnAction() {

    override fun processAction(
        event: AnActionEvent,
        actionData: ActionData,
        dartClass: DartClassDefinition
    ): PerformAction? {
        val declarations = selectFieldsWithDialog(actionData.project, dartClass) ?: return null

        return Companion.processAction(
            GenerationData(actionData, dartClass, declarations)
        )
    }

    companion object : StaticActionProcessor {

        override fun processAction(generationData: GenerationData): PerformAction {
            val (actionData, dartClass, declarations) = generationData

            val (project, _, _, _) = actionData

            val variableNames: List<AliasedVariableTemplateParam> = declarations
                .map {
                    AliasedVariableTemplateParamImpl(
                        variableName = it.variableName,
                        type = it.fullTypeName
                            ?: throw RuntimeException("No type is available - this variable should not be assignable from constructor"),
                        publicVariableName = it.publicVariableName,
                        isNullable = it.isNullable
                    )
                }

            val templateManager = TemplateManager.getInstance(project)
            val configuration = ConfigurationDataManager.retrieveData(project)
            val dartClassName = dartClass.extractClassName()

            val template = createMapTemplate(
                templateManager,
                MapTemplateParams(
                    className = dartClassName,
                    variables = variableNames,
                    useNewKeyword = configuration.useNewKeyword,
                    addKeyMapper = configuration.addKeyMapperForMap,
                    noImplicitCasts = configuration.noImplicitCasts
                )
            )

            val deleteCall = createMapDeleteCall(dartClass)

            return PerformAction(
                deleteCall,
                template
            )
        }

    }
}
