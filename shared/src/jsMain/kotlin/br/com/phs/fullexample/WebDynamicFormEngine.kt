package br.com.phs.fullexample

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
class WebDynamicFormEngine {
    private val delegate = DynamicFormEngine(
        source = BundledSampleLoginFormSource,
        actionHandler = SampleLoginActionHandler,
    )

    fun getState(): DynamicFormState = delegate.getState()

    fun getComponentsCount(): Int = delegate.getComponentsCount()

    fun getComponentAt(index: Int): DynamicComponentState = delegate.getComponentAt(index)

    fun updateTextField(componentId: String, value: String) {
        delegate.updateTextField(componentId, value)
    }

    fun updateCheckboxField(componentId: String, checked: Boolean) {
        delegate.updateCheckboxField(componentId, checked)
    }

    fun triggerAction(actionId: String) {
        delegate.triggerAction(actionId)
    }
}
