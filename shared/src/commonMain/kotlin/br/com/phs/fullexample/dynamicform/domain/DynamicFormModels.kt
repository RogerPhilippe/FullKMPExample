package br.com.phs.fullexample.dynamicform.domain

internal data class DynamicFormDefinition(
    val id: String,
    val components: List<DynamicComponentDefinition>,
)

internal sealed interface DynamicComponentDefinition {
    val id: String
}

internal data class HeadingComponentDefinition(
    override val id: String,
    val text: String,
    val align: ComponentAlign,
    val margin: ComponentMargin,
) : DynamicComponentDefinition

internal data class TextComponentDefinition(
    override val id: String,
    val text: String,
    val align: ComponentAlign,
    val margin: ComponentMargin,
) : DynamicComponentDefinition

internal data class StatusComponentDefinition(
    override val id: String,
    val align: ComponentAlign,
    val margin: ComponentMargin,
) : DynamicComponentDefinition

internal data class SpaceComponentDefinition(
    override val id: String,
    val width: Int,
    val height: Int,
    val align: ComponentAlign,
    val margin: ComponentMargin,
) : DynamicComponentDefinition

internal data class FieldComponentDefinition(
    override val id: String,
    val type: FieldComponentType,
    val label: String,
    val placeholder: String,
    val required: Boolean,
    val minLength: Int?,
    val maxLength: Int?,
    val pattern: String?,
    val defaultChecked: Boolean,
    val align: ComponentAlign,
    val margin: ComponentMargin,
) : DynamicComponentDefinition {
    val isTextInput: Boolean = type != FieldComponentType.CHECKBOX
    val isCheckbox: Boolean = type == FieldComponentType.CHECKBOX
}

internal data class ButtonComponentDefinition(
    override val id: String,
    val action: String,
    val label: String,
    val align: ComponentAlign,
    val margin: ComponentMargin,
) : DynamicComponentDefinition

internal enum class ComponentAlign {
    LEFT,
    CENTER,
    RIGHT,
    FILL_WIDTH,
}

internal data class ComponentMargin(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int,
)

internal enum class FieldComponentType {
    TEXT,
    EMAIL,
    PASSWORD,
    CHECKBOX,
}
