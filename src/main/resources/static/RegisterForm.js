import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from './axiosInstance';
import './RegisterForm.css';

function RegisterForm() {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState(''); 
  const navigate = useNavigate();

  const handleRegister = async (e) => {
    e.preventDefault();

    if (password !== confirmPassword) {
      alert('Hasła nie są takie same!');
      return;
    }

    try {
      await api.post('/register', { username, email, password });
      alert('Konto zostało utworzone. Możesz się teraz zalogować.');
      navigate('/login');
    } catch (err) {
      alert('Coś poszło nie tak przy rejestracji');
    }
  };

  return (
    <div className="register-wrapper">
      <div className="register-container">
        <h2 className="register-title">Rejestracja</h2>
        <form onSubmit={handleRegister} className="register-form">
          <input
            type="text"
            placeholder="Login"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
            className="register-input"
          />
          <input
            type="email"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            className="register-input"
          />
          <input
            type="password"
            placeholder="Hasło"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            className="register-input"
          />
          <input
            type="password"
            placeholder="Potwierdź hasło"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            required
            className="register-input"
          />
          <button type="submit" className="register-button">Załóż konto</button>
        </form>
        <p className="register-text">
          Masz już konto?{' '}
          <Link to="/login" className="register-link">
            Zaloguj się
          </Link>
        </p>
      </div>
    </div>
  );
}

export default RegisterForm;
