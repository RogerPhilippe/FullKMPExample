import './UserRegistrationApp.css';

import { useState } from 'react';
import { RegisteredUser, UserRegistrationScreen, WebUserRegistrationEngine } from 'shared';

export function UserRegistrationApp() {
  const [engine] = useState(() => new WebUserRegistrationEngine());
  const [state, setState] = useState(() => engine.getState());

  const refreshState = () => {
    setState(engine.getState());
  };

  return (
    <main className="registration-shell">
      <section className="registration-card">
        {state.screen === UserRegistrationScreen.USERS ? (
          <>
            <div className="header-row">
              <div>
                <p className="eyebrow">KMP Shared</p>
                <h1>Usuarios Cadastrados</h1>
              </div>

              <button
                className="secondary-button"
                onClick={() => {
                  engine.openRegister();
                  refreshState();
                }}
                type="button"
              >
                Cadastrar
              </button>
            </div>

            {state.message && <p className="status-message">{state.message}</p>}

            {state.users.length === 0 ? (
              <div className="empty-state">Nenhum usuario cadastrado.</div>
            ) : (
              <div className="users-grid">
                {state.users.map((user: RegisteredUser) => (
                  <article className="user-card" key={`${user.email}-${user.phone}`}>
                    <strong>{user.name}</strong>
                    <span>{user.email}</span>
                    <span>{user.phone}</span>
                  </article>
                ))}
              </div>
            )}
          </>
        ) : (
          <>
            <p className="eyebrow">KMP Shared</p>
            <h1>{state.title}</h1>

            <label>
              <span>Nome</span>
              <input
                onChange={(event) => {
                  engine.updateName(event.target.value);
                  refreshState();
                }}
                type="text"
                value={state.name}
              />
            </label>

            <label>
              <span>E-mail</span>
              <input
                onChange={(event) => {
                  engine.updateEmail(event.target.value);
                  refreshState();
                }}
                type="email"
                value={state.email}
              />
            </label>

            <label>
              <span>Telefone</span>
              <input
                onChange={(event) => {
                  engine.updatePhone(event.target.value);
                  refreshState();
                }}
                type="tel"
                value={state.phone}
              />
            </label>

            {state.message && <p className="status-message">{state.message}</p>}

            <div className="actions-row">
              <button
                className="primary-button"
                onClick={() => {
                  engine.saveUser();
                  refreshState();
                }}
                type="button"
              >
                Salvar
              </button>

              <button
                className="secondary-button"
                onClick={() => {
                  engine.openUsers();
                  refreshState();
                }}
                type="button"
              >
                Usuarios
              </button>
            </div>
          </>
        )}
      </section>
    </main>
  );
}
