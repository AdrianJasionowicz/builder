import React, { useState } from 'react';

const ArmySettings = ({ onApply }) => {
  const [points, setPoints] = useState(2000);

  const handleApply = () => {
    onApply(points); 
  };

  return (
    <div className="p-4 border rounded bg-gray-50 mb-4 flex gap-4 items-end">
      <div>
        <label className="block text-sm font-semibold">Limit punktów</label>
        <input
          type="number"
          min="500"
          step="100"
          value={points}
          onChange={(e) => setPoints(Number(e.target.value))}
          className="border p-1 rounded w-24"
        />
      </div>

      <button
        onClick={handleApply}
        className="bg-blue-500 text-white px-3 py-1 rounded hover:bg-blue-600"
      >
        Zastosuj
      </button>
    </div>
  );
};

export default ArmySettings;