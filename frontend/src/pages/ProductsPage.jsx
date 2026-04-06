import { useEffect, useState } from 'react';
import { getErrorMessage } from '../api/client';
import { mealsApi, productsApi } from '../api/services';
import { ActionIconButton } from '../components/ActionIconButton';
import { DataTable } from '../components/DataTable';
import { Panel } from '../components/Panel';
import { StatusBanner } from '../components/StatusBanner';
import { emptyNutritionForm, formatWholeNumber, getProductEmoji, nutritionFormFromValue, nutritionPayloadFromForm } from '../utils/foodDiary';

function emptyProductForm() {
  return {
    name: '',
    nutrition: emptyNutritionForm(),
  };
}

function productPayloadFromForm(form) {
  return {
    name: form.name.trim(),
    nutritionalValue100g: nutritionPayloadFromForm(form.nutrition),
  };
}

export function ProductsPage() {
  const [products, setProducts] = useState([]);
  const [meals, setMeals] = useState([]);
  const [searchValue, setSearchValue] = useState('');
  const [filterMealId, setFilterMealId] = useState('');
  const [showFilters, setShowFilters] = useState(false);
  const [showForm, setShowForm] = useState(true);
  const [productForm, setProductForm] = useState(emptyProductForm());
  const [editingId, setEditingId] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [formError, setFormError] = useState('');
  const [formSuccess, setFormSuccess] = useState('');

  async function loadProducts() {
    setLoading(true);
    setError('');

    try {
      if (searchValue.trim()) {
        setProducts(await productsApi.searchByName(searchValue.trim()));
      } else if (filterMealId) {
        setProducts(await productsApi.searchByMeal(Number(filterMealId)));
      } else {
        setProducts(await productsApi.getAll());
      }
    } catch (requestError) {
      setError(getErrorMessage(requestError));
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    mealsApi
      .getAll()
      .then((data) => setMeals(data))
      .catch(() => setMeals([]));
  }, []);

  useEffect(() => {
    loadProducts();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [searchValue, filterMealId]);

  function resetForm() {
    setEditingId(null);
    setProductForm(emptyProductForm());
    setFormError('');
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setFormError('');
    setFormSuccess('');

    try {
      const payload = productPayloadFromForm(productForm);
      if (editingId) {
        await productsApi.update(editingId, payload);
        setFormSuccess('Продукт обновлен.');
      } else {
        await productsApi.create(payload);
        setFormSuccess('Продукт добавлен.');
      }

      resetForm();
      await loadProducts();
    } catch (requestError) {
      setFormError(getErrorMessage(requestError));
    }
  }

  async function handleEdit(productId) {
    setFormError('');
    setFormSuccess('');

    try {
      const product = await productsApi.getById(productId);
      setEditingId(productId);
      setProductForm({
        name: product.name,
        nutrition: nutritionFormFromValue(product.nutritionalValue100g),
      });
      setShowForm(true);
    } catch (requestError) {
      setFormError(getErrorMessage(requestError));
    }
  }

  async function handleDelete(productId) {
    if (!window.confirm(`Удалить продукт #${productId}?`)) {
      return;
    }

    try {
      await productsApi.remove(productId);
      if (editingId === productId) {
        resetForm();
      }
      await loadProducts();
    } catch (requestError) {
      setError(getErrorMessage(requestError));
    }
  }

  return (
    <div className="page-stack">
      <div className="page-header">
        <div>
          <p className="page-kicker">Продукты</p>
          <h1>Справочник продуктов</h1>
          <p className="page-subtitle">Таблица продуктов с КБЖУ и быстрыми действиями администратора.</p>
        </div>
        <div className="inline-actions">
          <button type="button" className="button" onClick={() => setShowForm((current) => !current)}>
            {showForm ? 'Скрыть форму' : 'Добавить продукт'}
          </button>
        </div>
      </div>

      <div className="toolbar-strip">
        <div className="toolbar-search-group">
          <label className="search-input">
            <input
              value={searchValue}
              onChange={(event) => setSearchValue(event.target.value)}
              placeholder="Найти продукт по названию"
            />
          </label>
          <button type="button" className="button-ghost" onClick={() => setShowFilters((current) => !current)}>
            {showFilters ? 'Скрыть фильтрацию' : 'Фильтрация'}
          </button>
          <div className="toolbar-count">Найдено продуктов: {products.length}</div>
        </div>
      </div>

      {showFilters ? (
        <Panel title="Фильтры продуктов" description="Дополнительная выборка по приему пищи, в котором используется продукт.">
          <div className="form-grid">
            <div className="field">
              <label htmlFor="product-meal-filter">Прием пищи</label>
              <select id="product-meal-filter" value={filterMealId} onChange={(event) => setFilterMealId(event.target.value)}>
                <option value="">Любой</option>
                {meals.map((meal) => (
                  <option key={meal.id} value={meal.id}>
                    {meal.name}
                  </option>
                ))}
              </select>
            </div>
          </div>
          <div className="inline-actions">
            <button
              type="button"
              className="button-ghost"
              onClick={() => {
                setFilterMealId('');
                setSearchValue('');
              }}
            >
              Сбросить фильтры
            </button>
          </div>
        </Panel>
      ) : null}

      <div className="content-grid content-grid-wide">
        <Panel title="Таблица продуктов" description="Название продукта, КБЖУ и действия для редактирования.">
          <StatusBanner tone="error">{error}</StatusBanner>
          <StatusBanner tone="info">{loading ? 'Загружаю продукты…' : ''}</StatusBanner>

          <DataTable
            columns={[
              {
                header: 'Название',
                render: (product) => (
                  <div className="product-name-cell">
                    <span className="product-emoji" aria-hidden="true">
                      {getProductEmoji(product.name)}
                    </span>
                    <span>{product.name}</span>
                  </div>
                ),
              },
              { header: 'К', render: (product) => formatWholeNumber(product.nutritionalValue100g?.calories) },
              { header: 'Б', render: (product) => formatWholeNumber(product.nutritionalValue100g?.proteins) },
              { header: 'Ж', render: (product) => formatWholeNumber(product.nutritionalValue100g?.fats) },
              { header: 'У', render: (product) => formatWholeNumber(product.nutritionalValue100g?.carbohydrates) },
            ]}
            rows={products}
            getRowKey={(product) => product.id}
            emptyMessage="По текущему фильтру продукты не найдены."
            actions={(product) => (
              <>
                <ActionIconButton icon="edit" label={`Редактировать продукт ${product.name}`} onClick={() => handleEdit(product.id)} />
                <ActionIconButton icon="delete" tone="danger" label={`Удалить продукт ${product.name}`} onClick={() => handleDelete(product.id)} />
              </>
            )}
          />
        </Panel>

        {showForm ? (
          <Panel title={editingId ? 'Редактирование продукта' : 'Новый продукт'} description="Поля КБЖУ задаются на 100 грамм продукта.">
            <StatusBanner tone="error">{formError}</StatusBanner>
            <StatusBanner tone="success">{formSuccess}</StatusBanner>

            <form className="page-stack" onSubmit={handleSubmit}>
              <div className="field">
                <label htmlFor="product-name">Название</label>
                <input
                  id="product-name"
                  value={productForm.name}
                  onChange={(event) => setProductForm((current) => ({ ...current, name: event.target.value }))}
                  required
                />
              </div>

              <div className="form-grid-3">
                <div className="field">
                  <label htmlFor="product-calories">Калории</label>
                  <input
                    id="product-calories"
                    type="number"
                    min="0"
                    step="0.1"
                    value={productForm.nutrition.calories}
                    onChange={(event) =>
                      setProductForm((current) => ({
                        ...current,
                        nutrition: { ...current.nutrition, calories: event.target.value },
                      }))
                    }
                    required
                  />
                </div>
                <div className="field">
                  <label htmlFor="product-proteins">Белки</label>
                  <input
                    id="product-proteins"
                    type="number"
                    min="0"
                    step="0.1"
                    value={productForm.nutrition.proteins}
                    onChange={(event) =>
                      setProductForm((current) => ({
                        ...current,
                        nutrition: { ...current.nutrition, proteins: event.target.value },
                      }))
                    }
                    required
                  />
                </div>
                <div className="field">
                  <label htmlFor="product-fats">Жиры</label>
                  <input
                    id="product-fats"
                    type="number"
                    min="0"
                    step="0.1"
                    value={productForm.nutrition.fats}
                    onChange={(event) =>
                      setProductForm((current) => ({
                        ...current,
                        nutrition: { ...current.nutrition, fats: event.target.value },
                      }))
                    }
                    required
                  />
                </div>
                <div className="field">
                  <label htmlFor="product-carbs">Углеводы</label>
                  <input
                    id="product-carbs"
                    type="number"
                    min="0"
                    step="0.1"
                    value={productForm.nutrition.carbohydrates}
                    onChange={(event) =>
                      setProductForm((current) => ({
                        ...current,
                        nutrition: { ...current.nutrition, carbohydrates: event.target.value },
                      }))
                    }
                    required
                  />
                </div>
              </div>

              <div className="inline-actions">
                <button type="submit" className="button">
                  {editingId ? 'Сохранить продукт' : 'Добавить продукт'}
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
