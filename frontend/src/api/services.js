import { buildQuery, fetchJson } from './client';

async function fetchAllUserPages(params = {}) {
  const firstPage = await fetchJson(`/users${buildQuery({ page: 0, size: 10, sort: 'id,asc', ...params })}`);
  const users = [...firstPage.content];

  for (let pageIndex = 1; pageIndex < firstPage.totalPages; pageIndex += 1) {
    const page = await fetchJson(`/users${buildQuery({ page: pageIndex, size: 10, sort: 'id,asc', ...params })}`);
    users.push(...page.content);
  }

  return users;
}

export const usersApi = {
  getAll: (params = {}) => fetchAllUserPages(params),
  getById: (id) => fetchJson(`/users/${id}`),
  create: (payload) => fetchJson('/users', { method: 'POST', body: payload }),
  update: (id, payload) => fetchJson(`/users/${id}`, { method: 'PUT', body: payload }),
  remove: (id) => fetchJson(`/users/${id}`, { method: 'DELETE' }),
};

export const productsApi = {
  getAll: () => fetchJson('/products'),
  getById: (id) => fetchJson(`/products/${id}`),
  searchByName: (nameSearch) => fetchJson(`/products/name${buildQuery({ nameSearch })}`),
  searchByMeal: (mealId) => fetchJson(`/products/meal_list${buildQuery({ mealId })}`),
  create: (payload) => fetchJson('/products', { method: 'POST', body: payload }),
  update: (id, payload) => fetchJson(`/products/${id}`, { method: 'PUT', body: payload }),
  remove: (id) => fetchJson(`/products/${id}`, { method: 'DELETE' }),
};

export const mealsApi = {
  getAll: () => fetchJson('/meal'),
  getById: (id) => fetchJson(`/meal/${id}`),
  searchByName: (nameSearch) => fetchJson(`/meal/name${buildQuery({ nameSearch })}`),
  searchByAuthor: (authorId) => fetchJson(`/meal/author${buildQuery({ authorId })}`),
  searchByProducts: (productIds) => fetchJson(`/meal/product_list${buildQuery({ productIds })}`),
  create: (payload) => fetchJson('/meal', { method: 'POST', body: payload }),
  update: (id, payload) => fetchJson(`/meal/${id}`, { method: 'PUT', body: payload }),
  remove: (id) => fetchJson(`/meal/${id}`, { method: 'DELETE' }),
};

export const bodyParametersApi = {
  byUser: (userId) => fetchJson(`/body-parameters/user${buildQuery({ userId })}`),
  create: (payload) => fetchJson('/body-parameters', { method: 'POST', body: payload }),
  calculateGoal: (userId) => fetchJson(`/body-parameters/user/${userId}/nutritional`, { method: 'POST' }),
  setManualGoal: (id, payload) =>
    fetchJson(`/body-parameters/${id}/nutritional/manual`, { method: 'PUT', body: payload }),
};

export const waterApi = {
  getById: (id) => fetchJson(`/water-intakes/${id}`),
  byUser: (userId) => fetchJson(`/water-intakes/user${buildQuery({ userId })}`),
  create: (payload) => fetchJson('/water-intakes', { method: 'POST', body: payload }),
  update: (id, payload) => fetchJson(`/water-intakes/${id}`, { method: 'PUT', body: payload }),
  remove: (id) => fetchJson(`/water-intakes/${id}`, { method: 'DELETE' }),
};

export const notesApi = {
  byMeal: (mealId) => fetchJson(`/note/meal_note${buildQuery({ mealId })}`),
};
