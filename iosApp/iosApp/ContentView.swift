import SwiftUI
import Shared

struct UserRow: Identifiable {
    let id: String
    let name: String
    let email: String
    let phone: String
}

@MainActor
final class RegistrationViewModel: ObservableObject {
    private let engine = IosUserRegistrationEngine()

    @Published var state: UserRegistrationState
    @Published var users: [UserRow]

    init() {
        self.state = engine.getState()
        self.users = []
        refresh()
    }

    func updateName(_ value: String) {
        engine.updateName(value: value)
        refresh()
    }

    func updateEmail(_ value: String) {
        engine.updateEmail(value: value)
        refresh()
    }

    func updatePhone(_ value: String) {
        engine.updatePhone(value: value)
        refresh()
    }

    func saveUser() {
        engine.saveUser()
        refresh()
    }

    func openUsers() {
        engine.openUsers()
        refresh()
    }

    func openRegister() {
        engine.openRegister()
        refresh()
    }

    private func refresh() {
        state = engine.getState()
        users = (0..<Int(engine.getUsersCount())).map { index in
            let user = engine.getUserAt(index: Int32(index))
            return UserRow(
                id: "\(user.email)-\(user.phone)",
                name: user.name,
                email: user.email,
                phone: user.phone
            )
        }
    }
}

struct ContentView: View {
    @StateObject private var viewModel = RegistrationViewModel()

    var body: some View {
        NavigationStack {
            Group {
                if viewModel.state.isUsersScreen {
                    usersScreen
                } else {
                    registerScreen
                }
            }
            .navigationTitle(viewModel.state.isUsersScreen ? "Usuarios" : "Cadastro")
            .padding()
        }
    }

    private var registerScreen: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text("Cadastro de Usuarios")
                .font(.largeTitle.bold())

            TextField("Nome", text: Binding(
                get: { viewModel.state.name },
                set: viewModel.updateName
            ))
            .textFieldStyle(.roundedBorder)

            TextField("E-mail", text: Binding(
                get: { viewModel.state.email },
                set: viewModel.updateEmail
            ))
            .textInputAutocapitalization(.never)
            .keyboardType(.emailAddress)
            .textFieldStyle(.roundedBorder)

            TextField("Telefone", text: Binding(
                get: { viewModel.state.phone },
                set: viewModel.updatePhone
            ))
            .keyboardType(.phonePad)
            .textFieldStyle(.roundedBorder)

            if !viewModel.state.message.isEmpty {
                Text(viewModel.state.message)
                    .foregroundStyle(.indigo)
            }

            HStack(spacing: 12) {
                Button("Salvar", action: viewModel.saveUser)
                    .buttonStyle(.borderedProminent)

                Button("Usuarios", action: viewModel.openUsers)
                    .buttonStyle(.bordered)
            }

            Spacer()
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
    }

    private var usersScreen: some View {
        VStack(alignment: .leading, spacing: 16) {
            HStack {
                Text("Usuarios Cadastrados")
                    .font(.largeTitle.bold())

                Spacer()

                Button("Cadastrar", action: viewModel.openRegister)
                    .buttonStyle(.borderedProminent)
            }

            if !viewModel.state.message.isEmpty {
                Text(viewModel.state.message)
                    .foregroundStyle(.indigo)
            }

            if viewModel.users.isEmpty {
                ContentUnavailableView("Nenhum usuario cadastrado.", systemImage: "person.crop.circle.badge.plus")
            } else {
                List(viewModel.users) { user in
                    VStack(alignment: .leading, spacing: 6) {
                        Text(user.name)
                            .font(.headline)
                        Text(user.email)
                            .foregroundStyle(.secondary)
                        Text(user.phone)
                            .foregroundStyle(.secondary)
                    }
                    .padding(.vertical, 4)
                }
                .listStyle(.plain)
            }
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
