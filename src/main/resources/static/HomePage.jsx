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
  const [army, setArmy] = useState(null);
  const [selectedUnit, setSelectedUnit] = useState(null);
  const [refreshTrigger, setRefreshTrigger] = useState(0);
  const [templateUnits, setTemplateUnits] = useState(null);

  const handleArmySelected = (selectedArmy) => {
    setArmy(selectedArmy);
    setSelectedNation(selectedArmy.faction);
    setTemplateUnits(null);
  };

  const handleAddUnit = async (unit) => {
    const token = localStorage.getItem("jwtToken");
    await api.post(`/army/${army.id}/addUnit/${unit.id}`, null, {
      headers: { Authorization: `Bearer ${token}` },
    });
    setRefreshTrigger(prev => prev + 1);
  };

  const handleRemoveUnit = async (unitId) => {
    const token = localStorage.getItem("jwtToken");
    await api.delete(`/army/${army.id}/units/${unitId}`, {
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
    setArmy(templateData);
    setSelectedNation(templateData.factionName);
    setTemplateUnits(templateData.units);
    setSelectedUnit(null);
    setRefreshTrigger(prev => prev + 1);
  };

  return (
    <div>
      <Header army={army} onTemplateLoaded={handleTemplateLoaded} />
      {!army ? (
        <ArmySelector onArmySelected={handleArmySelected} />
      ) : (
        <div className="page-container">
          <div className="column">
            <AvailableUnits armyId={army.id} onAdd={handleAddUnit} />
          </div>
          <div className="column">
            <SelectedUnits
              armyId={army.id}
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
              <ArmyPoints armyId={army.id} refreshTrigger={refreshTrigger} />
              {selectedUnit && (
                <UpgradePanel
                  armyId={army.id}
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
