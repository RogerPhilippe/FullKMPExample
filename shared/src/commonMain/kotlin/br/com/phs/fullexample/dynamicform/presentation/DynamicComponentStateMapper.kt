package br.com.phs.fullexample.dynamicform.presentation

import br.com.phs.fullexample.DynamicComponentState
import br.com.phs.fullexample.dynamicform.application.DynamicFormSession
import br.com.phs.fullexample.dynamicform.domain.ButtonComponentDefinition
import br.com.phs.fullexample.dynamicform.domain.ComponentAlign
import br.com.phs.fullexample.dynamicform.domain.ComponentMargin
import br.com.phs.fullexample.dynamicform.domain.DynamicComponentDefinition
import br.com.phs.fullexample.dynamicform.domain.FieldComponentDefinition
import br.com.phs.fullexample.dynamicform.domain.FieldComponentType
import br.com.phs.fullexample.dynamicform.domain.HeadingComponentDefinition
import br.com.phs.fullexample.dynamicform.domain.SpaceComponentDefinition
import br.com.phs.fullexample.dynamicform.domain.StatusComponentDefinition
import br.com.phs.fullexample.dynamicform.domain.TextComponentDefinition

internal object DynamicComponentStateMapper {
    fun map(
        component: DynamicComponentDefinition,
        session: DynamicFormSession,
        actionsEnabled: Boolean,
    ): DynamicComponentState {
        return when (component) {
            is HeadingComponentDefinition -> createState(
                id = component.id,
                kind = "heading",
                text = component.text,
                align = component.align,
                margin = component.margin,
            )

            is TextComponentDefinition -> createState(
                id = component.id,
                kind = "text",
                text = component.text,
                align = component.align,
                margin = component.margin,
            )

            is StatusComponentDefinition -> createState(
                id = component.id,
                kind = "status",
                text = session.statusMessage,
                isVisible = session.statusMessage.isNotBlank(),
                isStatus = true,
                align = component.align,
                margin = component.margin,
            )

            is SpaceComponentDefinition -> createState(
                id = component.id,
                kind = "space",
                isEnabled = false,
                isSpace = true,
                align = component.align,
                margin = component.margin,
                spaceWidth = component.width,
                spaceHeight = component.height,
            )

            is FieldComponentDefinition -> createState(
                id = component.id,
                kind = if (component.isCheckbox) "checkbox" else "field",
                label = component.label,
                placeholder = component.placeholder,
                value = session.textValues[component.id].orEmpty(),
                checked = session.checkboxValues[component.id] == true,
                error = session.fieldErrors[component.id].orEmpty(),
                isTextInput = component.isTextInput,
                isCheckbox = component.isCheckbox,
                isPassword = component.type == FieldComponentType.PASSWORD,
                maxLength = component.maxLength ?: 0,
                align = component.align,
                margin = component.margin,
            )

            is ButtonComponentDefinition -> createState(
                id = component.id,
                kind = "button",
                text = component.label,
                label = component.label,
                actionId = component.action,
                isEnabled = actionsEnabled,
                isButton = true,
                align = component.align,
                margin = component.margin,
            )
        }
    }

    private fun createState(
        id: String,
        kind: String,
        align: ComponentAlign,
        margin: ComponentMargin,
        text: String = "",
        label: String = "",
        placeholder: String = "",
        value: String = "",
        checked: Boolean = false,
        error: String = "",
        actionId: String = "",
        isVisible: Boolean = true,
        isEnabled: Boolean = true,
        isTextInput: Boolean = false,
        isCheckbox: Boolean = false,
        isPassword: Boolean = false,
        isButton: Boolean = false,
        isStatus: Boolean = false,
        isSpace: Boolean = false,
        maxLength: Int = 0,
        spaceWidth: Int = 0,
        spaceHeight: Int = 0,
    ): DynamicComponentState {
        return DynamicComponentState(
            id = id,
            kind = kind,
            text = text,
            label = label,
            placeholder = placeholder,
            value = value,
            checked = checked,
            error = error,
            actionId = actionId,
            isVisible = isVisible,
            isEnabled = isEnabled,
            isTextInput = isTextInput,
            isCheckbox = isCheckbox,
            isPassword = isPassword,
            isButton = isButton,
            isStatus = isStatus,
            isSpace = isSpace,
            maxLength = maxLength,
            align = align.name.lowercase(),
            spaceWidth = spaceWidth,
            spaceHeight = spaceHeight,
            marginLeft = margin.left,
            marginTop = margin.top,
            marginRight = margin.right,
            marginBottom = margin.bottom,
        )
    }
}
