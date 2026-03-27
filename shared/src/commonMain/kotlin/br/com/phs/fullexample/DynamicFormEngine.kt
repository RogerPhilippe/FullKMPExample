package br.com.phs.fullexample

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

internal interface DynamicFormSource {
    fun loadXml(): String
}

internal object BundledSampleLoginFormSource : DynamicFormSource {
    override fun loadXml(): String = GeneratedDynamicForms.sampleLoginFormXml.trimIndent()
}

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

internal data class DynamicFormActionRequest(
    val actionId: String,
    val textValues: Map<String, String>,
    val checkboxValues: Map<String, Boolean>,
)

internal data class DynamicFormActionResult(
    val isSuccess: Boolean,
    val statusMessage: String,
    val fieldErrors: Map<String, String> = emptyMap(),
    val clearTextFields: Set<String> = emptySet(),
)

internal interface DynamicFormActionHandler {
    fun onAction(request: DynamicFormActionRequest): DynamicFormActionResult
}

internal object SampleLoginActionHandler : DynamicFormActionHandler {
    override fun onAction(request: DynamicFormActionRequest): DynamicFormActionResult {
        require(request.actionId == "submit") { "Acao nao suportada: ${request.actionId}" }

        val email = request.textValues["email"].orEmpty().trim()
        val password = request.textValues["password"].orEmpty()
        val rememberMe = request.checkboxValues["rememberMe"] == true

        return if (email == "demo@acme.com" && password == "123456") {
            DynamicFormActionResult(
                isSuccess = true,
                statusMessage = if (rememberMe) {
                    "Login efetuado. Sessao persistente habilitada."
                } else {
                    "Login efetuado. Sessao temporaria criada."
                },
                clearTextFields = setOf("password"),
            )
        } else {
            DynamicFormActionResult(
                isSuccess = false,
                statusMessage = "Credenciais invalidas. Use demo@acme.com / 123456.",
                fieldErrors = mapOf("password" to "Senha ou e-mail nao conferem."),
            )
        }
    }
}

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

