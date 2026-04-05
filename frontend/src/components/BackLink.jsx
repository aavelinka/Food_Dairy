import { Link } from 'react-router-dom';

export function BackLink({ to, children }) {
  return (
    <Link className="page-back-link" to={to}>
      <span className="page-back-link-icon" aria-hidden="true">
        &larr;
      </span>
      <span>{children}</span>
    </Link>
  );
}
