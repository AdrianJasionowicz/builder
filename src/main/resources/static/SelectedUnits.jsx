import React, { useEffect, useState } from "react";
import api from "./axiosInstance";
import "./SelectedUnits.css";

export default function SelectedUnits({ armyId, refreshTrigger, onIncrease, onDecrease, onRemove, onShowUpgrades }) {
  const [selectedUnits, setSelectedUnits] = useState([]);
  const [loading, setLoading] = useState(false);

  const fetchSelectedUnits = async () => {
    if (!armyId) return;
    try {
      setLoading(true);
      const token = localStorage.getItem("jwtToken");
      const response = await api.get(`/army/${armyId}/units`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setSelectedUnits(response.data);
    } catch (error) {
      console.error("Błąd ładowania jednostek:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSelectedUnits();
  }, [armyId, refreshTrigger]);

  if (!armyId) return <p>Wybierz armię, aby zobaczyć wybrane jednostki.</p>;
  if (loading) return <p>Ładowanie wybranych jednostek...</p>;

  return (
    <div className="selected-units">
      <h2>Wybrane jednostki</h2>
      {selectedUnits.length === 0 ? (
        <p>Brak wybranych jednostek</p>
      ) : (
        <table className="selected-table">
          <thead>
            <tr>
              <th>Jednostka</th>
              <th>Koszt</th>
              <th>Ilość</th>
              <th>Akcje</th>
              <th>Ulepszenia</th>
            </tr>
          </thead>
          <tbody>
            {selectedUnits.map(unit => (
              <tr key={unit.id}>
                <td>{unit.unit?.name || "Brak nazwy"}</td>
                <td>{unit.totalCost ?? 0} pkt</td>
                <td>{unit.quantity}</td>
                <td className="actions">
                  <button
                    onClick={() => onIncrease(unit.id)}
                    className={unit.quantity === 1 ? "btn-disabled" : "btn-increase"}
                  >
                    +
                  </button>
                  <button
                    onClick={() => onDecrease(unit.id)}
                    className={unit.quantity === 1 ? "btn-disabled" : "btn-decrease"}
                  >
                    -
                  </button>
                  <button onClick={() => onRemove(unit.id)} className="btn-remove">
                    Usuń
                  </button>
                </td>
                <td>
                  <button
                    onClick={() => onShowUpgrades(unit)}
                    className="btn-show-upgrades"
                  >
                    Pokaż ulepszenia
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
