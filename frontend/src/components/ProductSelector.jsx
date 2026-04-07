import { useState } from 'react';
import { getProductEmoji } from '../utils/foodDiary';

export function ProductSelector({
  products,
  selectedIds,
  onToggle,
  searchPlaceholder = 'Найти продукт по названию',
  emptyMessage = 'Продукты по текущему запросу не найдены.',
}) {
  const [searchValue, setSearchValue] = useState('');

  const normalizedSearch = searchValue.trim().toLowerCase();
  const visibleProducts = normalizedSearch
    ? products.filter((product) => product.name.toLowerCase().includes(normalizedSearch))
    : products;
  const selectedProducts = products.filter((product) => selectedIds.includes(product.id));

  return (
    <div className="product-selector">
      <div className="product-selector-toolbar">
        <label className="search-input">
          <input
            value={searchValue}
            onChange={(event) => setSearchValue(event.target.value)}
            placeholder={searchPlaceholder}
          />
        </label>
        <div className="product-selector-count">Выбрано продуктов: {selectedIds.length}</div>
      </div>

      {selectedProducts.length > 0 ? (
        <div className="selected-products">
          {selectedProducts.map((product) => (
            <div className="selected-product-chip" key={product.id}>
              <span>
                <span className="product-emoji" aria-hidden="true">
                  {getProductEmoji(product.name)}
                </span>{' '}
                {product.name}
              </span>
              <button type="button" aria-label={`Убрать ${product.name}`} onClick={() => onToggle(product.id)}>
                ×
              </button>
            </div>
          ))}
        </div>
      ) : (
        <div className="product-selector-hint">Выбери продукты для блюда из списка ниже.</div>
      )}

      <div className="product-option-grid">
        {visibleProducts.length === 0 ? (
          <div className="empty-card">{emptyMessage}</div>
        ) : (
          visibleProducts.map((product) => {
            const isSelected = selectedIds.includes(product.id);

            return (
              <label className={`product-option${isSelected ? ' product-option-selected' : ''}`} key={product.id}>
                <input type="checkbox" checked={isSelected} onChange={() => onToggle(product.id)} />
                <span className="product-option-name">
                  <span className="product-emoji" aria-hidden="true">
                    {getProductEmoji(product.name)}
                  </span>
                  {product.name}
                </span>
              </label>
            );
          })
        )}
      </div>
    </div>
  );
}