open class DynamicFormEngine internal constructor(
    source: DynamicFormSource,
    private val actionHandler: DynamicFormActionHandler,
) {
    private val definition = DynamicFormXmlParser.parse(source.loadXml())
    private val textValues = linkedMapOf<String, String>()
    private val checkboxValues = linkedMapOf<String, Boolean>()
    private val fieldErrors = linkedMapOf<String, String>()
    private var statusMessage = ""
    private var isLoading = false
    private var isAuthenticated = false

    init {
        definition.components.forEach { component ->
            if (component is FieldComponentDefinition) {
                if (component.isCheckbox) {
                    checkboxValues[component.id] = component.defaultChecked
                } else {
                    textValues[component.id] = ""
                }
            }
        }
    }

    fun getState(): DynamicFormState {
        return DynamicFormState(
            formId = definition.id,
            statusMessage = statusMessage,
            isLoading = isLoading,
            isAuthenticated = isAuthenticated,
        )
    }

    fun getComponentsCount(): Int = definition.components.size

    fun getComponentAt(index: Int): DynamicComponentState {
        return when (val component = definition.components[index]) {
            is HeadingComponentDefinition -> DynamicComponentState(
                id = component.id,
                kind = "heading",
                text = component.text,
                label = "",
                placeholder = "",
                value = "",
                checked = false,
                error = "",
                actionId = "",
                isVisible = true,
                isEnabled = true,
                isTextInput = false,
                isCheckbox = false,
                isPassword = false,
                isButton = false,
                isStatus = false,
                isSpace = false,
                maxLength = 0,
                align = component.align.name.lowercase(),
                spaceWidth = 0,
                spaceHeight = 0,
                marginLeft = component.margin.left,
                marginTop = component.margin.top,
                marginRight = component.margin.right,
                marginBottom = component.margin.bottom,
            )

            is TextComponentDefinition -> DynamicComponentState(
                id = component.id,
                kind = "text",
                text = component.text,
                label = "",
                placeholder = "",
                value = "",
                checked = false,
                error = "",
                actionId = "",
                isVisible = true,
                isEnabled = true,
                isTextInput = false,
                isCheckbox = false,
                isPassword = false,
                isButton = false,
                isStatus = false,
                isSpace = false,
                maxLength = 0,
                align = component.align.name.lowercase(),
                spaceWidth = 0,
                spaceHeight = 0,
                marginLeft = component.margin.left,
                marginTop = component.margin.top,
                marginRight = component.margin.right,
                marginBottom = component.margin.bottom,
            )

            is StatusComponentDefinition -> DynamicComponentState(
                id = component.id,
                kind = "status",
                text = statusMessage,
                label = "",
                placeholder = "",
                value = "",
                checked = false,
                error = "",
                actionId = "",
                isVisible = statusMessage.isNotBlank(),
                isEnabled = true,
                isTextInput = false,
                isCheckbox = false,
                isPassword = false,
                isButton = false,
                isStatus = true,
                isSpace = false,
                maxLength = 0,
                align = component.align.name.lowercase(),
                spaceWidth = 0,
                spaceHeight = 0,
                marginLeft = component.margin.left,
                marginTop = component.margin.top,
                marginRight = component.margin.right,
                marginBottom = component.margin.bottom,
            )

            is SpaceComponentDefinition -> DynamicComponentState(
                id = component.id,
                kind = "space",
                text = "",
                label = "",
                placeholder = "",
                value = "",
                checked = false,
                error = "",
                actionId = "",
                isVisible = true,
                isEnabled = false,
                isTextInput = false,
                isCheckbox = false,
                isPassword = false,
                isButton = false,
                isStatus = false,
                isSpace = true,
                maxLength = 0,
                align = component.align.name.lowercase(),
                spaceWidth = component.width,
                spaceHeight = component.height,
                marginLeft = component.margin.left,
                marginTop = component.margin.top,
                marginRight = component.margin.right,
                marginBottom = component.margin.bottom,
            )

            is FieldComponentDefinition -> DynamicComponentState(
                id = component.id,
                kind = if (component.isCheckbox) "checkbox" else "field",
                text = "",
                label = component.label,
                placeholder = component.placeholder,
                value = textValues[component.id].orEmpty(),
                checked = checkboxValues[component.id] == true,
                error = fieldErrors[component.id].orEmpty(),
                actionId = "",
                isVisible = true,
                isEnabled = true,
                isTextInput = component.isTextInput,
                isCheckbox = component.isCheckbox,
                isPassword = component.type == FieldComponentType.PASSWORD,
                isButton = false,
                isStatus = false,
                isSpace = false,
                maxLength = component.maxLength ?: 0,
                align = component.align.name.lowercase(),
                spaceWidth = 0,
                spaceHeight = 0,
                marginLeft = component.margin.left,
                marginTop = component.margin.top,
                marginRight = component.margin.right,
                marginBottom = component.margin.bottom,
            )

            is ButtonComponentDefinition -> DynamicComponentState(
                id = component.id,
                kind = "button",
                text = component.label,
                label = component.label,
                placeholder = "",
                value = "",
                checked = false,
                error = "",
                actionId = component.action,
                isVisible = true,
                isEnabled = canTriggerActions(),
                isTextInput = false,
                isCheckbox = false,
                isPassword = false,
                isButton = true,
                isStatus = false,
                isSpace = false,
                maxLength = 0,
                align = component.align.name.lowercase(),
                spaceWidth = 0,
                spaceHeight = 0,
                marginLeft = component.margin.left,
                marginTop = component.margin.top,
                marginRight = component.margin.right,
                marginBottom = component.margin.bottom,
            )
        }
    }

    fun updateTextField(componentId: String, value: String) {
        val definition = requireTextField(componentId)
        textValues[componentId] = value.limitTo(definition.maxLength)
        fieldErrors.remove(componentId)
        statusMessage = ""
        isAuthenticated = false
    }

    fun updateCheckboxField(componentId: String, checked: Boolean) {
        requireCheckboxField(componentId)
        checkboxValues[componentId] = checked
        fieldErrors.remove(componentId)
        statusMessage = ""
    }

    fun triggerAction(actionId: String) {
        if (isLoading) return

        val hasErrors = validateAllTextFields()
        if (hasErrors) {
            statusMessage = "Revise os campos destacados."
            isAuthenticated = false
            return
        }

        isLoading = true
        val result = actionHandler.onAction(
            DynamicFormActionRequest(
                actionId = actionId,
                textValues = textValues.toMap(),
                checkboxValues = checkboxValues.toMap(),
            ),
        )
        isLoading = false
        statusMessage = result.statusMessage
        isAuthenticated = result.isSuccess
        fieldErrors.clear()
        fieldErrors.putAll(result.fieldErrors)
        result.clearTextFields.forEach { fieldId ->
            if (textValues.containsKey(fieldId)) {
                textValues[fieldId] = ""
            }
        }
    }

    private fun validateAllTextFields(): Boolean {
        var hasErrors = false
        definition.components.forEach { component ->
            if (component is FieldComponentDefinition && component.isTextInput) {
                val error = validateField(component)
                if (error != null) {
                    fieldErrors[component.id] = error
                    hasErrors = true
                } else {
                    fieldErrors.remove(component.id)
                }
            }
        }
        return hasErrors
    }

    private fun canTriggerActions(): Boolean {
        if (isLoading) return false
        return definition.components.none { component ->
            component is FieldComponentDefinition &&
                component.required &&
                component.isTextInput &&
                textValues[component.id].orEmpty().isBlank()
        }
    }

    private fun validateField(field: FieldComponentDefinition): String? {
        val rawValue = textValues[field.id].orEmpty()
        val trimmedValue = rawValue.trim()

        if (field.required && trimmedValue.isBlank()) {
            return "${field.label} e obrigatorio."
        }
        if (trimmedValue.isBlank()) {
            return null
        }
        if (field.minLength != null && trimmedValue.length < field.minLength) {
            return "${field.label} deve ter ao menos ${field.minLength} caracteres."
        }
        if (field.maxLength != null && trimmedValue.length > field.maxLength) {
            return "${field.label} deve ter no maximo ${field.maxLength} caracteres."
        }
        if (field.pattern != null && !Regex(field.pattern).matches(trimmedValue)) {
            return "${field.label} esta em formato invalido."
        }
        return null
    }

    private fun requireTextField(componentId: String): FieldComponentDefinition {
        val field = definition.components
            .filterIsInstance<FieldComponentDefinition>()
            .firstOrNull { it.id == componentId && it.isTextInput }
        require(field != null) { "Campo de texto desconhecido: $componentId" }
        return field
    }

    private fun requireCheckboxField(componentId: String) {
        val field = definition.components
            .filterIsInstance<FieldComponentDefinition>()
            .firstOrNull { it.id == componentId && it.isCheckbox }
        require(field != null) { "Checkbox desconhecido: $componentId" }
    }

    private fun String.limitTo(maxLength: Int?): String {
        if (maxLength == null || length <= maxLength) return this
        return take(maxLength)
    }
}

