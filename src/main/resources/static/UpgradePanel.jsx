import React, { useEffect, useState } from "react";
import api from "./axiosInstance";
import "./UpgradePanel.css";

export default function UpgradePanel({ armyId, selectedUnit, onUpgradeChange }) {
  const [upgrades, setUpgrades] = useState([]);
  const [loading, setLoading] = useState(false);

  const fetchUpgrades = async () => {
    if (!selectedUnit) return;
    try {
      setLoading(true);
      const token = localStorage.getItem("jwtToken");
      const res = await api.get(`/army/${armyId}/getUpgrades/${selectedUnit.id}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setUpgrades(res.data);
    } catch (err) {
      setUpgrades([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUpgrades();
  }, [armyId, selectedUnit]);

  const handleAddUpgrade = async (upgradeId) => {
    const token = localStorage.getItem("jwtToken");
    await api.post(`/addUpgrade`, null, {
      params: { upgradeId },
      headers: { Authorization: `Bearer ${token}` },
    });
    if (onUpgradeChange) onUpgradeChange();
    fetchUpgrades();
  };

  const handleRemoveUpgrade = async (upgradeId) => {
    const token = localStorage.getItem("jwtToken");
    await api.post(`/removeSelectedUpgrade`, null, {
      params: { upgradeId },
      headers: { Authorization: `Bearer ${token}` },
    });
    if (onUpgradeChange) onUpgradeChange();
    fetchUpgrades();
  };

  if (!selectedUnit) return <p>Wybierz jednostkę, aby zobaczyć ulepszenia.</p>;
  if (loading) return <p>Ładowanie ulepszeń...</p>;
  if (upgrades.length === 0) return <p>Brak ulepszeń dla tej jednostki.</p>;

  return (
    <div className="upgrade-panel">
      <h3>Ulepszenia: {selectedUnit.unit?.name}</h3>
      <table className="upgrade-table">
        <thead>
          <tr>
            <th>Status</th>
            <th>Nazwa ulepszenia</th>
            <th>Koszt</th>
            <th>Akcje</th>
          </tr>
        </thead>
        <tbody>
          {upgrades.map((upg) => (
            <tr key={upg.id}>
              <td style={{ textAlign: "center" }}>{upg.selected ? "✅" : "❌"}</td>
              <td>{upg.name}</td>
              <td>{upg.pointsCost} pkt</td>
              <td>
                {!upg.selected ? (
                  <button onClick={() => handleAddUpgrade(upg.id)}>Dodaj</button>
                ) : (
                  <button onClick={() => handleRemoveUpgrade(upg.id)}>Usuń</button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
