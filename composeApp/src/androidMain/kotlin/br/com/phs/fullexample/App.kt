package br.com.phs.fullexample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun App() {
    val engine = remember { AndroidDynamicFormEngine() }
    var formState by remember { mutableStateOf(engine.getState()) }
    var components by remember { mutableStateOf(loadComponents(engine)) }

    fun refreshState() {
        formState = engine.getState()
        components = loadComponents(engine)
    }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .padding(24.dp),
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    components.forEach { component ->
                        val outerModifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = component.marginLeft.dp,
                                top = component.marginTop.dp,
                                end = component.marginRight.dp,
                                bottom = component.marginBottom.dp,
                            )

                        when {
                            component.isSpace -> Spacer(
                                modifier = outerModifier.then(
                                    Modifier
                                        .width(component.spaceWidth.dp)
                                        .height(component.spaceHeight.dp),
                                ),
                            )

                            component.kind == "heading" -> AlignedRow(component.align, outerModifier) {
                                Text(
                                    text = component.text,
                                    modifier = contentModifier(component.align),
                                    style = MaterialTheme.typography.headlineMedium,
                                    textAlign = textAlign(component.align),
                                )
                            }

                            component.kind == "text" -> AlignedRow(component.align, outerModifier) {
                                Text(
                                    text = component.text,
                                    modifier = contentModifier(component.align),
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = textAlign(component.align),
                                )
                            }

                            component.isCheckbox -> AlignedRow(component.align, outerModifier) {
                                Row(
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

                            component.isTextInput -> AlignedRow(component.align, outerModifier) {
                                OutlinedTextField(
                                    value = component.value,
                                    onValueChange = {
                                        engine.updateTextField(component.id, it)
                                        refreshState()
                                    },
                                    modifier = contentModifier(component.align),
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

                            component.isStatus && component.isVisible -> AlignedRow(component.align, outerModifier) {
                                Text(
                                    text = component.text,
                                    modifier = contentModifier(component.align),
                                    color = if (formState.isAuthenticated) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.error
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = textAlign(component.align),
                                )
                            }

                            component.isButton -> AlignedRow(component.align, outerModifier) {
                                Button(
                                    onClick = {
                                        engine.triggerAction(component.actionId)
                                        refreshState()
                                    },
                                    modifier = contentModifier(component.align),
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
}

private fun loadComponents(engine: AndroidDynamicFormEngine): List<DynamicComponentState> {
    return List(engine.getComponentsCount()) { index ->
        engine.getComponentAt(index)
    }
}

@Composable
private fun AlignedRow(
    align: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    when (align) {
        "center" -> Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.weight(1f))
            content()
            Spacer(modifier = Modifier.weight(1f))
        }

        "right" -> Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.weight(1f))
            content()
        }

        else -> Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            content()
        }
    }
}

private fun contentModifier(align: String): Modifier {
    return when (align) {
        "fill_width" -> Modifier.fillMaxWidth()
        else -> Modifier
    }
}

private fun textAlign(align: String): TextAlign {
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
