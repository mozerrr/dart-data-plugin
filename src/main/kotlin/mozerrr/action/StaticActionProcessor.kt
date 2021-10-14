package mozerrr.action

import mozerrr.action.data.GenerationData
import mozerrr.action.data.PerformAction

interface StaticActionProcessor {

    fun processAction(generationData: GenerationData): PerformAction?

}
