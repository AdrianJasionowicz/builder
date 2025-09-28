import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "./axiosInstance";
import "./UserSettings.css";

function getJwtFromCookie() {
  const match = document.cookie.match(new RegExp("(^| )jwt=([^;]+)"));
  return match ? match[2] : null;
}

export default function UserSettings() {
  const [userInfo, setUserInfo] = useState(null);
  const [email, setEmail] = useState("");
  const [currentPasswordForEmail, setCurrentPasswordForEmail] = useState("");
  const [currentPasswordForPassword, setCurrentPasswordForPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    const fetchUserInfo = async () => {
      const token = getJwtFromCookie();
      if (!token) return;
      try {
        const res = await api.get("/user/getUserInfo", {
          headers: { Authorization: `Bearer ${token}` },
        });
        setUserInfo(res.data);
        setEmail(res.data.email);
      } catch {}
    };
    fetchUserInfo();
  }, []);

  const handleEmailChange = async () => {
    const token = getJwtFromCookie();
    if (!token) return;
    try {
      await api.post(
        "/user/setEmail",
        { email, password: currentPasswordForEmail },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      alert("Email zmieniony");
      setCurrentPasswordForEmail("");
    } catch {
      alert("Błąd zmiany emaila");
    }
  };

  const handlePasswordChange = async () => {
    const token = getJwtFromCookie();
    if (!token) return;
    try {
      await api.post(
        "/user/setPassword",
        { password: currentPasswordForPassword, newPassword },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      alert("Hasło zmienione");
      setCurrentPasswordForPassword("");
      setNewPassword("");
    } catch {
      alert("Błąd zmiany hasła");
    }
  };

  if (!userInfo) return <p className="loading">Ładowanie...</p>;

  return (
    <div className="settings-wrapper">
      <div className="settings-header">
        <button className="btn back-btn" onClick={() => navigate("/")}>← Powrót</button>
        <h1>Ustawienia konta</h1>
      </div>

      <div className="card">
        <div className="row">
          <span className="label">Aktualny e-mail</span>
          <span className="value">{userInfo.email}</span>
        </div>
      </div>

      <div className="grid">
        <div className="card">
          <h2>Zmień e-mail</h2>
          <label className="label" htmlFor="email">Nowy e-mail</label>
          <input
            id="email"
            type="email"
            className="input"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <label className="label" htmlFor="pass-for-email">Aktualne hasło</label>
          <input
            id="pass-for-email"
            type="password"
            className="input"
            value={currentPasswordForEmail}
            onChange={(e) => setCurrentPasswordForEmail(e.target.value)}
          />
          <button className="btn primary" onClick={handleEmailChange}>Zmień e-mail</button>
        </div>

        <div className="card">
          <h2>Zmień hasło</h2>
          <label className="label" htmlFor="curr-pass">Aktualne hasło</label>
          <input
            id="curr-pass"
            type="password"
            className="input"
            value={currentPasswordForPassword}
            onChange={(e) => setCurrentPasswordForPassword(e.target.value)}
          />
          <label className="label" htmlFor="new-pass">Nowe hasło</label>
          <input
            id="new-pass"
            type="password"
            className="input"
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
          />
          <button className="btn primary" onClick={handlePasswordChange}>Zmień hasło</button>
        </div>
      </div>
    </div>
  );
}
