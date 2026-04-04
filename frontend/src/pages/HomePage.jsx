import { Link } from 'react-router-dom';
import { Panel } from '../components/Panel';

const featureCards = [
  {
    title: 'Профили пользователей',
    text: 'Создание карточек пользователей с историей измерений, целями и быстрым переходом к связанным данным.',
  },
  {
    title: 'Целевое КБЖУ',
    text: 'Автоматический расчет по актуальным измерениям и ручная настройка, если пользователь хочет задать свою цель.',
  },
  {
    title: 'Приемы пищи и продукты',
    text: 'Отдельный каталог продуктов и сборка приемов пищи с суммарным КБЖУ для каждого блюда.',
  },
  {
    title: 'Вода и напитки',
    text: 'Учет выпитой жидкости внутри профиля пользователя без лишнего переключения между экранами.',
  },
];

const audienceCards = [
  {
    title: 'Для человека, который следит за питанием',
    text: 'Подходит тем, кто хочет видеть приемы пищи, целевое КБЖУ и базовую динамику тела в одном интерфейсе.',
  },
  {
    title: 'Для тренера или куратора',
    text: 'Удобно, когда нужно быстро открыть профиль пользователя, проверить актуальные параметры и сверить питание с целью.',
  },
  {
    title: 'Для учебного или демонстрационного проекта',
    text: 'Показывает связанный CRUD, бизнес-логику и навигацию между сущностями без перегруженного сценария.',
  },
];

const quickSteps = [
  'Создай пользователя с первичными измерениями.',
  'Проверь или пересчитай целевое КБЖУ в профиле.',
  'Добавь продукты и собери из них прием пищи.',
  'Отслеживай воду и новые измерения в карточке пользователя.',
];

const sectionLinks = [
  {
    title: 'Пользователи',
    description: 'Основная точка входа: профили, история измерений, КБЖУ, вода и приемы пищи.',
    to: '/users',
    className: 'button',
    label: 'Открыть профили',
  },
  {
    title: 'Продукты',
    description: 'Справочник ингредиентов с КБЖУ на 100 грамм для дальнейшей сборки приемов пищи.',
    to: '/products',
    className: 'button-secondary',
    label: 'Открыть продукты',
  },
  {
    title: 'Приемы пищи',
    description: 'Глобальный список блюд с привязкой к пользователю и переходом в детальный просмотр.',
    to: '/meals',
    className: 'button-ghost',
    label: 'Открыть блюда',
  },
];

export function HomePage() {
  return (
    <div className="page-stack">
      <section className="home-hero">
        <div className="home-hero-body">
          <p className="page-kicker">Главная</p>
          <h1 className="home-hero-title">Food Diary помогает вести питание, воду, измерения тела и целевое КБЖУ в одном месте.</h1>
          <p className="home-hero-subtitle">
            Это веб-приложение подходит для персонального дневника питания, сопровождения пользователя по питанию и демонстрации
            связанного функционала: профиль, история измерений, каталог продуктов и приемы пищи.
          </p>
        </div>

        <aside className="home-highlight-card">
          <span className="home-highlight-kicker">Кому подходит</span>
          <h2>Тем, кому нужен единый обзор питания и параметров пользователя.</h2>
          <ul className="home-bullet-list">
            <li>Для контроля суточной цели по калориям и макронутриентам.</li>
            <li>Для ведения истории измерений и быстрого пересчета цели.</li>
            <li>Для работы с набором продуктов и блюдами без разрыва сценария.</li>
          </ul>
        </aside>
      </section>

      <div className="content-grid">
        <Panel
          title="Что умеет приложение"
          eyebrow="Возможности"
          description="Ключевой функционал собран вокруг профиля пользователя и его пищевого дневника."
        >
          <div className="home-feature-grid">
            {featureCards.map((card) => (
              <article className="home-feature-card" key={card.title}>
                <h3>{card.title}</h3>
                <p>{card.text}</p>
              </article>
            ))}
          </div>
        </Panel>

        <Panel
          title="Когда приложение особенно полезно"
          eyebrow="Сценарии"
          description="Не только для хранения данных, но и для быстрого обзора состояния конкретного пользователя."
        >
          <div className="home-feature-grid">
            {audienceCards.map((card) => (
              <article className="home-feature-card home-feature-card-warm" key={card.title}>
                <h3>{card.title}</h3>
                <p>{card.text}</p>
              </article>
            ))}
          </div>
        </Panel>
      </div>

      <Panel
        title="Как начать работу"
        eyebrow="Быстрый старт"
        description="Базовый сценарий занимает несколько шагов и сразу дает полную картину по пользователю."
      >
        <div className="home-step-grid">
          {quickSteps.map((step, index) => (
            <article className="home-step-card" key={step}>
              <span className="home-step-number">0{index + 1}</span>
              <p>{step}</p>
            </article>
          ))}
        </div>
      </Panel>

      <Panel
        title="Разделы приложения"
        eyebrow="Навигация"
        description="Можно перейти сразу в нужный раздел в зависимости от задачи."
      >
        <div className="home-link-grid">
          {sectionLinks.map((item) => (
            <article className="home-link-card" key={item.title}>
              <div className="home-link-copy">
                <h3>{item.title}</h3>
                <p>{item.description}</p>
              </div>
              <Link className={item.className} to={item.to}>
                {item.label}
              </Link>
            </article>
          ))}
        </div>
      </Panel>
    </div>
  );
}
