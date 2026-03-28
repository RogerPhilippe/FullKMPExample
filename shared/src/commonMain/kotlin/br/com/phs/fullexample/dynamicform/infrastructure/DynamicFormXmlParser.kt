package br.com.phs.fullexample.dynamicform.infrastructure

import br.com.phs.fullexample.dynamicform.domain.ButtonComponentDefinition
import br.com.phs.fullexample.dynamicform.domain.ComponentAlign
import br.com.phs.fullexample.dynamicform.domain.ComponentMargin
import br.com.phs.fullexample.dynamicform.domain.DynamicComponentDefinition
import br.com.phs.fullexample.dynamicform.domain.DynamicFormDefinition
import br.com.phs.fullexample.dynamicform.domain.FieldComponentDefinition
import br.com.phs.fullexample.dynamicform.domain.FieldComponentType
import br.com.phs.fullexample.dynamicform.domain.HeadingComponentDefinition
import br.com.phs.fullexample.dynamicform.domain.SpaceComponentDefinition
import br.com.phs.fullexample.dynamicform.domain.StatusComponentDefinition
import br.com.phs.fullexample.dynamicform.domain.TextComponentDefinition

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
