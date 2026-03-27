import React from 'react';
import ReactDOM from 'react-dom/client';
import { UserRegistrationApp } from './components/UserRegistrationApp/UserRegistrationApp.tsx';

const rootElement = document.getElementById('root');
if (!rootElement) throw new Error('Failed to find the root element');

ReactDOM.createRoot(rootElement).render(
  <React.StrictMode>
    <UserRegistrationApp />
  </React.StrictMode>
);
