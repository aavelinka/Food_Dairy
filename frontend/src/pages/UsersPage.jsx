import { useEffect, useMemo, useState } from 'react';
import { getErrorMessage } from '../api/client';
import { bodyParametersApi, usersApi } from '../api/services';
import { ActionIconButton, ActionIconLink } from '../components/ActionIconButton';
import { Panel } from '../components/Panel';
import { StatusBanner } from '../components/StatusBanner';
import { GOAL_OPTIONS, SEX_OPTIONS, buildInitials, parseDecimal, parseInteger, pickLatestRecord, todayIso } from '../utils/foodDiary';

function emptyUserForm() {
  return {
    name: '',
    email: '',
    password: '',
    goalType: 'MAINTENANCE',
    recordDate: todayIso(),
    sex: 'FEMALE',
    weight: '',
    height: '',
    age: '',
    chest: '',
    waist: '',
    hips: '',
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

async function enrichUsersWithLatestMetrics(users) {
  const histories = await Promise.all(users.map((user) => bodyParametersApi.byUser(user.id).catch(() => [])));

  return users.map((user, index) => ({
    ...user,
    latestMetrics: pickLatestRecord(histories[index]),
  }));
}

export function UsersPage() {
  const [directoryUsers, setDirectoryUsers] = useState([]);
  const [searchValue, setSearchValue] = useState('');
  const [filters, setFilters] = useState({ sex: '', age: '' });
  const [showFilters, setShowFilters] = useState(false);
  const [showCreateForm, setShowCreateForm] = useState(true);
  const [userForm, setUserForm] = useState(emptyUserForm());
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [formLoading, setFormLoading] = useState(false);
  const [formError, setFormError] = useState('');
  const [formSuccess, setFormSuccess] = useState('');

  async function loadDirectory() {
    setLoading(true);
    setError('');

    try {
      const users = await usersApi.getAll();
      setDirectoryUsers(await enrichUsersWithLatestMetrics(users));
    } catch (requestError) {
      setError(getErrorMessage(requestError));
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadDirectory();
  }, []);

  const visibleUsers = useMemo(() => {
    const normalizedSearch = searchValue.trim().toLowerCase();

    return directoryUsers.filter((user) => {
      const matchesName = !normalizedSearch || user.name.toLowerCase().includes(normalizedSearch);
      const matchesSex = !filters.sex || user.latestMetrics?.sex === filters.sex;
      const matchesAge = !filters.age || Number(user.latestMetrics?.age) === Number(filters.age);

      return matchesName && matchesSex && matchesAge;
    });
  }, [directoryUsers, filters.age, filters.sex, searchValue]);

  async function handleDelete(userId) {
    if (!window.confirm(`Удалить пользователя #${userId}?`)) {
      return;
    }

    try {
      await usersApi.remove(userId);
      setDirectoryUsers((current) => current.filter((user) => user.id !== userId));
    } catch (requestError) {
      setError(getErrorMessage(requestError));
    }
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setFormLoading(true);
    setFormError('');
    setFormSuccess('');

    try {
      await usersApi.create(userPayloadFromForm(userForm));
      setUserForm(emptyUserForm());
      setFormSuccess('Пользователь успешно создан.');
      await loadDirectory();
    } catch (requestError) {
      setFormError(getErrorMessage(requestError));
    } finally {
      setFormLoading(false);
    }
  }

  function resetFilters() {
    setFilters({ sex: '', age: '' });
    setSearchValue('');
  }

  return (
    <div className="page-stack">
      <div className="page-header">
        <div>
          <p className="page-kicker">Пользователи</p>
          <h1>Справочник пользователей</h1>
          <p className="page-subtitle">Поиск, фильтрация и переход в профиль пользователя.</p>
        </div>
        <div className="inline-actions">
          <button type="button" className="button" onClick={() => setShowCreateForm((current) => !current)}>
            {showCreateForm ? 'Скрыть форму' : 'Добавить пользователя'}
          </button>
        </div>
      </div>

      <div className="toolbar-strip">
        <div className="toolbar-search-group">
          <label className="search-input">
            <input
              value={searchValue}
              onChange={(event) => setSearchValue(event.target.value)}
              placeholder="Найти пользователя по имени"
            />
          </label>
          <button type="button" className="button-ghost" onClick={() => setShowFilters((current) => !current)}>
            {showFilters ? 'Скрыть фильтрацию' : 'Фильтрация'}
          </button>
          <div className="toolbar-count">Найдено пользователей: {visibleUsers.length}</div>
        </div>
      </div>

      {showFilters ? (
        <Panel title="Фильтры пользователей" description="Можно искать только по полу, возрасту или комбинировать фильтры с поиском по имени.">
          <div className="form-grid">
            <div className="field">
              <label htmlFor="user-filter-sex">Пол</label>
              <select
                id="user-filter-sex"
                value={filters.sex}
                onChange={(event) => setFilters((current) => ({ ...current, sex: event.target.value }))}
              >
                <option value="">Любой</option>
                {SEX_OPTIONS.map((option) => (
                  <option key={option} value={option}>
                    {option === 'FEMALE' ? 'Женский' : 'Мужской'}
                  </option>
                ))}
              </select>
            </div>
            <div className="field">
              <label htmlFor="user-filter-age">Возраст</label>
              <input
                id="user-filter-age"
                type="number"
                min="1"
                value={filters.age}
                onChange={(event) => setFilters((current) => ({ ...current, age: event.target.value }))}
                placeholder="Например, 23"
              />
            </div>
          </div>
          <div className="inline-actions">
            <button type="button" className="button-ghost" onClick={resetFilters}>
              Сбросить фильтры
            </button>
          </div>
        </Panel>
      ) : null}

      <div className="content-grid content-grid-wide">
        <Panel title="Пользователи" description="В списке отображаются только имена и доступные действия администратора.">
          <StatusBanner tone="error">{error}</StatusBanner>
          <StatusBanner tone="info">{loading ? 'Загружаю пользователей…' : ''}</StatusBanner>

          <div className="entity-list">
            {!loading && visibleUsers.length === 0 ? (
              <div className="empty-card">По текущему запросу пользователи не найдены.</div>
            ) : (
              visibleUsers.map((user) => (
                <article className="entity-row entity-row-compact" key={user.id}>
                  <div className="entity-row-main entity-row-main-with-avatar">
                    <div className="avatar-badge avatar-badge-small" aria-hidden="true">
                      <span>{buildInitials(user.name)}</span>
                    </div>
                    <div className="user-identity">
                      <h3>{user.name}</h3>
                    </div>
                  </div>
                  <div className="inline-actions">
                    <ActionIconLink to={`/users/${user.id}`} icon="view" label={`Открыть профиль пользователя ${user.name}`} />
                    <ActionIconButton icon="delete" tone="danger" label={`Удалить пользователя ${user.name}`} onClick={() => handleDelete(user.id)} />
                  </div>
                </article>
              ))
            )}
          </div>
        </Panel>

        {showCreateForm ? (
          <Panel title="Новый пользователь" description="Создание пользователя сразу с первичными параметрами тела.">
            <StatusBanner tone="error">{formError}</StatusBanner>
            <StatusBanner tone="success">{formSuccess}</StatusBanner>

            <form className="page-stack" onSubmit={handleSubmit}>
              <div className="form-grid">
                <div className="field">
                  <label htmlFor="new-user-name">Имя</label>
                  <input
                    id="new-user-name"
                    value={userForm.name}
                    onChange={(event) => setUserForm((current) => ({ ...current, name: event.target.value }))}
                    required
                  />
                </div>
                <div className="field">
                  <label htmlFor="new-user-email">Почта</label>
                  <input
                    id="new-user-email"
                    type="email"
                    value={userForm.email}
                    onChange={(event) => setUserForm((current) => ({ ...current, email: event.target.value }))}
                    required
                  />
                </div>
                <div className="field">
                  <label htmlFor="new-user-password">Пароль</label>
                  <input
                    id="new-user-password"
                    type="password"
                    minLength="6"
                    value={userForm.password}
                    onChange={(event) => setUserForm((current) => ({ ...current, password: event.target.value }))}
                    required
                  />
                </div>
                <div className="field">
                  <label htmlFor="new-user-goal">Цель</label>
                  <select
                    id="new-user-goal"
                    value={userForm.goalType}
                    onChange={(event) => setUserForm((current) => ({ ...current, goalType: event.target.value }))}
                  >
                    {GOAL_OPTIONS.map((option) => (
                      <option key={option} value={option}>
                        {option === 'WEIGHT_LOSS'
                          ? 'Снижение веса'
                          : option === 'WEIGHT_GAIN'
                            ? 'Набор массы'
                            : 'Поддержание'}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="form-grid-3 form-grid-compact">
                <div className="field">
                  <label htmlFor="new-user-date">Дата записи</label>
                  <input
                    id="new-user-date"
                    type="date"
                    value={userForm.recordDate}
                    onChange={(event) => setUserForm((current) => ({ ...current, recordDate: event.target.value }))}
                    required
                  />
                </div>
                <div className="field">
                  <label htmlFor="new-user-sex">Пол</label>
                  <select
                    id="new-user-sex"
                    value={userForm.sex}
                    onChange={(event) => setUserForm((current) => ({ ...current, sex: event.target.value }))}
                  >
                    <option value="FEMALE">Женский</option>
                    <option value="MALE">Мужской</option>
                  </select>
                </div>
                <div className="field">
                  <label htmlFor="new-user-age">Возраст</label>
                  <input
                    id="new-user-age"
                    type="number"
                    min="1"
                    value={userForm.age}
                    onChange={(event) => setUserForm((current) => ({ ...current, age: event.target.value }))}
                    required
                  />
                </div>
                <div className="field">
                  <label htmlFor="new-user-weight">Вес, кг</label>
                  <input
                    id="new-user-weight"
                    type="number"
                    min="0"
                    step="0.1"
                    value={userForm.weight}
                    onChange={(event) => setUserForm((current) => ({ ...current, weight: event.target.value }))}
                    required
                  />
                </div>
                <div className="field">
                  <label htmlFor="new-user-height">Рост, см</label>
                  <input
                    id="new-user-height"
                    type="number"
                    min="0"
                    step="0.1"
                    value={userForm.height}
                    onChange={(event) => setUserForm((current) => ({ ...current, height: event.target.value }))}
                    required
                  />
                </div>
                <div className="field">
                  <label htmlFor="new-user-chest">Грудь, см</label>
                  <input
                    id="new-user-chest"
                    type="number"
                    min="0"
                    step="0.1"
                    value={userForm.chest}
                    onChange={(event) => setUserForm((current) => ({ ...current, chest: event.target.value }))}
                  />
                </div>
                <div className="field">
                  <label htmlFor="new-user-waist">Талия, см</label>
                  <input
                    id="new-user-waist"
                    type="number"
                    min="0"
                    step="0.1"
                    value={userForm.waist}
                    onChange={(event) => setUserForm((current) => ({ ...current, waist: event.target.value }))}
                  />
                </div>
                <div className="field">
                  <label htmlFor="new-user-hips">Бедра, см</label>
                  <input
                    id="new-user-hips"
                    type="number"
                    min="0"
                    step="0.1"
                    value={userForm.hips}
                    onChange={(event) => setUserForm((current) => ({ ...current, hips: event.target.value }))}
                  />
                </div>
              </div>

              <div className="inline-actions form-actions">
                <button type="submit" className="button" disabled={formLoading}>
                  {formLoading ? 'Сохраняю…' : 'Создать пользователя'}
                </button>
                <button type="button" className="button-ghost" onClick={() => setUserForm(emptyUserForm())}>
                  Очистить форму
                </button>
              </div>
            </form>
          </Panel>
        ) : (
          <Panel title="Создание пользователя" description="Форма скрыта. Нажми «Добавить пользователя», чтобы открыть её снова.">
            <div className="empty-card">Здесь будет форма создания нового пользователя.</div>
          </Panel>
        )}
      </div>
    </div>
  );
}
