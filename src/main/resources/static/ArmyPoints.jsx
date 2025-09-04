import React, { useEffect, useState } from 'react';
import api from './axiosInstance';
import './ArmyPoints.css';

const ArmyPoints = ({ armyId, refreshTrigger }) => {
  const [pointsLimit, setPointsLimit] = useState(null);
  const [usedPoints, setUsedPoints] = useState(null);
  const [points, setPoints] = useState(2000);

  const order = [
    { key: 'Lords', label: 'Lords', color: '#ef4444' },
    { key: 'Heroes', label: 'Heroes', color: '#f59e0b' },
    { key: 'Core', label: 'Core', color: '#10b981' },
    { key: 'Special', label: 'Special', color: '#3b82f6' },
    { key: 'Rare', label: 'Rare', color: '#8b5cf6' }
  ];

  const keyMap = {
    Lords: 'Lords',
    Heroes: 'Hero',
    Core: 'Core',
    Special: 'Special',
    Rare: 'Rare'
  };

  const fetchUsedPoints = async () => {
    if (!armyId) return;
    try {
      const token = localStorage.getItem('jwtToken');
      const res = await api.get(`/usedPoints`, {
        params: { armyId },
        headers: { Authorization: `Bearer ${token}` }
      });
      setUsedPoints(res.data);
    } catch (err) { console.error(err); }
  };

  const fetchPointsLimit = async (newPoints) => {
    try {
      const token = localStorage.getItem('jwtToken');
      const res = await api.post(`/setPointsRestriction`, null, {
        params: { points: newPoints },
        headers: { Authorization: `Bearer ${token}` }
      });
      setPointsLimit(res.data);
    } catch (err) { console.error(err); }
  };

  useEffect(() => {
    fetchUsedPoints();
    fetchPointsLimit(points);
  }, [armyId, refreshTrigger, points]);

  const onApplySettings = async () => {
    await fetchPointsLimit(points);
    await fetchUsedPoints();
  };

  if (!pointsLimit || !usedPoints) return <p>Ładowanie danych armii...</p>;

  const totalUsed = Object.entries(usedPoints)
    .filter(([key]) => key !== 'Total')
    .reduce((a, [, b]) => a + b, 0);

  const totalLimit = pointsLimit.Total || 0;

  const chartData = order.map(({ key, label, color }) => ({
    label,
    value: usedPoints[keyMap[key]] || 0,
    color,
    percentage: totalUsed > 0 ? ((usedPoints[keyMap[key]] || 0) / totalUsed * 100).toFixed(1) : 0
  })).filter(item => item.value > 0);

  const getLimitColor = (used, limit, type) => {
    if (type === 'Core') {
      if (used >= limit) return 'army-points-normal';
      return 'army-points-core-warning';
    }
    if (!limit) return 'army-points-gray';
    const ratio = used / limit;
    if (ratio > 1) return 'army-points-over-limit';
    if (ratio >= 0.75) return 'army-points-warning';
    return 'army-points-normal';
  };

  const renderPieChart = () => {
    if (totalUsed === 0) return <div className="pie-chart-empty">Brak wydanych punktów</div>;
    let cumulativePercentage = 0;
    return (
      <div className="pie-chart-container">
        <h3 className="pie-chart-title">Rozkład punktów</h3>
        <div className="pie-chart">
          <svg viewBox="0 0 100 100" className="pie-svg">
            {chartData.map((item, index) => {
              const percentage = (item.value / totalUsed) * 100;
              const startAngle = cumulativePercentage * 3.6;
              cumulativePercentage += percentage;
              const endAngle = cumulativePercentage * 3.6;
              const largeArcFlag = percentage > 50 ? 1 : 0;
              const startX = 50 + 50 * Math.cos((startAngle - 90) * (Math.PI / 180));
              const startY = 50 + 50 * Math.sin((startAngle - 90) * (Math.PI / 180));
              const endX = 50 + 50 * Math.cos((endAngle - 90) * (Math.PI / 180));
              const endY = 50 + 50 * Math.sin((endAngle - 90) * (Math.PI / 180));
              return (
                <path
                  key={index}
                  d={`M50 50 L${startX} ${startY} A50 50 0 ${largeArcFlag} 1 ${endX} ${endY} Z`}
                  fill={item.color}
                  stroke="#fff"
                  strokeWidth="1"
                />
              );
            })}
            <circle cx="50" cy="50" r="35" fill="white" />
            <text x="50" y="50" textAnchor="middle" dy="0.3em" fontSize="12" fontWeight="bold">
              {totalUsed.toLocaleString()} pkt
            </text>
          </svg>
        </div>
        <div className="pie-legend">
          {chartData.map((item, index) => (
            <div key={index} className="legend-item">
              <div className="legend-color" style={{ backgroundColor: item.color }}></div>
              <span className="legend-label">{item.label}</span>
              <span className="legend-value">{item.percentage}%</span>
            </div>
          ))}
        </div>
      </div>
    );
  };

  return (
    <div className="space-y-4">
      <div className="p-4 border rounded-lg bg-white shadow">
        <h2 className="text-lg font-bold mb-3">Ustawienia</h2>
        <div className="flex gap-4 mb-4 items-end">
          <div>
            <label className="block text-sm font-semibold">Limit punktów</label>
            <input
              type="number"
              min="500"
              step="100"
              value={points}
              onChange={(e) => setPoints(Number(e.target.value))}
              className="border rounded px-2 py-1 w-28 text-right"
            />
          </div>
          <button onClick={onApplySettings} className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600">
            Zastosuj
          </button>
        </div>
        <table className="w-full text-sm mb-4">
          <tbody>
            {order.map(({ key, label }) => {
              const used = usedPoints[keyMap[key]] || 0;
              const limit = pointsLimit[keyMap[key]] || 0;
              const percentage = totalUsed > 0 ? ((used / totalUsed) * 100).toFixed(1) : 0;
              return (
                <tr key={key} className="border-t">
                  <td className="py-1">{label}</td>
                  <td className={`text-right py-1 ${getLimitColor(used, limit, key)}`}>
                    {used.toLocaleString()} / {limit.toLocaleString()} ({percentage}%)
                  </td>
                </tr>
              );
            })}
          </tbody>
          <tfoot>
            <tr className="border-t font-bold">
              <td className="py-2">Razem</td>
              <td className={`text-right py-2 ${getLimitColor(totalUsed, totalLimit)}`}>
                {totalUsed.toLocaleString()} / {totalLimit.toLocaleString()} (100%)
              </td>
            </tr>
          </tfoot>
        </table>
        {renderPieChart()}
      </div>
    </div>
  );
};

export default ArmyPoints;
