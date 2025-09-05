import React, { useState, useEffect } from 'react';
import './Header.css';

export default function Header({ onTemplateLoaded, refreshTemplates, army, onExportPdf, onOpenSettings }) {
  const [isTemplatesOpen, setIsTemplatesOpen] = useState(false);
  const [templates, setTemplates] = useState([]);

  const API_URL = "http://localhost:8080";

  const fetchTemplates = async () => {
    try {
      const res = await fetch(`${API_URL}/templates`, { credentials: 'include' });
      if (res.ok) setTemplates(await res.json());
    } catch (e) {
      console.error(e);
    }
  };

  useEffect(() => {
    if (isTemplatesOpen) fetchTemplates();
  }, [isTemplatesOpen, refreshTemplates]);

  const handleLogout = () => {
    document.cookie = 'token=; path=/; max-age=0';
    window.location.href = '/login';
  };

  const handleLoadTemplate = async (id) => {
    try {
      const templateRes = await fetch(`${API_URL}/template/${id}`, { credentials: 'include' });
      if (!templateRes.ok) {
        alert('Błąd wczytywania szablonu');
        return;
      }
      const templateData = await templateRes.json();

      const unitsRes = await fetch(`${API_URL}/army/${id}/units`, { credentials: 'include' });
      if (!unitsRes.ok) {
        alert('Błąd wczytywania jednostek');
        return;
      }
      const unitsData = await unitsRes.json();

      onTemplateLoaded && onTemplateLoaded({ ...templateData, units: unitsData });
      setIsTemplatesOpen(false);
      alert(`Wczytano szablon: ${templateData.name}`);
    } catch (e) {
      console.error(e);
      alert('Błąd wczytywania');
    }
  };

  return (
    <header className="header">
      <div className="header-left">
        <span className="logo-icon">⚔️</span>
        <h1 className="logo-text">Warhammer Army Builder</h1>
      </div>

      <div className="header-center">
        <button onClick={onExportPdf} className="btn export-btn">📄 Eksportuj PDF</button>

        <div className="dropdown">
          <button onClick={() => setIsTemplatesOpen(!isTemplatesOpen)} className="btn load-btn">📂 Wczytaj</button>
          {isTemplatesOpen && (
            <div className="dropdown-menu">
              {templates.length === 0 ? (
                <p>Brak szablonów</p>
              ) : (
                templates.map(t => (
                  <div
                    key={t.id}
                    className="template-item"
                    onClick={() => handleLoadTemplate(t.id)}
                  >
                    {t.name} • {t.factionName} • {t.pointsLimit} pkt
                  </div>
                ))
              )}
            </div>
          )}
        </div>
      </div>

      <div className="header-right">
        <button className="btn settings-btn" onClick={onOpenSettings}>⚙️ Ustawienia</button>
        <button className="btn logout-btn" onClick={handleLogout}>🚪 Wyloguj</button>
      </div>
    </header>
  );
}
