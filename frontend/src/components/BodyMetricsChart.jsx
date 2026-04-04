import { sortByDateAscending } from '../utils/foodDiary';

const WEIGHT_SERIES = { key: 'weight', label: 'Вес', color: '#2f80ed' };

function buildPolyline(records, key, width, height, padding, minValue, maxValue) {
  const usableWidth = width - padding * 2;
  const usableHeight = height - padding * 2;
  const span = Math.max(maxValue - minValue, 1);

  return records
    .map((record, index) => {
      const value = record?.[key];
      if (value === null || value === undefined) {
        return null;
      }

      const x = padding + (records.length === 1 ? usableWidth / 2 : (usableWidth / (records.length - 1)) * index);
      const normalized = (value - minValue) / span;
      const y = height - padding - normalized * usableHeight;

      return `${x},${y}`;
    })
    .filter(Boolean)
    .join(' ');
}

function buildPoints(records, key, width, height, padding, minValue, maxValue) {
  const usableWidth = width - padding * 2;
  const usableHeight = height - padding * 2;
  const span = Math.max(maxValue - minValue, 1);

  return records
    .map((record, index) => {
      const value = record?.[key];
      if (value === null || value === undefined) {
        return null;
      }

      const x = padding + (records.length === 1 ? usableWidth / 2 : (usableWidth / (records.length - 1)) * index);
      const normalized = (value - minValue) / span;
      const y = height - padding - normalized * usableHeight;

      return { x, y, value };
    })
    .filter(Boolean);
}

export function BodyMetricsChart({ records }) {
  const sortedRecords = sortByDateAscending(records);
  const values = sortedRecords
    .map((record) => record?.[WEIGHT_SERIES.key])
    .filter((value) => value !== null && value !== undefined);

  if (sortedRecords.length === 0 || values.length === 0) {
    return <div className="chart-placeholder">История изменения веса пока пуста.</div>;
  }

  const width = 720;
  const height = 280;
  const padding = 32;
  const minValue = Math.min(...values);
  const maxValue = Math.max(...values);
  const polyline = buildPolyline(sortedRecords, WEIGHT_SERIES.key, width, height, padding, minValue, maxValue);
  const points = buildPoints(sortedRecords, WEIGHT_SERIES.key, width, height, padding, minValue, maxValue);

  return (
    <div className="chart-shell">
      <div className="chart-legend">
        <span className="legend-item">
          <span className="legend-dot" style={{ backgroundColor: WEIGHT_SERIES.color }} />
          {WEIGHT_SERIES.label}
        </span>
      </div>

      <svg className="metrics-chart" viewBox={`0 0 ${width} ${height}`} role="img" aria-label="График изменения веса">
        {[0, 1, 2, 3].map((step) => {
          const y = padding + ((height - padding * 2) / 3) * step;
          return <line key={step} x1={padding} x2={width - padding} y1={y} y2={y} className="chart-grid-line" />;
        })}

        <g>
          <polyline
            points={polyline}
            fill="none"
            stroke={WEIGHT_SERIES.color}
            strokeWidth="3"
            strokeLinecap="round"
            strokeLinejoin="round"
          />
          {points.map((point, index) => (
            <circle key={`${WEIGHT_SERIES.key}-${index}`} cx={point.x} cy={point.y} r="4" fill={WEIGHT_SERIES.color} />
          ))}
        </g>
      </svg>

      <div className="chart-footer">
        <span>{sortedRecords[0]?.recordDate}</span>
        <span>{sortedRecords[sortedRecords.length - 1]?.recordDate}</span>
      </div>
    </div>
  );
}
