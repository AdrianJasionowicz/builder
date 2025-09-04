import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from './axiosInstance';
import './LoginForm.css';  

function LoginForm() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      await api.post('/login', { username, password });
      alert('Zalogowano pomyślnie');
      navigate('/');

    } catch (err) {
      alert('Błędne dane logowania');
    }
  };

  return (
    <div className="login-wrapper">
      <div className="login-container">
        <h2 className="login-title">Logowanie</h2>
        <form onSubmit={handleLogin} className="login-form">
          <input
            type="text"
            placeholder="Login"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
            className="login-input"
          />
          <input
            type="password"
            placeholder="Hasło"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            className="login-input"
          />
          <button type="submit" className="login-button">Zaloguj</button>
        </form>
        <p className="login-text">
          Nie masz konta?{' '}
          <Link to="/register" className="login-link">
            Załóż je
          </Link>
        </p>
      </div>
    </div>
  );
}

export default LoginForm;
