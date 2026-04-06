import { Link } from 'react-router-dom';

function EyeIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true" focusable="false">
      <path
        d="M1.5 12s3.8-6.5 10.5-6.5S22.5 12 22.5 12s-3.8 6.5-10.5 6.5S1.5 12 1.5 12Z"
        fill="none"
        stroke="currentColor"
        strokeLinecap="round"
        strokeLinejoin="round"
        strokeWidth="1.8"
      />
      <circle cx="12" cy="12" r="3.2" fill="none" stroke="currentColor" strokeWidth="1.8" />
    </svg>
  );
}

function PencilIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true" focusable="false">
      <path
        d="M4 20l3.5-.7L18.7 8.1a2.1 2.1 0 0 0 0-3L16.9 3.3a2.1 2.1 0 0 0-3 0L2.7 14.5 2 18Z"
        fill="none"
        stroke="currentColor"
        strokeLinecap="round"
        strokeLinejoin="round"
        strokeWidth="1.8"
      />
      <path d="M12.8 4.4 19.6 11.2" fill="none" stroke="currentColor" strokeLinecap="round" strokeWidth="1.8" />
    </svg>
  );
}

function TrashIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true" focusable="false">
      <path
        d="M4.5 7.5h15"
        fill="none"
        stroke="currentColor"
        strokeLinecap="round"
        strokeWidth="1.8"
      />
      <path
        d="M9 4.5h6"
        fill="none"
        stroke="currentColor"
        strokeLinecap="round"
        strokeWidth="1.8"
      />
      <path
        d="M7 7.5 7.8 19a2 2 0 0 0 2 1.9h4.4a2 2 0 0 0 2-1.9L17 7.5"
        fill="none"
        stroke="currentColor"
        strokeLinecap="round"
        strokeLinejoin="round"
        strokeWidth="1.8"
      />
      <path
        d="M10 10.5v6M14 10.5v6"
        fill="none"
        stroke="currentColor"
        strokeLinecap="round"
        strokeWidth="1.8"
      />
    </svg>
  );
}

const iconMap = {
  view: EyeIcon,
  edit: PencilIcon,
  delete: TrashIcon,
};

const toneMap = {
  secondary: 'button-secondary',
  ghost: 'button-ghost',
  danger: 'button-danger',
};

function ActionIcon({ icon }) {
  const IconComponent = iconMap[icon];
  return IconComponent ? <IconComponent /> : null;
}

function buildClassName(tone, className) {
  const baseClassName = `action-icon-button ${toneMap[tone] ?? toneMap.ghost}`;
  return className ? `${baseClassName} ${className}` : baseClassName;
}

export function ActionIconButton({ icon, label, tone = 'ghost', className, ...props }) {
  return (
    <button
      type="button"
      aria-label={label}
      title={label}
      className={buildClassName(tone, className)}
      {...props}
    >
      <ActionIcon icon={icon} />
    </button>
  );
}

export function ActionIconLink({ to, icon, label, tone = 'secondary', className, ...props }) {
  return (
    <Link
      to={to}
      aria-label={label}
      title={label}
      className={buildClassName(tone, className)}
      {...props}
    >
      <ActionIcon icon={icon} />
    </Link>
  );
}