internal object DynamicFormXmlParser {
    private val formRegex = Regex("<form\\s+([^>]*)>([\\s\\S]*)</form>")
    private val componentRegex = Regex("<(heading|text|status|space|field|button)\\s+([\\s\\S]*?)/>")
    private val attributeRegex = Regex("([a-zA-Z][a-zA-Z0-9]*)\\s*=\\s*\"([^\"]*)\"")

    fun parse(xml: String): DynamicFormDefinition {
        val match = requireNotNull(formRegex.find(xml)) { "XML invalido para o formulario dinamico." }
        val formAttributes = attributeMap(match.groupValues[1])
        val body = match.groupValues[2]
        val components = componentRegex.findAll(body)
            .map { componentMatch ->
                parseComponent(
                    tag = componentMatch.groupValues[1],
                    attributes = attributeMap(componentMatch.groupValues[2]),
                )
            }
            .toList()

        require(components.isNotEmpty()) { "O formulario precisa de componentes." }

        return DynamicFormDefinition(
            id = formAttributes.required("id"),
            components = components,
        )
    }

    private fun parseComponent(tag: String, attributes: Map<String, String>): DynamicComponentDefinition {
        return when (tag) {
            "heading" -> HeadingComponentDefinition(
                id = attributes.required("id"),
                text = attributes.required("text"),
                align = attributes.align(),
                margin = attributes.margin(),
            )

            "text" -> TextComponentDefinition(
                id = attributes.required("id"),
                text = attributes.required("text"),
                align = attributes.align(),
                margin = attributes.margin(),
            )

            "status" -> StatusComponentDefinition(
                id = attributes.required("id"),
                align = attributes.align(),
                margin = attributes.margin(),
            )

            "space" -> SpaceComponentDefinition(
                id = attributes.required("id"),
                width = attributes["width"]?.toIntOrNull() ?: 0,
                height = attributes["height"]?.toIntOrNull() ?: 0,
                align = attributes.align(),
                margin = attributes.margin(),
            )

            "field" -> parseField(attributes)
            "button" -> ButtonComponentDefinition(
                id = attributes.required("id"),
                action = attributes.required("action"),
                label = attributes.required("label"),
                align = attributes.align(),
                margin = attributes.margin(),
            )

            else -> error("Tag nao suportada: $tag")
        }
    }

