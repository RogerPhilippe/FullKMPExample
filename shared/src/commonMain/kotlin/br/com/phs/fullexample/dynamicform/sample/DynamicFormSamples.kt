package br.com.phs.fullexample.dynamicform.sample

import br.com.phs.fullexample.GeneratedDynamicForms
import br.com.phs.fullexample.dynamicform.application.DynamicFormActionHandler
import br.com.phs.fullexample.dynamicform.application.DynamicFormActionRequest
import br.com.phs.fullexample.dynamicform.application.DynamicFormActionResult
import br.com.phs.fullexample.dynamicform.application.DynamicFormSource

internal object BundledSampleLoginFormSource : DynamicFormSource {
    override fun loadXml(): String = GeneratedDynamicForms.sampleLoginFormXml.trimIndent()
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
