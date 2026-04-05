export const GOAL_OPTIONS = ['WEIGHT_LOSS', 'MAINTENANCE', 'WEIGHT_GAIN'];
export const SEX_OPTIONS = ['FEMALE', 'MALE'];
export const GOAL_LABELS = {
  WEIGHT_LOSS: 'Снижение веса',
  MAINTENANCE: 'Поддержание',
  WEIGHT_GAIN: 'Набор массы',
};
export const SEX_LABELS = {
  FEMALE: 'Женский',
  MALE: 'Мужской',
};

export function todayIso() {
  return new Date().toISOString().slice(0, 10);
}

export function parseDecimal(value) {
  if (value === '' || value === null || value === undefined) {
    return null;
  }

  return Number(value);
}

export function parseInteger(value) {
  if (value === '' || value === null || value === undefined) {
    return null;
  }

  return Number.parseInt(value, 10);
}

export function formatDate(value) {
  return value || '—';
}

export function formatWholeNumber(value, fallback = '—') {
  if (value === '' || value === null || value === undefined) {
    return fallback;
  }

  const numericValue = Number(value);
  return Number.isFinite(numericValue) ? String(Math.trunc(numericValue)) : fallback;
}

export function formatNutrition(value) {
  if (!value) {
    return 'Не задано';
  }

  return `${formatWholeNumber(value.calories, 0)} ккал · Б ${formatWholeNumber(value.proteins, 0)} · Ж ${formatWholeNumber(value.fats, 0)} · У ${formatWholeNumber(value.carbohydrates, 0)}`;
}

export function formatGoalType(value) {
  return GOAL_LABELS[value] || value || '—';
}

export function formatSex(value) {
  return SEX_LABELS[value] || value || '—';
}

export function formatMl(value) {
  return value ? `${value} мл` : '—';
}

export function formatAge(value) {
  return value ? `${value} лет` : '—';
}

export function buildInitials(name) {
  if (!name) {
    return 'FD';
  }

  const parts = name
    .trim()
    .split(/\s+/)
    .filter(Boolean)
    .slice(0, 2);

  return parts.map((part) => part[0]?.toUpperCase() ?? '').join('') || 'FD';
}

