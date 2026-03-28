package br.com.phs.fullexample

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
data class DynamicFormState(
    val formId: String,
    val statusMessage: String,
    val isLoading: Boolean,
    val isAuthenticated: Boolean,
)

@OptIn(ExperimentalJsExport::class)
@JsExport
data class DynamicComponentState(
    val id: String,
    val kind: String,
    val text: String,
    val label: String,
    val placeholder: String,
    val value: String,
    val checked: Boolean,
    val error: String,
    val actionId: String,
    val isVisible: Boolean,
    val isEnabled: Boolean,
    val isTextInput: Boolean,
    val isCheckbox: Boolean,
    val isPassword: Boolean,
    val isButton: Boolean,
    val isStatus: Boolean,
    val isSpace: Boolean,
    val maxLength: Int,
    val align: String,
    val spaceWidth: Int,
    val spaceHeight: Int,
    val marginLeft: Int,
    val marginTop: Int,
    val marginRight: Int,
    val marginBottom: Int,
)
