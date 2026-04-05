import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { getErrorMessage } from '../api/client';
import { mealsApi, notesApi, productsApi, usersApi } from '../api/services';
import { BackLink } from '../components/BackLink';
import { Panel } from '../components/Panel';
import { StatusBanner } from '../components/StatusBanner';
import { findEntityNameById, formatDate, formatGoalType, formatNutrition, formatWholeNumber, getProductEmoji, joinNotes } from '../utils/foodDiary';

export function MealDetailsPage() {
  const { mealId } = useParams();
  const [meal, setMeal] = useState(null);
  const [products, setProducts] = useState([]);
  const [author, setAuthor] = useState(null);
  const [note, setNote] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    async function loadMealDetails() {
      setLoading(true);
      setError('');

      try {
        const mealData = await mealsApi.getById(Number(mealId));
        const [productsData, noteData] = await Promise.all([productsApi.getAll(), notesApi.byMeal(Number(mealId))]);

        setMeal(mealData);
        setProducts(productsData);
        setNote(noteData[0] ?? null);

        if (mealData.authorId) {
          setAuthor(await usersApi.getById(mealData.authorId));
        } else {
          setAuthor(null);
        }
      } catch (requestError) {
        setError(getErrorMessage(requestError));
      } finally {
        setLoading(false);
      }
    }

    loadMealDetails();
  }, [mealId]);

  return (
    <div className="page-stack">
      <BackLink to="/meals">Ко всем приемам пищи</BackLink>

      <div className="page-header">
        <div>
          <p className="page-kicker">Прием пищи</p>
          <h1>{meal?.name || `Прием пищи #${mealId}`}</h1>
          <p className="page-subtitle">Подробный просмотр блюда, списка продуктов и описания.</p>
        </div>
        <div className="inline-actions">
          {meal?.authorId ? (
            <Link className="button-secondary" to={`/users/${meal.authorId}`}>
              К профилю пользователя
            </Link>
          ) : null}
        </div>
      </div>

      <StatusBanner tone="error">{error}</StatusBanner>
      <StatusBanner tone="info">{loading ? 'Загружаю информацию о приеме пищи…' : ''}</StatusBanner>

      {meal ? (
        <>
          <div className="stats-grid">
            <div className="stat-card">
              <span className="stat-label">Пользователь</span>
              <strong>{author?.name || 'Не указан'}</strong>
              <span className="stat-subtext">{author ? formatGoalType(author.goalType) : 'Без привязки к пользователю'}</span>
            </div>
            <div className="stat-card">
              <span className="stat-label">Дата</span>
              <strong>{formatDate(meal.date)}</strong>
              <span className="stat-subtext">Дата приема пищи</span>
            </div>
            <div className="stat-card">
              <span className="stat-label">Общее КБЖУ</span>
              <strong>{formatWholeNumber(meal.totalNutritional?.calories)}</strong>
              <span className="stat-subtext">{formatNutrition(meal.totalNutritional)}</span>
            </div>
          </div>

          <div className="content-grid">
            <Panel title="Продукты" description="Состав блюда по связанным продуктам.">
              <div className="entity-list">
                {meal.productIds.length === 0 ? (
                  <div className="empty-card">У этого приема пищи пока нет продуктов.</div>
                ) : (
                  meal.productIds.map((productId) => {
                    const product = products.find((item) => item.id === productId);

                    return (
                      <article className="entity-row" key={productId}>
                        <div className="entity-row-main">
                          <h3>
                            <span className="product-emoji" aria-hidden="true">
                              {getProductEmoji(product?.name)}
                            </span>{' '}
                            {findEntityNameById(products, productId, 'Продукт')}
                          </h3>
                          <p>{product ? formatNutrition(product.nutritionalValue100g) : 'Данные о продукте не загружены.'}</p>
                        </div>
                      </article>
                    );
                  })
                )}
              </div>
            </Panel>

            <Panel title="Описание" description="Текст рецепта или комментарий к блюду.">
              {note ? (
                <div className="note-body">
                  {joinNotes(note.notes)
                    .split('\n')
                    .filter(Boolean)
                    .map((line) => (
                      <p key={line}>{line}</p>
                    ))}
                </div>
              ) : (
                <div className="empty-card">Описание для этого блюда пока не добавлено.</div>
              )}
            </Panel>
          </div>
        </>
      ) : null}
    </div>
  );
}
