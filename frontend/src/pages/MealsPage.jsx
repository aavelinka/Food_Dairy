import { useEffect, useState } from 'react';
import { getErrorMessage } from '../api/client';
import { mealsApi, productsApi, usersApi } from '../api/services';
import { ActionIconButton, ActionIconLink } from '../components/ActionIconButton';
import { DataTable } from '../components/DataTable';
import { Panel } from '../components/Panel';
import { ProductSelector } from '../components/ProductSelector';
import { StatusBanner } from '../components/StatusBanner';
import {
  emptyNutritionForm,
  findEntityNameById,
  formatDate,
  formatGoalType,
  nutritionFormFromValue,
  nutritionPayloadFromForm,
  parseCommaSeparatedIds,
  parseInteger,
  todayIso,
  toggleId,
} from '../utils/foodDiary';

function emptyMealForm() {
  return {
    name: '',
    date: todayIso(),
    authorId: '',
    productIds: [],
    nutrition: emptyNutritionForm(),
  };
}

function mealPayloadFromForm(form) {
  return {
    name: form.name.trim(),
    date: form.date,
    authorId: parseInteger(form.authorId),
    productIds: form.productIds,
    totalNutritional: nutritionPayloadFromForm(form.nutrition),
  };
}

export function MealsPage() {
  const [meals, setMeals] = useState([]);
  const [users, setUsers] = useState([]);
  const [products, setProducts] = useState([]);
  const [searchValue, setSearchValue] = useState('');
  const [filterAuthorId, setFilterAuthorId] = useState('');
  const [filterProductIds, setFilterProductIds] = useState('');
  const [showFilters, setShowFilters] = useState(false);
  const [showForm, setShowForm] = useState(true);
  const [mealForm, setMealForm] = useState(emptyMealForm());
  const [editingId, setEditingId] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [formError, setFormError] = useState('');
  const [formSuccess, setFormSuccess] = useState('');

  async function loadReferenceData() {
    try {
      const [allUsers, productsData] = await Promise.all([usersApi.getAll(), productsApi.getAll()]);
      setUsers(allUsers);
      setProducts(productsData);
    } catch {
      setUsers([]);
      setProducts([]);
    }
  }

  async function loadMeals() {
    setLoading(true);
    setError('');

    try {
      if (searchValue.trim()) {
        setMeals(await mealsApi.searchByName(searchValue.trim()));
      } else if (filterAuthorId) {
        setMeals(await mealsApi.searchByAuthor(Number(filterAuthorId)));
      } else if (filterProductIds.trim()) {
        const parsedProductIds = parseCommaSeparatedIds(filterProductIds);
        if (parsedProductIds.length > 0) {
          setMeals(await mealsApi.searchByProducts(parsedProductIds));
        } else {
          setMeals(await mealsApi.getAll());
        }
      } else {
        setMeals(await mealsApi.getAll());
      }
    } catch (requestError) {
      setError(getErrorMessage(requestError));
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadReferenceData();
  }, []);

  useEffect(() => {
    loadMeals();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [searchValue, filterAuthorId, filterProductIds]);

  function resetForm() {
    setEditingId(null);
    setMealForm(emptyMealForm());
    setFormError('');
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setFormError('');
    setFormSuccess('');

    try {
      const payload = mealPayloadFromForm(mealForm);
      if (editingId) {
        await mealsApi.update(editingId, payload);
        setFormSuccess('Прием пищи обновлен.');
      } else {
        await mealsApi.create(payload);
        setFormSuccess('Прием пищи добавлен.');
      }

      resetForm();
      await loadMeals();
    } catch (requestError) {
      setFormError(getErrorMessage(requestError));
    }
  }

  async function handleEdit(mealId) {
    setFormError('');
    setFormSuccess('');

    try {
      const meal = await mealsApi.getById(mealId);
      setEditingId(mealId);
      setMealForm({
        name: meal.name,
        date: meal.date ?? todayIso(),
        authorId: meal.authorId ?? '',
        productIds: meal.productIds ?? [],
        nutrition: nutritionFormFromValue(meal.totalNutritional),
      });
      setShowForm(true);
    } catch (requestError) {
      setFormError(getErrorMessage(requestError));
    }
  }

  async function handleDelete(mealId) {
    if (!window.confirm(`Удалить прием пищи #${mealId}?`)) {
      return;
    }

    try {
      await mealsApi.remove(mealId);
      if (editingId === mealId) {
        resetForm();
      }
      await loadMeals();
    } catch (requestError) {
      setError(getErrorMessage(requestError));
    }
  }

  function toggleProduct(productId) {
    setMealForm((current) => ({
      ...current,
      productIds: toggleId(current.productIds, productId),
    }));
  }

  return (
    <div className="page-stack">
      <div className="page-header">
        <div>
          <p className="page-kicker">Приемы пищи</p>
          <h1>Список приемов пищи</h1>
          <p className="page-subtitle">Глобальный список блюд с привязкой к пользователю и переходом в детальный просмотр.</p>
        </div>
        <div className="inline-actions">
          <button type="button" className="button" onClick={() => setShowForm((current) => !current)}>
            {showForm ? 'Скрыть форму' : 'Добавить прием пищи'}
          </button>
        </div>
      </div>

      <div className="toolbar-strip">
        <div className="toolbar-search-group">
          <label className="search-input">
            <input
              value={searchValue}
              onChange={(event) => setSearchValue(event.target.value)}
              placeholder="Найти прием пищи по названию"
            />
          </label>
          <button type="button" className="button-ghost" onClick={() => setShowFilters((current) => !current)}>
            {showFilters ? 'Скрыть фильтрацию' : 'Фильтрация'}
          </button>
          <div className="toolbar-count">Найдено приемов пищи: {meals.length}</div>
        </div>
      </div>

      {showFilters ? (
        <Panel title="Фильтры приемов пищи" description="Можно отфильтровать по пользователю или по списку идентификаторов продуктов.">
          <div className="form-grid">
            <div className="field">
              <label htmlFor="meal-filter-author">Пользователь</label>
              <select id="meal-filter-author" value={filterAuthorId} onChange={(event) => setFilterAuthorId(event.target.value)}>
                <option value="">Любой</option>
                {users.map((user) => (
                  <option key={user.id} value={user.id}>
                    {user.name} · {formatGoalType(user.goalType)}
                  </option>
                ))}
              </select>
            </div>
            <div className="field">
              <label htmlFor="meal-filter-products">ID продуктов</label>
              <input
                id="meal-filter-products"
                value={filterProductIds}
                onChange={(event) => setFilterProductIds(event.target.value)}
                placeholder="1,2,5"
              />
            </div>
          </div>
          <div className="inline-actions">
            <button
              type="button"
              className="button-ghost"
              onClick={() => {
                setSearchValue('');
                setFilterAuthorId('');
                setFilterProductIds('');
              }}
            >
              Сбросить фильтры
            </button>
          </div>
        </Panel>
      ) : null}

      <div className="content-grid content-grid-wide">
        <Panel title="Приемы пищи" description="В строке показываются название, автор и действия для просмотра и удаления.">
          <StatusBanner tone="error">{error}</StatusBanner>
          <StatusBanner tone="info">{loading ? 'Загружаю приемы пищи…' : ''}</StatusBanner>

          <DataTable
            columns={[
              { header: 'Название', render: (meal) => meal.name },
              { header: 'Пользователь', render: (meal) => (meal.authorId ? findEntityNameById(users, meal.authorId, 'Пользователь') : 'Не указан') },
              { header: 'Дата', render: (meal) => formatDate(meal.date) },
            ]}
            rows={meals}
            getRowKey={(meal) => meal.id}
            emptyMessage="Приемы пищи по текущему фильтру не найдены."
            actions={(meal) => (
              <>
                <ActionIconLink to={`/meals/${meal.id}`} icon="view" label={`Открыть прием пищи ${meal.name}`} />
                <ActionIconButton icon="edit" label={`Редактировать прием пищи ${meal.name}`} onClick={() => handleEdit(meal.id)} />
                <ActionIconButton icon="delete" tone="danger" label={`Удалить прием пищи ${meal.name}`} onClick={() => handleDelete(meal.id)} />
              </>
            )}
          />
        </Panel>

        {showForm ? (
          <Panel title={editingId ? 'Редактирование приема пищи' : 'Новый прием пищи'} description="Создание и редактирование блюда из глобального списка.">
            <StatusBanner tone="error">{formError}</StatusBanner>
            <StatusBanner tone="success">{formSuccess}</StatusBanner>

            <form className="page-stack" onSubmit={handleSubmit}>
              <div className="form-grid">
                <div className="field">
                  <label htmlFor="meal-name">Название</label>
                  <input
                    id="meal-name"
                    value={mealForm.name}
                    onChange={(event) => setMealForm((current) => ({ ...current, name: event.target.value }))}
                    required
                  />
                </div>
                <div className="field">
                  <label htmlFor="meal-date">Дата</label>
                  <input
                    id="meal-date"
                    type="date"
                    value={mealForm.date}
                    onChange={(event) => setMealForm((current) => ({ ...current, date: event.target.value }))}
                    required
                  />
                </div>
                <div className="field-full">
                  <label htmlFor="meal-author">Пользователь</label>
                  <select
                    id="meal-author"
                    value={mealForm.authorId}
                    onChange={(event) => setMealForm((current) => ({ ...current, authorId: event.target.value }))}
                  >
                    <option value="">Без автора</option>
                    {users.map((user) => (
                      <option key={user.id} value={user.id}>
                        {user.name}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="field-full">
                <label>Продукты</label>
                <ProductSelector products={products} selectedIds={mealForm.productIds} onToggle={toggleProduct} />
              </div>

              <div className="form-grid-3">
                <div className="field">
                  <label htmlFor="meal-calories">Калории</label>
                  <input
                    id="meal-calories"
                    type="number"
                    min="0"
                    step="0.1"
                    value={mealForm.nutrition.calories}
                    onChange={(event) =>
                      setMealForm((current) => ({
                        ...current,
                        nutrition: { ...current.nutrition, calories: event.target.value },
                      }))
                    }
                    required
                  />
                </div>
                <div className="field">
                  <label htmlFor="meal-proteins">Белки</label>
                  <input
                    id="meal-proteins"
                    type="number"
                    min="0"
                    step="0.1"
                    value={mealForm.nutrition.proteins}
                    onChange={(event) =>
                      setMealForm((current) => ({
                        ...current,
                        nutrition: { ...current.nutrition, proteins: event.target.value },
                      }))
                    }
                    required
                  />
                </div>
                <div className="field">
                  <label htmlFor="meal-fats">Жиры</label>
                  <input
                    id="meal-fats"
                    type="number"
                    min="0"
                    step="0.1"
                    value={mealForm.nutrition.fats}
                    onChange={(event) =>
                      setMealForm((current) => ({
                        ...current,
                        nutrition: { ...current.nutrition, fats: event.target.value },
                      }))
                    }
                    required
                  />
                </div>
                <div className="field">
                  <label htmlFor="meal-carbohydrates">Углеводы</label>
                  <input
                    id="meal-carbohydrates"
                    type="number"
                    min="0"
                    step="0.1"
                    value={mealForm.nutrition.carbohydrates}
                    onChange={(event) =>
                      setMealForm((current) => ({
                        ...current,
                        nutrition: { ...current.nutrition, carbohydrates: event.target.value },
                      }))
                    }
                    required
                  />
                </div>
              </div>

              <div className="inline-actions form-actions">
                <button type="submit" className="button">
                  {editingId ? 'Сохранить прием пищи' : 'Добавить прием пищи'}
                </button>
                {editingId ? (
                  <button type="button" className="button-ghost" onClick={resetForm}>
                    Отменить редактирование
                  </button>
                ) : null}
              </div>
            </form>
          </Panel>
        ) : null}
      </div>
    </div>
  );
}
