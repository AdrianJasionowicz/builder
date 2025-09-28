import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./Header.css";

export default function Header({ onTemplateLoaded, refreshTemplates, army }) {
  const [isTemplatesOpen, setIsTemplatesOpen] = useState(false);
  const [templates, setTemplates] = useState([]);
  const navigate = useNavigate();
  const API_URL = "http://localhost:8080";

  const fetchTemplates = async () => {
    try {
      const res = await fetch(`${API_URL}/templates`, {
        credentials: "include"
      });
      if (res.ok) setTemplates(await res.json());
    } catch (e) {
      console.error(e);
    }
  };

  useEffect(() => {
    if (isTemplatesOpen) fetchTemplates();
  }, [isTemplatesOpen, refreshTemplates]);

  const handleLogout = () => {
    document.cookie = "token=; path=/; max-age=0";
    navigate("/login");
  };

  const handleSettings = () => {
    navigate("/settings");
  };

  const handleExportPdf = async () => {
    if (!army) {
      alert("Najpierw wybierz armię!");
      return;
    }

    const armyName = prompt("Podaj nazwę pliku PDF:", army.name || "army_export");
    if (!armyName) return;

    try {
      const res = await fetch(`${API_URL}/exportPdf`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Accept": "application/pdf"
        },
        credentials: "include",
        body: JSON.stringify({ armyName, armyId: army.id })
      });

      if (!res.ok) {
        const text = await res.text();
        console.error("Błąd backendu:", text);
        throw new Error("Błąd pobierania PDF");
      }

      const blob = await res.blob();
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;
      link.download = `${armyName}.pdf`;
      link.click();
      window.URL.revokeObjectURL(url);
    } catch (e) {
      console.error(e);
      alert("Nie udało się wygenerować PDF");
    }
  };

  return (
    <header className="header">
      <div className="header-left">
        <span className="logo-icon">⚔️</span>
        <h1 className="logo-text">Warhammer Army Builder</h1>
      </div>
      <div className="header-center">
        <button onClick={handleExportPdf} className="btn export-btn">📄 Eksportuj PDF</button>
        <div className="dropdown">
          <button onClick={() => setIsTemplatesOpen(!isTemplatesOpen)} className="btn load-btn">📂 Wczytaj</button>
          {isTemplatesOpen && (
            <div className="dropdown-menu">
              {templates.length === 0 ? <p>Brak szablonów</p> :
                templates.map(t => (
                  <div key={t.id} className="template-item" onClick={() => onTemplateLoaded(t)}>
                    {t.name} • {t.factionName} • {t.pointsLimit} pkt
                  </div>
                ))
              }
            </div>
          )}
        </div>
      </div>
      <div className="header-right">
        <button className="btn settings-btn" onClick={handleSettings}>⚙️ Ustawienia</button>
        <button className="btn logout-btn" onClick={handleLogout}>🚪 Wyloguj</button>
      </div>
    </header>
  );
}
