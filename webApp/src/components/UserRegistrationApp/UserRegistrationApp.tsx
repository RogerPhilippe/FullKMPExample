import './UserRegistrationApp.css';

import type { CSSProperties } from 'react';
import { useState } from 'react';
import { DynamicComponentState, WebDynamicFormEngine } from 'shared';

function alignStyle(align: string): CSSProperties {
  switch (align) {
    case 'center':
      return { display: 'flex', justifyContent: 'center', width: '100%' };
    case 'right':
      return { display: 'flex', justifyContent: 'flex-end', width: '100%' };
    case 'fill_width':
      return { display: 'block', width: '100%' };
    default:
      return { display: 'flex', justifyContent: 'flex-start', width: '100%' };
  }
}

function componentMarginStyle(component: DynamicComponentState): CSSProperties {
  return {
    marginBottom: `${component.marginBottom}px`,
    marginLeft: `${component.marginLeft}px`,
    marginRight: `${component.marginRight}px`,
    marginTop: `${component.marginTop}px`,
  };
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
        {components.map((component) => {
          if (!component.isVisible) {
            return null;
          }

          if (component.kind === 'heading') {
            return <div key={component.id} style={{ ...alignStyle(component.align), ...componentMarginStyle(component) }}><h1 style={{ margin: 0, textAlign: component.align === 'center' ? 'center' : component.align === 'right' ? 'right' : 'left', width: component.align === 'fill_width' ? '100%' : 'auto' }}>{component.text}</h1></div>;
          }

          if (component.kind === 'text') {
            return <div key={component.id} style={{ ...alignStyle(component.align), ...componentMarginStyle(component) }}><p style={{ margin: 0, textAlign: component.align === 'center' ? 'center' : component.align === 'right' ? 'right' : 'left', width: component.align === 'fill_width' ? '100%' : 'auto' }}>{component.text}</p></div>;
          }

          if (component.isSpace) {
            return <div key={component.id} style={{ ...alignStyle(component.align), ...componentMarginStyle(component) }}><div style={{ height: `${component.spaceHeight}px`, width: component.spaceWidth > 0 ? `${component.spaceWidth}px` : undefined }} /></div>;
          }

          if (component.isCheckbox) {
            return (
              <div key={component.id} style={{ ...alignStyle(component.align), ...componentMarginStyle(component) }}>
                <label
                  style={{
                    alignItems: 'center',
                    display: 'flex',
                    flexDirection: 'row',
                    gap: '0.75rem',
                    width: component.align === 'fill_width' ? '100%' : 'fit-content',
                    marginBottom: 0,
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
              </div>
            );
          }

          if (component.isTextInput) {
            return (
              <div key={component.id} style={{ ...alignStyle(component.align), ...componentMarginStyle(component) }}>
                <label style={{ width: component.align === 'fill_width' ? '100%' : '320px', maxWidth: '100%', marginBottom: 0 }}>
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
              </div>
            );
          }

          if (component.isStatus) {
            return (
              <div key={component.id} style={{ ...alignStyle(component.align), ...componentMarginStyle(component) }}>
                <p className="status-message" style={{ margin: 0, textAlign: component.align === 'center' ? 'center' : component.align === 'right' ? 'right' : 'left', width: component.align === 'fill_width' ? '100%' : 'auto' }}>
                  {component.text}
                </p>
              </div>
            );
          }

          if (component.isButton) {
            return (
              <div key={component.id} style={{ ...alignStyle(component.align), ...componentMarginStyle(component) }}>
                <div className="actions-row" style={{ width: component.align === 'fill_width' ? '100%' : 'auto' }}>
                  <button
                    className="primary-button"
                    disabled={!component.isEnabled}
                    onClick={() => {
                      engine.triggerAction(component.actionId);
                      refresh();
                    }}
                    style={{ width: component.align === 'fill_width' ? '100%' : 'auto' }}
                    type="button"
                  >
                    {component.label}
                  </button>
                </div>
              </div>
            );
          }

          return null;
        })}
      </section>
    </main>
  );
}
