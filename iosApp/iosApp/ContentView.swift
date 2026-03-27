import SwiftUI
import Shared

struct DynamicComponentRow: Identifiable {
    let id: String
    let kind: String
    let text: String
    let label: String
    let placeholder: String
    let value: String
    let checked: Bool
    let error: String
    let actionId: String
    let isVisible: Bool
    let isEnabled: Bool
    let isTextInput: Bool
    let isCheckbox: Bool
    let isPassword: Bool
    let isButton: Bool
    let isStatus: Bool
    let align: String
}

@MainActor
final class DynamicFormViewModel: ObservableObject {
    private let engine = IosDynamicFormEngine()

    @Published var state: DynamicFormState
    @Published var components: [DynamicComponentRow]

    init() {
        self.state = engine.getState()
        self.components = []
        refresh()
    }

    func updateText(id: String, value: String) {
        engine.updateTextField(componentId: id, value: value)
        refresh()
    }

    func updateCheckbox(id: String, checked: Bool) {
        engine.updateCheckboxField(componentId: id, checked: checked)
        refresh()
    }

    func trigger(actionId: String) {
        engine.triggerAction(actionId: actionId)
        refresh()
    }

    func component(for id: String) -> DynamicComponentRow? {
        components.first(where: { $0.id == id })
    }

    private func refresh() {
        state = engine.getState()
        components = (0..<Int(engine.getComponentsCount())).map { index in
            let component = engine.getComponentAt(index: Int32(index))
            return DynamicComponentRow(
                id: component.id,
                kind: component.kind,
                text: component.text,
                label: component.label,
                placeholder: component.placeholder,
                value: component.value,
                checked: component.checked,
                error: component.error,
                actionId: component.actionId,
                isVisible: component.isVisible,
                isEnabled: component.isEnabled,
                isTextInput: component.isTextInput,
                isCheckbox: component.isCheckbox,
                isPassword: component.isPassword,
                isButton: component.isButton,
                isStatus: component.isStatus,
                align: component.align
            )
        }
    }
}

struct ContentView: View {
    @StateObject private var viewModel = DynamicFormViewModel()

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    ForEach(viewModel.components) { component in
                        if !component.isVisible {
                            EmptyView()
                        } else if component.kind == "heading" {
                            Text(component.text)
                                .font(.largeTitle.bold())
                                .frame(maxWidth: component.align == "fill_width" ? .infinity : nil, alignment: swiftUIAlignment(component.align))
                        } else if component.kind == "text" {
                            Text(component.text)
                                .foregroundStyle(.secondary)
                                .frame(maxWidth: component.align == "fill_width" ? .infinity : nil, alignment: swiftUIAlignment(component.align))
                        } else if component.isCheckbox {
                            Toggle(
                                component.label,
                                isOn: Binding(
                                    get: { viewModel.component(for: component.id)?.checked ?? false },
                                    set: { viewModel.updateCheckbox(id: component.id, checked: $0) }
                                )
                            )
                            .frame(maxWidth: component.align == "fill_width" ? .infinity : nil, alignment: swiftUIAlignment(component.align))
                        } else if component.isPassword {
                            VStack(alignment: .leading, spacing: 8) {
                                SecureField(
                                    component.placeholder.isEmpty ? component.label : component.placeholder,
                                    text: Binding(
                                        get: { viewModel.component(for: component.id)?.value ?? "" },
                                        set: { viewModel.updateText(id: component.id, value: $0) }
                                    )
                                )
                                .textFieldStyle(.roundedBorder)

                                if let latest = viewModel.component(for: component.id), !latest.error.isEmpty {
                                    Text(latest.error)
                                        .font(.footnote)
                                        .foregroundStyle(.red)
                                }
                            }
                            .frame(maxWidth: component.align == "fill_width" ? .infinity : nil, alignment: swiftUIAlignment(component.align))
                        } else if component.isTextInput {
                            VStack(alignment: .leading, spacing: 8) {
                                TextField(
                                    component.placeholder.isEmpty ? component.label : component.placeholder,
                                    text: Binding(
                                        get: { viewModel.component(for: component.id)?.value ?? "" },
                                        set: { viewModel.updateText(id: component.id, value: $0) }
                                    )
                                )
                                .textInputAutocapitalization(.never)
                                .keyboardType(component.id == "email" ? .emailAddress : .default)
                                .textFieldStyle(.roundedBorder)

                                if let latest = viewModel.component(for: component.id), !latest.error.isEmpty {
                                    Text(latest.error)
                                        .font(.footnote)
                                        .foregroundStyle(.red)
                                }
                            }
                            .frame(maxWidth: component.align == "fill_width" ? .infinity : nil, alignment: swiftUIAlignment(component.align))
                        } else if component.isStatus {
                            Text(component.text)
                                .foregroundStyle(viewModel.state.isAuthenticated ? .green : .red)
                                .frame(maxWidth: component.align == "fill_width" ? .infinity : nil, alignment: swiftUIAlignment(component.align))
                        } else if component.isButton {
                            Button(action: { viewModel.trigger(actionId: component.actionId) }) {
                                Text(component.label)
                                    .frame(maxWidth: component.align == "fill_width" ? .infinity : nil)
                            }
                            .buttonStyle(.borderedProminent)
                            .disabled(!component.isEnabled)
                            .frame(maxWidth: component.align == "fill_width" ? .infinity : nil, alignment: swiftUIAlignment(component.align))
                        }
                    }
                }
                .frame(maxWidth: .infinity, alignment: .topLeading)
                .padding()
            }
            .navigationTitle("Login")
        }
    }
}

private func swiftUIAlignment(_ align: String) -> Alignment {
    switch align {
    case "center":
        return .center
    case "right":
        return .trailing
    default:
        return .leading
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
