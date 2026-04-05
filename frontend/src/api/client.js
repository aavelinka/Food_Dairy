const API_ROOT = (import.meta.env.VITE_API_BASE_URL || '/api').replace(/\/$/, '');

export class ApiError extends Error {
  constructor(message, status, data) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.data = data;
  }
}

function buildQuery(params = {}) {
  const search = new URLSearchParams();

  Object.entries(params).forEach(([key, value]) => {
    if (value === undefined || value === null || value === '') {
      return;
    }

    if (Array.isArray(value)) {
      value
        .filter((item) => item !== undefined && item !== null && item !== '')
        .forEach((item) => search.append(key, String(item)));
      return;
    }

    search.set(key, String(value));
  });

  const queryString = search.toString();
  return queryString ? `?${queryString}` : '';
}

async function parseResponse(response) {
  if (response.status === 204) {
    return null;
  }

  const contentType = response.headers.get('content-type') ?? '';

  if (contentType.includes('application/json')) {
    return response.json();
  }

  return response.text();
}

export async function fetchJson(path, options = {}) {
  const headers = new Headers(options.headers ?? {});
  let body = options.body;

  if (body !== undefined && body !== null && !(body instanceof FormData) && typeof body !== 'string') {
    headers.set('Content-Type', 'application/json');
    body = JSON.stringify(body);
  }

  headers.set('Accept', 'application/json');

  const response = await fetch(`${API_ROOT}${path}`, {
    ...options,
    headers,
    body,
  });

  const data = await parseResponse(response);

  if (!response.ok) {
    const message = data?.message || `Request failed with status ${response.status}`;
    throw new ApiError(message, response.status, data);
  }

  return data;
}

export function getErrorMessage(error) {
  if (error instanceof ApiError) {
    const fieldErrors = Object.values(error.data?.fieldErrors ?? {});
    if (fieldErrors.length > 0) {
      return `${error.message}: ${fieldErrors.join('; ')}`;
    }

    return error.message;
  }

  if (error instanceof Error) {
    return error.message;
  }

  return 'Unexpected error';
}

export { buildQuery };
