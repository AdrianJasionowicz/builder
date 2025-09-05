import React, { useEffect, useState } from 'react';
import api from './axiosInstance';
import './ArmySelector.css';

export default function ArmySelector({ onArmySelected }) {
  const [templates, setTemplates] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(true);
  const [newArmyName, setNewArmyName] = useState('');
  const [points, setPoints] = useState(2000);
  const [faction, setFaction] = useState('');

  const factions = [
    "Skaven", "Vampire Counts", "High Elves", "Dark Elves", "Lizardmen",
    "Bretonnia", "Empire", "Dwarfs", "Orcs & Goblins", "Chaos",
    "Tomb Kings", "Wood Elves", "Chaos Dwarfs"
  ];

  useEffect(() => {
    fetchTemplates();
  }, []);

  const fetchTemplates = async () => {
    try {
      const token = localStorage.getItem("jwtToken");
      const res = await api.get("/templates", {
        headers: { Authorization: `Bearer ${token}` }
      });
      setTemplates(res.data);
    } catch (err) {
      console.error("Błąd fetchowania szablonów:", err);
    } finally {
      setLoading(false);
    }
  };

  // Funkcja do załadowania istniejącej armii
  const handleSelectArmy = (army) => {
    onArmySelected({
      id: army.id,           // armyId potrzebne do pobrania jednostek
      name: army.name,
      points: army.pointsLimit,
      faction: army.faction
    });
    setShowModal(false);
  };

  // Funkcja do stworzenia nowej armii
  const handleCreateArmy = async () => {
    if (!newArmyName || !faction) {
      alert("Podaj nazwę armii i wybierz frakcję!");
      return;
    }
    try {
      const token = localStorage.getItem("jwtToken");
      const res = await api.post(
        `/army/create?name=${encodeURIComponent(newArmyName)}&faction=${encodeURIComponent(faction)}&points=${points}`,
        null,
        { headers: { Authorization: `Bearer ${token}` } }
      );

      // res.data to już armyId (Long z backendu)
      const armyId = res.data;

      onArmySelected({
        id: armyId,
        name: newArmyName,
        points,
        faction
      });

      setShowModal(false);
    } catch (err) {
      console.error("Błąd podczas tworzenia armii:", err);
      alert("Nie udało się stworzyć armii.");
    }
  };

  // Funkcja do usuwania szablonu
  const handleDeleteArmy = async (id) => {
    if (!window.confirm("Na pewno chcesz usunąć tę armię?")) return;
    try {
      const token = localStorage.getItem("jwtToken");
      await api.delete(`/template/${id}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      fetchTemplates();
    } catch (err) {
      console.error("Błąd podczas usuwania armii:", err);
      alert("Nie udało się usunąć armii.");
    }
  };

  if (!showModal) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-box">
        <div className="modal-left">
          <h2>Stwórz nową armię</h2>
          <label>Wybierz frakcję:</label>
          <select value={faction} onChange={e => setFaction(e.target.value)}>
            <option value="">-- Wybierz frakcję --</option>
            {factions.map(f => (
              <option key={f} value={f}>{f}</option>
            ))}
          </select>
          <input
            type="text"
            placeholder="Nazwa armii"
            value={newArmyName}
            onChange={e => setNewArmyName(e.target.value)}
          />
          <input
            type="number"
            placeholder="Punkty"
            value={points}
            onChange={e => setPoints(Number(e.target.value))}
          />
          <button onClick={handleCreateArmy}>➕ Utwórz Armię</button>
        </div>

        <div className="modal-right">
          <h2>Zapisane armie</h2>
          {loading ? (
            <p>Ładowanie...</p>
          ) : templates.length === 0 ? (
            <p>Brak zapisanych armii</p>
          ) : (
            <div className="army-list">
              {templates.map(army => (
                <div key={army.id} className="army-item">
                  <span>{army.name} ({army.pointsLimit} pkt, {army.faction})</span>
                  <div className="army-actions">
                    <button onClick={() => handleSelectArmy(army)}>Załaduj</button>
                    <button onClick={() => handleDeleteArmy(army.id)}>Usuń</button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
