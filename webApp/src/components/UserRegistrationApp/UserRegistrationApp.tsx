import './UserRegistrationApp.css';

import type { CSSProperties } from 'react';
import { useState } from 'react';
import { DynamicComponentState, WebDynamicFormEngine } from 'shared';

function alignStyle(align: string): CSSProperties {
  switch (align) {
    case 'center':
      return { alignSelf: 'center', textAlign: 'center' };
    case 'right':
      return { alignSelf: 'flex-end', textAlign: 'right' };
    case 'fill_width':
      return { width: '100%' };
    default:
      return { alignSelf: 'flex-start', textAlign: 'left' };
  }
}

export function UserRegistrationApp() {
  const [engine] = useState(() => new WebDynamicFormEngine());
  const [components, setComponents] = useState<DynamicComponentState[]>(() =>
    Array.from({ length: engine.getComponentsCount() }, (_, index) => engine.getComponentAt(index)),
  );

  const refresh = () => {
    setComponents(Array.from({ length: engine.getComponentsCount() }, (_, index) => engine.getComponentAt(index)));
  };

  return (
    <main className="registration-shell">
      <section className="registration-card">
        <p className="eyebrow">KMP Shared XML</p>

        {components.map((component) => {
          if (!component.isVisible) {
            return null;
          }

          if (component.kind === 'heading') {
            return <h1 key={component.id} style={alignStyle(component.align)}>{component.text}</h1>;
          }

          if (component.kind === 'text') {
            return <p key={component.id} style={alignStyle(component.align)}>{component.text}</p>;
          }

          if (component.isCheckbox) {
            return (
              <label
                key={component.id}
                style={{
                  ...alignStyle(component.align),
                  alignItems: 'center',
                  display: 'flex',
                  flexDirection: 'row',
                  gap: '0.75rem',
                  width: component.align === 'fill_width' ? '100%' : 'fit-content',
                }}
              >
                <input
                  checked={component.checked}
                  onChange={(event) => {
                    engine.updateCheckboxField(component.id, event.target.checked);
                    refresh();
                  }}
                  style={{ width: 'auto' }}
                  type="checkbox"
                />
                <span>{component.label}</span>
              </label>
            );
          }

          if (component.isTextInput) {
            return (
              <label key={component.id} style={alignStyle(component.align)}>
                <span>{component.label}</span>
                <input
                  maxLength={component.maxLength > 0 ? component.maxLength : undefined}
                  onChange={(event) => {
                    engine.updateTextField(component.id, event.target.value);
                    refresh();
                  }}
                  placeholder={component.placeholder || component.label}
                  type={component.isPassword ? 'password' : 'text'}
                  value={component.value}
                />
                {component.error && <small className="status-message">{component.error}</small>}
              </label>
            );
          }

          if (component.isStatus) {
            return (
              <p className="status-message" key={component.id} style={alignStyle(component.align)}>
                {component.text}
              </p>
            );
          }

          if (component.isButton) {
            return (
              <div className="actions-row" key={component.id} style={alignStyle(component.align)}>
                <button
                  className="primary-button"
                  disabled={!component.isEnabled}
                  onClick={() => {
                    engine.triggerAction(component.actionId);
                    refresh();
                  }}
                  type="button"
                >
                  {component.label}
                </button>
              </div>
            );
          }

          return null;
        })}
      </section>
    </main>
  );
}
