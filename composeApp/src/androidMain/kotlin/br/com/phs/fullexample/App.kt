package br.com.phs.fullexample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun App() {
    val engine = remember { AndroidDynamicFormEngine() }
    var formState by remember { mutableStateOf(engine.getState()) }

    fun refreshState() {
        formState = engine.getState()
    }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .padding(24.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    repeat(engine.getComponentsCount()) { index ->
                        val component = engine.getComponentAt(index)
                        when {
                            component.kind == "heading" -> Text(
                                text = component.text,
                                modifier = componentModifier(component.align),
                                style = MaterialTheme.typography.headlineMedium,
                                textAlign = componentTextAlign(component.align),
                            )

                            component.kind == "text" -> Text(
                                text = component.text,
                                modifier = componentModifier(component.align),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = componentTextAlign(component.align),
                            )

                            component.isCheckbox -> {
                                Row(
                                    modifier = componentModifier(component.align),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Checkbox(
                                        checked = component.checked,
                                        onCheckedChange = {
                                            engine.updateCheckboxField(component.id, it)
                                            refreshState()
                                        },
                                    )
                                    Text(text = component.label)
                                }
                            }

                            component.isTextInput -> {
                                OutlinedTextField(
                                    value = component.value,
                                    onValueChange = {
                                        engine.updateTextField(component.id, it)
                                        refreshState()
                                    },
                                    modifier = componentModifier(component.align),
                                    label = { Text(component.label) },
                                    placeholder = {
                                        if (component.placeholder.isNotBlank()) {
                                            Text(component.placeholder)
                                        }
                                    },
                                    singleLine = true,
                                    isError = component.error.isNotBlank(),
                                    supportingText = {
                                        if (component.error.isNotBlank()) {
                                            Text(component.error)
                                        }
                                    },
                                    visualTransformation = if (component.isPassword) {
                                        PasswordVisualTransformation()
                                    } else {
                                        VisualTransformation.None
                                    },
                                )
                            }

                            component.isStatus && component.isVisible -> Text(
                                text = component.text,
                                modifier = componentModifier(component.align),
                                color = if (formState.isAuthenticated) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.error
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = componentTextAlign(component.align),
                            )

                            component.isButton -> Button(
                                onClick = {
                                    engine.triggerAction(component.actionId)
                                    refreshState()
                                },
                                modifier = componentModifier(component.align),
                                enabled = component.isEnabled,
                            ) {
                                Text(component.label)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun componentModifier(align: String): Modifier {
    return when (align) {
        "center" -> Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
        "right" -> Modifier.fillMaxWidth().wrapContentWidth(Alignment.End)
        "fill_width" -> Modifier.fillMaxWidth()
        else -> Modifier.fillMaxWidth().wrapContentWidth(Alignment.Start)
    }
}

private fun componentTextAlign(align: String): TextAlign {
    return when (align) {
        "center" -> TextAlign.Center
        "right" -> TextAlign.End
        else -> TextAlign.Start
    }
}

@Preview
@Composable
private fun AppPreview() {
    App()
}
