export function StatusBanner({ tone = 'info', children }) {
  if (!children) {
    return null;
  }

  return <div className={`status-banner status-${tone}`}>{children}</div>;
}