    private fun parseField(attributes: Map<String, String>): FieldComponentDefinition {
        val type = when (attributes.required("type").lowercase()) {
            "text" -> FieldComponentType.TEXT
            "email" -> FieldComponentType.EMAIL
            "password" -> FieldComponentType.PASSWORD
            "checkbox" -> FieldComponentType.CHECKBOX
            else -> error("Tipo de campo nao suportado.")
        }

        return FieldComponentDefinition(
            id = attributes.required("id"),
            type = type,
            label = attributes.required("label"),
            placeholder = attributes["placeholder"].orEmpty(),
            required = attributes.boolean("required"),
            minLength = attributes["minLength"]?.toIntOrNull(),
            maxLength = attributes["maxLength"]?.toIntOrNull(),
            pattern = attributes["pattern"],
            defaultChecked = attributes.boolean("checked"),
            align = attributes.align(),
            margin = attributes.margin(),
        )
    }

    private fun attributeMap(rawAttributes: String): Map<String, String> {
        return attributeRegex.findAll(rawAttributes)
            .associate { match -> match.groupValues[1] to match.groupValues[2] }
    }

    private fun Map<String, String>.required(name: String): String {
        return requireNotNull(this[name]) { "Atributo obrigatorio ausente: $name" }
    }

    private fun Map<String, String>.boolean(name: String): Boolean {
        return this[name]?.equals("true", ignoreCase = true) == true
    }

    private fun Map<String, String>.align(): ComponentAlign {
        return when (this["align"]?.lowercase().orEmpty()) {
            "", "left" -> ComponentAlign.LEFT
            "center" -> ComponentAlign.CENTER
            "right" -> ComponentAlign.RIGHT
            "fill_width" -> ComponentAlign.FILL_WIDTH
            else -> error("Valor de align nao suportado: ${this["align"]}")
        }
    }

    private fun Map<String, String>.margin(): ComponentMargin {
        val all = this["margin"]?.toIntOrNull() ?: 0
        return ComponentMargin(
            left = this["marginLeft"]?.toIntOrNull() ?: all,
            top = this["marginTop"]?.toIntOrNull() ?: all,
            right = this["marginRight"]?.toIntOrNull() ?: all,
            bottom = this["marginBottom"]?.toIntOrNull() ?: all,
        )
    }
}