export function getProductEmoji(name = '') {
  const normalized = name.trim().toLowerCase();

  if (!normalized) {
    return '🍽️';
  }

  const mappings = [
    { emoji: '🍎', patterns: ['apple', 'яблок'] },
    { emoji: '🍌', patterns: ['banana', 'банан'] },
    { emoji: '🍊', patterns: ['orange', 'апельсин', 'mandarin', 'мандарин'] },
    { emoji: '🍋', patterns: ['lemon', 'лимон'] },
    { emoji: '🍓', patterns: ['strawberr', 'клубник'] },
    { emoji: '🫐', patterns: ['blueberr', 'черник', 'ягод'] },
    { emoji: '🍇', patterns: ['grape', 'виноград'] },
    { emoji: '🍉', patterns: ['watermelon', 'арбуз'] },
    { emoji: '🍍', patterns: ['pineapple', 'ананас'] },
    { emoji: '🍑', patterns: ['peach', 'персик'] },
    { emoji: '🍐', patterns: ['pear', 'груш'] },
    { emoji: '🥝', patterns: ['kiwi', 'киви'] },
    { emoji: '🥑', patterns: ['avocado', 'авокад'] },
    { emoji: '🍅', patterns: ['tomato', 'томат', 'помидор'] },
    { emoji: '🥒', patterns: ['cucumber', 'огур'] },
    { emoji: '🥦', patterns: ['broccoli', 'брокколи'] },
    { emoji: '🥕', patterns: ['carrot', 'морков'] },
    { emoji: '🥔', patterns: ['potato', 'карто'] },
    { emoji: '🌽', patterns: ['corn', 'кукуруз'] },
    { emoji: '🫑', patterns: ['pepper', 'перец'] },
    { emoji: '🍄', patterns: ['mushroom', 'гриб'] },
    { emoji: '🥜', patterns: ['nut', 'орех'] },
    { emoji: '🫘', patterns: ['bean', 'фасол', 'чечев', 'нут'] },
    { emoji: '🥚', patterns: ['egg', 'яйц'] },
    { emoji: '🧀', patterns: ['cheese', 'сыр'] },
    { emoji: '🥛', patterns: ['milk', 'молок', 'kefir', 'кефир'] },
    { emoji: '🥣', patterns: ['yogurt', 'йогурт', 'porridge', 'каша', 'oat', 'овсян'] },
    { emoji: '🍗', patterns: ['chicken', 'кур', 'turkey', 'индейк'] },
    { emoji: '🥩', patterns: ['beef', 'говядин', 'meat', 'мяс', 'pork', 'свин'] },
    { emoji: '🐟', patterns: ['fish', 'рыб', 'salmon', 'лосос', 'tuna', 'тунец'] },
    { emoji: '🍤', patterns: ['shrimp', 'кревет'] },
    { emoji: '🍞', patterns: ['bread', 'хлеб', 'toast', 'тост'] },
    { emoji: '🍚', patterns: ['rice', 'рис', 'buckwheat', 'греч'] },
    { emoji: '🍝', patterns: ['pasta', 'макарон', 'spaghetti', 'спагет'] },
    { emoji: '🥗', patterns: ['salad', 'салат'] },
    { emoji: '🍲', patterns: ['soup', 'суп'] },
    { emoji: '🍕', patterns: ['pizza', 'пицц'] },
    { emoji: '🍔', patterns: ['burger', 'бургер'] },
    { emoji: '🍫', patterns: ['chocolate', 'шоколад'] },
    { emoji: '🍪', patterns: ['cookie', 'печень'] },
    { emoji: '🍰', patterns: ['cake', 'торт'] },
    { emoji: '🍨', patterns: ['ice cream', 'морож'] },
    { emoji: '☕', patterns: ['coffee', 'кофе'] },
    { emoji: '🍵', patterns: ['tea', 'чай'] },
  ];

  const match = mappings.find((item) => item.patterns.some((pattern) => normalized.includes(pattern)));
  return match?.emoji || '🍽️';
}

export function toggleId(selectedIds, id) {
  return selectedIds.includes(id) ? selectedIds.filter((value) => value !== id) : [...selectedIds, id];
}

export function parseCommaSeparatedIds(value) {
  return value
    .split(',')
    .map((chunk) => chunk.trim())
    .filter(Boolean)
    .map((chunk) => Number.parseInt(chunk, 10))
    .filter((chunk) => Number.isFinite(chunk));
}

export function joinNotes(notes = []) {
  return notes.join('\n');
}

export function pickLatestRecord(records = [], dateKey = 'recordDate') {
  return [...records].sort((left, right) => {
    const leftValue = left?.[dateKey] ?? '';
    const rightValue = right?.[dateKey] ?? '';

    if (leftValue === rightValue) {
      return (right?.id ?? 0) - (left?.id ?? 0);
    }

    return rightValue.localeCompare(leftValue);
  })[0];
}

export function findEntityNameById(items = [], id, fallbackPrefix = 'ID') {
  const entity = items.find((item) => item.id === id);
  return entity?.name || `${fallbackPrefix} ${id}`;
}

export function sortByDateAscending(records = [], dateKey = 'recordDate') {
  return [...records].sort((left, right) => (left?.[dateKey] ?? '').localeCompare(right?.[dateKey] ?? ''));
}

export function emptyNutritionForm() {
  return {
    calories: '',
    proteins: '',
    fats: '',
    carbohydrates: '',
  };
}

export function nutritionFormFromValue(value) {
  return {
    calories: value?.calories ?? '',
    proteins: value?.proteins ?? '',
    fats: value?.fats ?? '',
    carbohydrates: value?.carbohydrates ?? '',
  };
}

export function nutritionPayloadFromForm(form) {
  return {
    calories: parseDecimal(form.calories),
    proteins: parseDecimal(form.proteins),
    fats: parseDecimal(form.fats),
    carbohydrates: parseDecimal(form.carbohydrates),
  };
}
