package mozerrr.action.utils

import mozerrr.templater.TemplateConstants
import mozerrr.utils.mergeCalls
import com.jetbrains.lang.dart.psi.DartClass

fun createMapDeleteCall(
    dartClass: DartClass
): (() -> Unit)? {

    val toMapMethod = dartClass.findMethodByName(TemplateConstants.TO_MAP_METHOD_NAME)
    val fromMapMethod = dartClass.findNamedConstructor(TemplateConstants.FROM_MAP_METHOD_NAME)

    return listOfNotNull(
        toMapMethod,
        fromMapMethod
    )
        .map { { it.delete() } }
        .mergeCalls()
}
