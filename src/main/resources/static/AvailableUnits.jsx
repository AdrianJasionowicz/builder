import React, { useEffect, useState } from 'react';
import api from './axiosInstance';

const AvailableUnits = ({ armyId, onAdd }) => {
  const [unitsByType, setUnitsByType] = useState({});
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!armyId) return;

    const fetchUnits = async () => {
      setLoading(true);
      setError(null);
      try {
        const res = await api.get(`/getAllUnits/${armyId}`);
        if (res.status === 204 || !res.data.length) {
          setUnitsByType({});
        } else {
          const grouped = res.data.reduce((acc, unit) => {
            if (!acc[unit.unitType]) acc[unit.unitType] = [];
            acc[unit.unitType].push(unit);
            return acc;
          }, {});
          setUnitsByType(grouped);
        }
      } catch (err) {
        console.error("Błąd fetchowania jednostek:", err);
        setError('Nie udało się pobrać jednostek.');
      } finally {
        setLoading(false);
      }
    };

    fetchUnits();
  }, [armyId]);

  if (!armyId) return <p>Wybierz armię, aby zobaczyć jednostki.</p>;
  if (loading) return <p>Ładowanie jednostek...</p>;
  if (error) return <p>{error}</p>;

  const unitOrder = ["Lords", "Hero", "Core", "Special", "Rare"];
  const orderedUnits = {};

  unitOrder.forEach(type => {
    if (unitsByType[type]) orderedUnits[type] = unitsByType[type];
  });

  return (
    <div className="column">
      <h2 className="section-title">Jednostki dostępne</h2>
      {Object.keys(orderedUnits).length === 0 ? (
        <p>Brak jednostek dla tej armii.</p>
      ) : (
        Object.entries(orderedUnits).map(([type, units]) => (
          <div key={type} className="unit-section">
            <h3>{type}</h3>
            <ul>
              {units.map(unit => (
                <li
                  key={unit.id}
                  onClick={() => onAdd(unit)}
                  className="unit-item cursor-pointer"
                >
                  <div>{unit.name}</div>
                  <div>{(unit.pointsCostPerUnit * unit.minQuantity).toLocaleString()} pkt</div>
                </li>
              ))}
            </ul>
          </div>
        ))
      )}
    </div>
  );
};

export default AvailableUnits;
