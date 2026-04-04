import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { getErrorMessage } from '../api/client';
import { bodyParametersApi, mealsApi, productsApi, usersApi, waterApi } from '../api/services';
import { BackLink } from '../components/BackLink';
import { BodyMetricsChart } from '../components/BodyMetricsChart';
import { Panel } from '../components/Panel';
import { ProductSelector } from '../components/ProductSelector';
import { StatusBanner } from '../components/StatusBanner';
import {
  GOAL_OPTIONS,
  buildInitials,
  emptyNutritionForm,
  formatAge,
  formatDate,
  formatGoalType,
  formatMl,
  formatWholeNumber,
  formatSex,
  nutritionFormFromValue,
  nutritionPayloadFromForm,
  parseDecimal,
  parseInteger,
  pickLatestRecord,
  todayIso,
  toggleId,
} from '../utils/foodDiary';

function buildProfileForm(user, latestMetrics) {
  return {
    name: user?.name ?? '',
    email: user?.email ?? '',
    password: '',
    goalType: user?.goalType ?? 'MAINTENANCE',
    recordDate: latestMetrics?.recordDate ?? todayIso(),
    sex: latestMetrics?.sex ?? 'FEMALE',
    weight: latestMetrics?.weight ?? '',
    height: latestMetrics?.height ?? '',
    age: latestMetrics?.age ?? '',
    chest: latestMetrics?.chest ?? '',
    waist: latestMetrics?.waist ?? '',
    hips: latestMetrics?.hips ?? '',
  };
}

function userPayloadFromForm(form) {
  return {
    name: form.name.trim(),
    email: form.email.trim(),
    password: form.password,
    goalType: form.goalType,
    measurements: {
      recordDate: form.recordDate,
      sex: form.sex,
      weight: parseDecimal(form.weight),
      height: parseDecimal(form.height),
      age: parseInteger(form.age),
      chest: parseDecimal(form.chest),
      waist: parseDecimal(form.waist),
      hips: parseDecimal(form.hips),
    },
  };
}

function buildMeasurementForm(userId, latestMetrics) {
  return {
    userId,
    recordDate: todayIso(),
    sex: latestMetrics?.sex ?? 'FEMALE',
    weight: latestMetrics?.weight ?? '',
    height: latestMetrics?.height ?? '',
    age: latestMetrics?.age ?? '',
    chest: latestMetrics?.chest ?? '',
    waist: latestMetrics?.waist ?? '',
    hips: latestMetrics?.hips ?? '',
  };
}

function measurementPayloadFromForm(form) {
  return {
    userId: parseInteger(form.userId),
    recordDate: form.recordDate,
    sex: form.sex,
    weight: parseDecimal(form.weight),
    height: parseDecimal(form.height),
    age: parseInteger(form.age),
    chest: parseDecimal(form.chest),
    waist: parseDecimal(form.waist),
    hips: parseDecimal(form.hips),
  };
}

function emptyMealForm() {
  return {
    name: '',
    date: todayIso(),
    productIds: [],
    nutrition: {
      calories: '',
      proteins: '',
      fats: '',
      carbohydrates: '',
    },
  };
}

function mealPayloadFromForm(form, authorId) {
  return {
    name: form.name.trim(),
    date: form.date,
    authorId,
    productIds: form.productIds,
    totalNutritional: nutritionPayloadFromForm(form.nutrition),
  };
}

function emptyWaterForm() {
  return {
    date: todayIso(),
    amountMl: '',
    drinkType: 'Вода',
    comment: '',
  };
}

function waterPayloadFromForm(form, userId) {
  return {
    userId,
    date: form.date,
    amountMl: parseInteger(form.amountMl),
    drinkType: form.drinkType.trim(),
    comment: form.comment.trim(),
  };
}

