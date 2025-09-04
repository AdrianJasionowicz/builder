import React, { useState } from "react";
import Header from "./Header";
import AvailableUnits from "./AvailableUnits";
import SelectedUnits from "./SelectedUnits";
import UpgradePanel from "./UpgradePanel";
import ArmyPoints from "./ArmyPoints";
import ArmySelector from "./ArmySelector";
import api from "./axiosInstance";
import "./HomePage.css";

export default function HomePage() {
  const [selectedNation, setSelectedNation] = useState("");
  const [armyId, setArmyId] = useState(null);
  const [selectedUnit, setSelectedUnit] = useState(null);
  const [refreshTrigger, setRefreshTrigger] = useState(0);
  const [templateUnits, setTemplateUnits] = useState(null);

  const handleArmySelected = ({ id, faction }) => {
    setArmyId(id);
    setSelectedNation(faction);
    setTemplateUnits(null);
  };

  const handleAddUnit = async (unit) => {
    const token = localStorage.getItem("jwtToken");
    await api.post(`/army/${armyId}/addUnit/${unit.id}`, null, {
      headers: { Authorization: `Bearer ${token}` },
    });
    setRefreshTrigger(prev => prev + 1);
  };

  const handleRemoveUnit = async (unitId) => {
    const token = localStorage.getItem("jwtToken");
    await api.delete(`/army/${armyId}/units/${unitId}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    setRefreshTrigger(prev => prev + 1);
    if (selectedUnit?.id === unitId) setSelectedUnit(null);
  };

  const handleIncrease = async (unitId) => {
    const token = localStorage.getItem("jwtToken");
    await api.post(`/increaseUnitQuantity?id=${unitId}`, null, {
      headers: { Authorization: `Bearer ${token}` },
    });
    setRefreshTrigger(prev => prev + 1);
  };

  const handleDecrease = async (unitId) => {
    const token = localStorage.getItem("jwtToken");
    await api.post(`/decreaseUnitQuantity?id=${unitId}`, null, {
      headers: { Authorization: `Bearer ${token}` },
    });
    setRefreshTrigger(prev => prev + 1);
  };

  const handleShowUpgrades = (unit) => setSelectedUnit(unit);

  const handleUpgradeChange = () => setRefreshTrigger(prev => prev + 1);

  const handleTemplateLoaded = (templateData) => {
    setArmyId(templateData.id);
    setSelectedNation(templateData.factionName);
    setTemplateUnits(templateData.units);
    setSelectedUnit(null);
    setRefreshTrigger(prev => prev + 1);
  };

  return (
    <div>
      <Header onTemplateLoaded={handleTemplateLoaded} />
      {!armyId ? (
        <ArmySelector onArmySelected={handleArmySelected} />
      ) : (
        <div className="page-container">
          <div className="column">
            <AvailableUnits armyId={armyId} onAdd={handleAddUnit} />
          </div>
          <div className="column">
            <SelectedUnits
              armyId={armyId}
              refreshTrigger={refreshTrigger}
              onRemove={handleRemoveUnit}
              onIncrease={handleIncrease}
              onDecrease={handleDecrease}
              onShowUpgrades={handleShowUpgrades}
              templateUnits={templateUnits}
            />
          </div>
          <div className="column">
            <div className="right-panel">
              <ArmyPoints armyId={armyId} refreshTrigger={refreshTrigger} />
              {selectedUnit && (
                <UpgradePanel
                  armyId={armyId}
                  selectedUnit={selectedUnit}
                  onUpgradeChange={handleUpgradeChange}
                />
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