export function UserDashboardPage() {
  const { userId } = useParams();
  const navigate = useNavigate();
  const numericUserId = Number(userId);

  const [user, setUser] = useState(null);
  const [bodyHistory, setBodyHistory] = useState([]);
  const [meals, setMeals] = useState([]);
  const [waterEntries, setWaterEntries] = useState([]);
  const [products, setProducts] = useState([]);
  const [activeTab, setActiveTab] = useState('meals');
  const [showProfileForm, setShowProfileForm] = useState(false);
  const [showMeasurementForm, setShowMeasurementForm] = useState(false);
  const [showGoalForm, setShowGoalForm] = useState(false);
  const [profileForm, setProfileForm] = useState(buildProfileForm(null, null));
  const [measurementForm, setMeasurementForm] = useState(buildMeasurementForm(numericUserId, null));
  const [goalForm, setGoalForm] = useState(emptyNutritionForm());
  const [mealForm, setMealForm] = useState(emptyMealForm());
  const [editingMealId, setEditingMealId] = useState(null);
  const [waterForm, setWaterForm] = useState(emptyWaterForm());
  const [editingWaterId, setEditingWaterId] = useState(null);
  const [loading, setLoading] = useState(true);
  const [goalLoading, setGoalLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  async function loadProfile() {
    setLoading(true);
    setError('');

    try {
      const [userData, bodyData, mealsData, waterData, productsData] = await Promise.all([
        usersApi.getById(numericUserId),
        bodyParametersApi.byUser(numericUserId),
        mealsApi.searchByAuthor(numericUserId),
        waterApi.byUser(numericUserId),
        productsApi.getAll(),
      ]);

      const latestMetrics = pickLatestRecord(bodyData);

      setUser(userData);
      setBodyHistory(bodyData);
      setMeals(mealsData);
      setWaterEntries(waterData);
      setProducts(productsData);
      setProfileForm(buildProfileForm(userData, latestMetrics));
      setMeasurementForm(buildMeasurementForm(numericUserId, latestMetrics));
      setGoalForm(nutritionFormFromValue(latestMetrics?.dailyGoal));
    } catch (requestError) {
      setError(getErrorMessage(requestError));
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadProfile();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [numericUserId]);

  const latestMetrics = pickLatestRecord(bodyHistory);
  const profileInitials = buildInitials(user?.name);

  async function handleDeleteUser() {
    if (!window.confirm(`Удалить пользователя #${numericUserId}?`)) {
      return;
    }

    try {
      await usersApi.remove(numericUserId);
      navigate('/users');
    } catch (requestError) {
      setError(getErrorMessage(requestError));
    }
  }

  async function handleProfileUpdate(event) {
    event.preventDefault();
    setError('');
    setSuccess('');

    try {
      await usersApi.update(numericUserId, userPayloadFromForm(profileForm));
      setSuccess('Профиль пользователя обновлен.');
      setShowProfileForm(false);
      await loadProfile();
    } catch (requestError) {
      setError(getErrorMessage(requestError));
    }
  }

  async function handleMeasurementCreate(event) {
    event.preventDefault();
    setError('');
    setSuccess('');

    try {
      await bodyParametersApi.create(measurementPayloadFromForm(measurementForm));
      setSuccess('Новая запись параметров тела добавлена.');
      setShowMeasurementForm(false);
      await loadProfile();
    } catch (requestError) {
      setError(getErrorMessage(requestError));
    }
  }

  async function handleAutoGoalCalculation() {
    setGoalLoading(true);
    setError('');
    setSuccess('');

    try {
      const goal = await bodyParametersApi.calculateGoal(numericUserId);
      setGoalForm(nutritionFormFromValue(goal));
      setShowGoalForm(false);
      setSuccess('Целевое КБЖУ пересчитано по последним измерениям.');
      await loadProfile();
    } catch (requestError) {
      setError(getErrorMessage(requestError));
    } finally {
      setGoalLoading(false);
    }
  }

  async function handleManualGoalSubmit(event) {
    event.preventDefault();
    if (!latestMetrics?.id) {
      setError('Нет сохраненной записи измерений для ручной настройки КБЖУ.');
      return;
    }

    setGoalLoading(true);
    setError('');
    setSuccess('');

    try {
      const goal = await bodyParametersApi.setManualGoal(latestMetrics.id, nutritionPayloadFromForm(goalForm));
      setGoalForm(nutritionFormFromValue(goal));
      setShowGoalForm(false);
      setSuccess('Целевое КБЖУ сохранено вручную.');
      await loadProfile();
    } catch (requestError) {
      setError(getErrorMessage(requestError));
    } finally {
      setGoalLoading(false);
    }
  }

  async function handleMealSubmit(event) {
    event.preventDefault();
    setError('');
    setSuccess('');

    try {
      const payload = mealPayloadFromForm(mealForm, numericUserId);
      if (editingMealId) {
        await mealsApi.update(editingMealId, payload);
        setSuccess('Прием пищи обновлен.');
      } else {
        await mealsApi.create(payload);
        setSuccess('Прием пищи добавлен.');
      }

      setMealForm(emptyMealForm());
      setEditingMealId(null);
      await loadProfile();
    } catch (requestError) {
      setError(getErrorMessage(requestError));
    }
  }

  async function handleMealDelete(mealId) {
    if (!window.confirm(`Удалить прием пищи #${mealId}?`)) {
      return;
    }

    try {
      await mealsApi.remove(mealId);
      if (editingMealId === mealId) {
        setMealForm(emptyMealForm());
        setEditingMealId(null);
      }
      setSuccess('Прием пищи удален.');
      await loadProfile();
    } catch (requestError) {
      setError(getErrorMessage(requestError));
    }
  }

  async function handleMealEdit(mealId) {
    try {
      const meal = await mealsApi.getById(mealId);
      setEditingMealId(mealId);
      setMealForm({
        name: meal.name,
        date: meal.date ?? todayIso(),
        productIds: meal.productIds ?? [],
        nutrition: nutritionFormFromValue(meal.totalNutritional),
      });
    } catch (requestError) {
      setError(getErrorMessage(requestError));
    }
  }

  async function handleWaterSubmit(event) {
    event.preventDefault();
    setError('');
    setSuccess('');

    try {
      const payload = waterPayloadFromForm(waterForm, numericUserId);
      if (editingWaterId) {
        await waterApi.update(editingWaterId, payload);
        setSuccess('Запись о жидкости обновлена.');
      } else {
        await waterApi.create(payload);
        setSuccess('Запись о жидкости добавлена.');
      }

      setWaterForm(emptyWaterForm());
      setEditingWaterId(null);
      await loadProfile();
    } catch (requestError) {
      setError(getErrorMessage(requestError));
    }
  }

  async function handleWaterDelete(entryId) {
    if (!window.confirm(`Удалить запись о жидкости #${entryId}?`)) {
      return;
    }

    try {
      await waterApi.remove(entryId);
      if (editingWaterId === entryId) {
        setWaterForm(emptyWaterForm());
        setEditingWaterId(null);
      }
      setSuccess('Запись о жидкости удалена.');
      await loadProfile();
    } catch (requestError) {
      setError(getErrorMessage(requestError));
    }
  }

  async function handleWaterEdit(entryId) {
    try {
      const entry = await waterApi.getById(entryId);
      setEditingWaterId(entryId);
      setWaterForm({
        date: entry.date ?? todayIso(),
        amountMl: entry.amountMl ?? '',
        drinkType: entry.drinkType ?? 'Вода',
        comment: entry.comment ?? '',
      });
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
      <BackLink to="/users">К списку пользователей</BackLink>

      <div className="profile-top-grid">
        <div className="profile-hero">
          <div className="profile-hero-main">
            <div className="avatar-badge avatar-badge-large profile-avatar" aria-hidden="true">
              <span>{profileInitials}</span>
            </div>
            <div className="profile-identity">
              <p className="page-kicker">Профиль пользователя</p>
              <h1>{user?.name || `Пользователь #${numericUserId}`}</h1>

              <div className="profile-meta-list">
                <div className="profile-meta-item">
                  <span className="profile-meta-label">Цель</span>
                  <strong>{formatGoalType(user?.goalType)}</strong>
                </div>
                <div className="profile-meta-item">
                  <span className="profile-meta-label">Профиль</span>
                  <span>
                    {formatSex(latestMetrics?.sex)} · {formatAge(latestMetrics?.age)} · {user?.email || 'Почта не указана'}
                  </span>
                </div>
                <div className="profile-meta-item">
                  <span className="profile-meta-label">Последняя запись</span>
                  <span>{formatDate(latestMetrics?.recordDate)}</span>
                </div>
              </div>

              <div className="inline-actions profile-actions">
                <button type="button" className="button-secondary" onClick={() => setShowProfileForm((current) => !current)}>
                  {showProfileForm ? 'Скрыть редактирование' : 'Редактировать профиль'}
                </button>
                <button type="button" className="button-ghost" onClick={() => setShowMeasurementForm((current) => !current)}>
                  {showMeasurementForm ? 'Скрыть форму измерений' : 'Добавить измерение'}
                </button>
                <button type="button" className="button-danger" onClick={handleDeleteUser}>
                  Удалить пользователя
                </button>
              </div>
            </div>
          </div>
        </div>

        <Panel className="profile-metrics-panel" title="Параметры тела" description="Текущие показатели и динамика веса.">
          <div className="body-summary-strip">
            <div className="body-summary-date">
              <span className="stat-label">Последняя запись</span>
              <strong>{formatDate(latestMetrics?.recordDate)}</strong>
            </div>
            <div className="body-summary-details">
              <span>Вес: {latestMetrics?.weight ?? '—'} кг</span>
              <span>Рост: {latestMetrics?.height ?? '—'} см</span>
              <span>Талия: {latestMetrics?.waist ?? '—'} см</span>
              <span>Бедра: {latestMetrics?.hips ?? '—'} см</span>
              <span>Грудь: {latestMetrics?.chest ?? '—'} см</span>
            </div>
          </div>

          <div className="profile-chart-compact">
            <BodyMetricsChart records={bodyHistory} />
          </div>
        </Panel>
      </div>

      <StatusBanner tone="error">{error}</StatusBanner>
      <StatusBanner tone="success">{success}</StatusBanner>
      <StatusBanner tone="info">{loading ? 'Загружаю профиль пользователя…' : ''}</StatusBanner>

      {showProfileForm || showMeasurementForm ? (
        <div className="content-grid">
          {showProfileForm ? (
            <Panel title="Редактирование профиля" description="Обновление пользователя добавляет новую запись в историю параметров.">
              <form className="page-stack" onSubmit={handleProfileUpdate}>
                <div className="form-grid">
                  <div className="field">
                    <label htmlFor="profile-name">Имя</label>
                    <input
                      id="profile-name"
                      value={profileForm.name}
                      onChange={(event) => setProfileForm((current) => ({ ...current, name: event.target.value }))}
                      required
                    />
                  </div>
                  <div className="field">
                    <label htmlFor="profile-email">Почта</label>
                    <input
                      id="profile-email"
                      type="email"
                      value={profileForm.email}
                      onChange={(event) => setProfileForm((current) => ({ ...current, email: event.target.value }))}
                      required
                    />
                  </div>
                  <div className="field">
                    <label htmlFor="profile-password">Новый пароль</label>
                    <input
                      id="profile-password"
                      type="password"
                      minLength="6"
                      value={profileForm.password}
                      onChange={(event) => setProfileForm((current) => ({ ...current, password: event.target.value }))}
                      required
                    />
                  </div>
                  <div className="field">
                    <label htmlFor="profile-goal">Цель</label>
                    <select
                      id="profile-goal"
                      value={profileForm.goalType}
                      onChange={(event) => setProfileForm((current) => ({ ...current, goalType: event.target.value }))}
                    >
                      {GOAL_OPTIONS.map((option) => (
                        <option key={option} value={option}>
                          {formatGoalType(option)}
                        </option>
                      ))}
                    </select>
                  </div>
                </div>

                <div className="form-grid-3">
                  <div className="field">
                    <label htmlFor="profile-record-date">Дата записи</label>
                    <input
                      id="profile-record-date"
                      type="date"
                      value={profileForm.recordDate}
                      onChange={(event) => setProfileForm((current) => ({ ...current, recordDate: event.target.value }))}
                      required
                    />
                  </div>
                  <div className="field">
                    <label htmlFor="profile-sex">Пол</label>
                    <select
                      id="profile-sex"
                      value={profileForm.sex}
                      onChange={(event) => setProfileForm((current) => ({ ...current, sex: event.target.value }))}
                    >
                      <option value="FEMALE">Женский</option>
                      <option value="MALE">Мужской</option>
                    </select>
                  </div>
                  <div className="field">
                    <label htmlFor="profile-age">Возраст</label>
                    <input
                      id="profile-age"
                      type="number"
                      min="1"
                      value={profileForm.age}
                      onChange={(event) => setProfileForm((current) => ({ ...current, age: event.target.value }))}
                      required
                    />
                  </div>
                  <div className="field">
                    <label htmlFor="profile-weight">Вес, кг</label>
                    <input
                      id="profile-weight"
                      type="number"
                      min="0"
                      step="0.1"
                      value={profileForm.weight}
                      onChange={(event) => setProfileForm((current) => ({ ...current, weight: event.target.value }))}
                      required
                    />
                  </div>
                  <div className="field">
                    <label htmlFor="profile-height">Рост, см</label>
                    <input
                      id="profile-height"
                      type="number"
                      min="0"
                      step="0.1"
                      value={profileForm.height}
                      onChange={(event) => setProfileForm((current) => ({ ...current, height: event.target.value }))}
                      required
                    />
                  </div>
                  <div className="field">
                    <label htmlFor="profile-chest">Грудь, см</label>
                    <input
                      id="profile-chest"
                      type="number"
                      min="0"
                      step="0.1"
                      value={profileForm.chest}
                      onChange={(event) => setProfileForm((current) => ({ ...current, chest: event.target.value }))}
                    />
                  </div>
                  <div className="field">
                    <label htmlFor="profile-waist">Талия, см</label>
                    <input
                      id="profile-waist"
                      type="number"
                      min="0"
                      step="0.1"
                      value={profileForm.waist}
                      onChange={(event) => setProfileForm((current) => ({ ...current, waist: event.target.value }))}
                    />
                  </div>
                  <div className="field">
                    <label htmlFor="profile-hips">Бедра, см</label>
                    <input
                      id="profile-hips"
                      type="number"
                      min="0"
                      step="0.1"
                      value={profileForm.hips}
                      onChange={(event) => setProfileForm((current) => ({ ...current, hips: event.target.value }))}
                    />
                  </div>
                </div>

                <div className="inline-actions">
                  <button type="submit" className="button">
                    Сохранить профиль
                  </button>
                </div>
              </form>
            </Panel>
          ) : null}

          {showMeasurementForm ? (
            <Panel title="Новая запись измерений" description="История измерений обновляется отдельно от общей карточки пользователя.">
              <form className="page-stack" onSubmit={handleMeasurementCreate}>
                <div className="form-grid-3">
                  <div className="field">
                    <label htmlFor="measurement-date">Дата</label>
                    <input
                      id="measurement-date"
                      type="date"
                      value={measurementForm.recordDate}
                      onChange={(event) => setMeasurementForm((current) => ({ ...current, recordDate: event.target.value }))}
                      required
                    />
                  </div>
                  <div className="field">
                    <label htmlFor="measurement-sex">Пол</label>
                    <select
                      id="measurement-sex"
                      value={measurementForm.sex}
                      onChange={(event) => setMeasurementForm((current) => ({ ...current, sex: event.target.value }))}
                    >
                      <option value="FEMALE">Женский</option>
                      <option value="MALE">Мужской</option>
                    </select>
                  </div>
                  <div className="field">
                    <label htmlFor="measurement-age">Возраст</label>
                    <input
                      id="measurement-age"
                      type="number"
                      min="1"
                      value={measurementForm.age}
                      onChange={(event) => setMeasurementForm((current) => ({ ...current, age: event.target.value }))}
                      required
                    />
                  </div>
                  <div className="field">
                    <label htmlFor="measurement-weight">Вес, кг</label>
                    <input
                      id="measurement-weight"
                      type="number"
                      min="0"
                      step="0.1"
                      value={measurementForm.weight}
                      onChange={(event) => setMeasurementForm((current) => ({ ...current, weight: event.target.value }))}
                      required
                    />
                  </div>
                  <div className="field">
                    <label htmlFor="measurement-height">Рост, см</label>
                    <input
                      id="measurement-height"
                      type="number"
                      min="0"
                      step="0.1"
                      value={measurementForm.height}
                      onChange={(event) => setMeasurementForm((current) => ({ ...current, height: event.target.value }))}
                      required
                    />
                  </div>
                  <div className="field">
                    <label htmlFor="measurement-chest">Грудь, см</label>
                    <input
                      id="measurement-chest"
                      type="number"
                      min="0"
                      step="0.1"
                      value={measurementForm.chest}
                      onChange={(event) => setMeasurementForm((current) => ({ ...current, chest: event.target.value }))}
                    />
                  </div>
                  <div className="field">
                    <label htmlFor="measurement-waist">Талия, см</label>
                    <input
                      id="measurement-waist"
                      type="number"
                      min="0"
                      step="0.1"
                      value={measurementForm.waist}
                      onChange={(event) => setMeasurementForm((current) => ({ ...current, waist: event.target.value }))}
                    />
                  </div>
                  <div className="field">
                    <label htmlFor="measurement-hips">Бедра, см</label>
                    <input
                      id="measurement-hips"
                      type="number"
                      min="0"
                      step="0.1"
                      value={measurementForm.hips}
                      onChange={(event) => setMeasurementForm((current) => ({ ...current, hips: event.target.value }))}
                    />
                  </div>
                </div>

                <div className="inline-actions">
                  <button type="submit" className="button">
                    Добавить запись
                  </button>
                </div>
              </form>
            </Panel>
          ) : null}
        </div>
      ) : null}

      <div className="tab-switcher">
        <button
          type="button"
          className={`tab-button${activeTab === 'meals' ? ' tab-button-active' : ''}`}
          onClick={() => setActiveTab('meals')}
        >
          Приемы пищи ({meals.length})
        </button>
        <button
          type="button"
          className={`tab-button${activeTab === 'water' ? ' tab-button-active' : ''}`}
          onClick={() => setActiveTab('water')}
        >
          Жидкость ({waterEntries.length})
        </button>
      </div>

      {activeTab === 'meals' ? (
        <div className="content-grid content-grid-wide">
          <Panel
            title="Приемы пищи пользователя"
            description="В списке показываются только названия блюд и доступные действия."
            actions={
              <button
                type="button"
                className="button"
                onClick={() => {
                  setEditingMealId(null);
                  setMealForm(emptyMealForm());
                }}
              >
                Добавить прием пищи
              </button>
            }
          >
            <div className="goal-card goal-card-inline">
              <span className="goal-label">Целевое КБЖУ пользователя</span>
              <strong>{formatWholeNumber(latestMetrics?.dailyGoal?.calories)} ккал</strong>
              <span className="stat-subtext">
                {!latestMetrics?.dailyGoal
                  ? 'Цель пока не задана'
                  : latestMetrics.autoCalculated === false
                    ? 'Задано вручную'
                    : 'Рассчитано автоматически'}
              </span>
              <ul className="goal-list">
                <li>Белки: {formatWholeNumber(latestMetrics?.dailyGoal?.proteins)}</li>
                <li>Жиры: {formatWholeNumber(latestMetrics?.dailyGoal?.fats)}</li>
                <li>Углеводы: {formatWholeNumber(latestMetrics?.dailyGoal?.carbohydrates)}</li>
              </ul>

              <div className="inline-actions goal-actions">
                <button
                  type="button"
                  className="button-secondary"
                  onClick={handleAutoGoalCalculation}
                  disabled={goalLoading}
                >
                  {goalLoading ? 'Пересчитываю…' : 'Рассчитать автоматически'}
                </button>
                <button
                  type="button"
                  className="button-ghost"
                  onClick={() => {
                    setGoalForm(nutritionFormFromValue(latestMetrics?.dailyGoal));
                    setShowGoalForm((current) => !current);
                  }}
                  disabled={!latestMetrics?.id || goalLoading}
                >
                  {showGoalForm ? 'Скрыть ручной ввод' : 'Изменить вручную'}
                </button>
              </div>

              {showGoalForm ? (
                <form className="goal-form" onSubmit={handleManualGoalSubmit}>
                  <div className="form-grid-3">
                    <div className="field">
                      <label htmlFor="user-goal-calories">Калории</label>
                      <input
                        id="user-goal-calories"
                        type="number"
                        min="0"
                        step="0.1"
                        value={goalForm.calories}
                        onChange={(event) => setGoalForm((current) => ({ ...current, calories: event.target.value }))}
                        required
                      />
                    </div>
                    <div className="field">
                      <label htmlFor="user-goal-proteins">Белки</label>
                      <input
                        id="user-goal-proteins"
                        type="number"
                        min="0"
                        step="0.1"
                        value={goalForm.proteins}
                        onChange={(event) => setGoalForm((current) => ({ ...current, proteins: event.target.value }))}
                        required
                      />
                    </div>
                    <div className="field">
                      <label htmlFor="user-goal-fats">Жиры</label>
                      <input
                        id="user-goal-fats"
                        type="number"
                        min="0"
                        step="0.1"
                        value={goalForm.fats}
                        onChange={(event) => setGoalForm((current) => ({ ...current, fats: event.target.value }))}
                        required
                      />
                    </div>
                    <div className="field">
                      <label htmlFor="user-goal-carbohydrates">Углеводы</label>
                      <input
                        id="user-goal-carbohydrates"
                        type="number"
                        min="0"
                        step="0.1"
                        value={goalForm.carbohydrates}
                        onChange={(event) =>
                          setGoalForm((current) => ({ ...current, carbohydrates: event.target.value }))
                        }
                        required
                      />
                    </div>
                  </div>

                  <div className="inline-actions">
                    <button type="submit" className="button" disabled={goalLoading}>
                      {goalLoading ? 'Сохраняю…' : 'Сохранить КБЖУ'}
                    </button>
                    <button
                      type="button"
                      className="button-ghost"
                      onClick={() => {
                        setGoalForm(nutritionFormFromValue(latestMetrics?.dailyGoal));
                        setShowGoalForm(false);
                      }}
                      disabled={goalLoading}
                    >
                      Отменить
                    </button>
                  </div>
                </form>
              ) : null}
            </div>

            <div className="entity-list">
              {meals.length === 0 ? (
                <div className="empty-card">У пользователя пока нет приемов пищи.</div>
              ) : (
                meals.map((meal) => (
                  <article className="entity-row" key={meal.id}>
                    <div className="entity-row-main">
                      <h3>{meal.name}</h3>
                      <p>{formatDate(meal.date)}</p>
                    </div>
                    <div className="inline-actions">
                      <Link className="button-secondary" to={`/meals/${meal.id}`}>
                        Посмотреть
                      </Link>
                      <button type="button" className="button-ghost" onClick={() => handleMealEdit(meal.id)}>
                        Редактировать
                      </button>
                      <button type="button" className="button-danger" onClick={() => handleMealDelete(meal.id)}>
                        Удалить
                      </button>
                    </div>
                  </article>
                ))
              )}
            </div>
          </Panel>

          <Panel
            title={editingMealId ? 'Редактирование приема пищи' : 'Новый прием пищи'}
            description="При создании внутри профиля пользователь подставляется автоматически."
          >
            <form className="page-stack" onSubmit={handleMealSubmit}>
              <div className="form-grid">
                <div className="field">
                  <label htmlFor="meal-name-profile">Название</label>
                  <input
                    id="meal-name-profile"
                    value={mealForm.name}
                    onChange={(event) => setMealForm((current) => ({ ...current, name: event.target.value }))}
                    required
                  />
                </div>
                <div className="field">
                  <label htmlFor="meal-date-profile">Дата</label>
                  <input
                    id="meal-date-profile"
                    type="date"
                    value={mealForm.date}
                    onChange={(event) => setMealForm((current) => ({ ...current, date: event.target.value }))}
                    required
                  />
                </div>
              </div>

              <div className="field-full">
                <label>Продукты</label>
                <ProductSelector
                  products={products}
                  selectedIds={mealForm.productIds}
                  onToggle={toggleProduct}
                  searchPlaceholder="Найти продукт для этого приема пищи"
                />
              </div>

              <div className="form-grid-3">
                <div className="field">
                  <label htmlFor="meal-calories-profile">Калории</label>
                  <input
                    id="meal-calories-profile"
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
                  <label htmlFor="meal-proteins-profile">Белки</label>
                  <input
                    id="meal-proteins-profile"
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
                  <label htmlFor="meal-fats-profile">Жиры</label>
                  <input
                    id="meal-fats-profile"
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
                  <label htmlFor="meal-carbohydrates-profile">Углеводы</label>
                  <input
                    id="meal-carbohydrates-profile"
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
                  {editingMealId ? 'Сохранить прием пищи' : 'Добавить прием пищи'}
                </button>
                {editingMealId ? (
                  <button
                    type="button"
                    className="button-ghost"
                    onClick={() => {
                      setEditingMealId(null);
                      setMealForm(emptyMealForm());
                    }}
                  >
                    Отменить редактирование
                  </button>
                ) : null}
              </div>
            </form>
          </Panel>
        </div>
      ) : (
        <div className="content-grid content-grid-wide">
          <Panel
            title="Выпитая жидкость"
            description="Список напитков и воды для выбранного пользователя."
            actions={
              <button
                type="button"
                className="button"
                onClick={() => {
                  setEditingWaterId(null);
                  setWaterForm(emptyWaterForm());
                }}
              >
                Добавить запись
              </button>
            }
          >
            <div className="entity-list">
              {waterEntries.length === 0 ? (
                <div className="empty-card">Записей о жидкости пока нет.</div>
              ) : (
                waterEntries.map((entry) => (
                  <article className="entity-row" key={entry.id}>
                    <div className="entity-row-main">
                      <h3>{entry.drinkType || 'Вода'}</h3>
                      <p>
                        {formatMl(entry.amountMl)} · {formatDate(entry.date)}
                      </p>
                    </div>
                    <div className="inline-actions">
                      <button type="button" className="button-ghost" onClick={() => handleWaterEdit(entry.id)}>
                        Редактировать
                      </button>
                      <button type="button" className="button-danger" onClick={() => handleWaterDelete(entry.id)}>
                        Удалить
                      </button>
                    </div>
                  </article>
                ))
              )}
            </div>
          </Panel>

          <Panel title={editingWaterId ? 'Редактирование записи' : 'Новая запись'} description="Здесь можно добавить воду или другой напиток.">
            <form className="page-stack" onSubmit={handleWaterSubmit}>
              <div className="form-grid">
                <div className="field">
                  <label htmlFor="water-date-profile">Дата</label>
                  <input
                    id="water-date-profile"
                    type="date"
                    value={waterForm.date}
                    onChange={(event) => setWaterForm((current) => ({ ...current, date: event.target.value }))}
                    required
                  />
                </div>
                <div className="field">
                  <label htmlFor="water-amount-profile">Количество, мл</label>
                  <input
                    id="water-amount-profile"
                    type="number"
                    min="1"
                    value={waterForm.amountMl}
                    onChange={(event) => setWaterForm((current) => ({ ...current, amountMl: event.target.value }))}
                    required
                  />
                </div>
                <div className="field">
                  <label htmlFor="water-drink-profile">Напиток</label>
                  <input
                    id="water-drink-profile"
                    value={waterForm.drinkType}
                    onChange={(event) => setWaterForm((current) => ({ ...current, drinkType: event.target.value }))}
                  />
                </div>
                <div className="field-full">
                  <label htmlFor="water-comment-profile">Комментарий</label>
                  <textarea
                    id="water-comment-profile"
                    value={waterForm.comment}
                    onChange={(event) => setWaterForm((current) => ({ ...current, comment: event.target.value }))}
                  />
                </div>
              </div>

              <div className="inline-actions">
                <button type="submit" className="button">
                  {editingWaterId ? 'Сохранить запись' : 'Добавить запись'}
                </button>
                {editingWaterId ? (
                  <button
                    type="button"
                    className="button-ghost"
                    onClick={() => {
                      setEditingWaterId(null);
                      setWaterForm(emptyWaterForm());
                    }}
                  >
                    Отменить редактирование
                  </button>
                ) : null}
              </div>
            </form>
          </Panel>
        </div>
      )}
    </div>
  );
}
