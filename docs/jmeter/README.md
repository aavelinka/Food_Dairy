# JMeter Load Tests

В каталоге лежат два JMeter-сценария:

- `all_endpoints_load_test.jmx` - общий нагрузочный сценарий для всех REST endpoint'ов проекта.
- `meal_bulk_load_test.jmx` - отдельный сценарий только для bulk-операций `meal`.

## Что покрывает `all_endpoints_load_test.jmx`

Сценарий разбит на thread group'ы:

- `User API Load` - все endpoint'ы `UserController`.
- `Product API Load` - все endpoint'ы `ProductController`.
- `Meal API Load` - все endpoint'ы `MealController`, включая `bulk/no-tx`, `bulk/tx`, `bulk/tx/async`, статус задачи и статистику.
- `Note API Load` - все endpoint'ы `NoteController`.
- `Body Parameters API Load` - все endpoint'ы `BodyParametersController`.
- `Water Intake API Load` - все endpoint'ы `WaterIntakeController`.
- `Demo API Load` - оба endpoint'а `DemoController`.

Каждый поток сам создаёт необходимые данные перед вызовом зависимых endpoint'ов. Это нужно, чтобы нагрузочный тест не зависел от заранее подготовленной БД.

## Запуск

Перед запуском подними приложение локально, например на `http://localhost:8080`.

Пример запуска полного сценария:

```bash
jmeter -n -t docs/jmeter/all_endpoints_load_test.jmx \
  -Jhost=localhost \
  -Jport=8080 \
  -Jthreads=5 \
  -Jloops=2 \
  -JrampUp=3 \
  -JdemoThreads=2 \
  -JdemoLoops=1 \
  -JdemoRampUp=1 \
  -JdemoThreadCount=50 \
  -JdemoIncrements=1000 \
  -JtestDate=2026-03-29 \
  -l docs/jmeter/api_full_results.jtl
```

Пример запуска только bulk-сценария:

```bash
jmeter -n -t docs/jmeter/meal_bulk_load_test.jmx \
  -Jhost=localhost \
  -Jport=8080 \
  -Jthreads=20 \
  -Jloops=3 \
  -JrampUp=5 \
  -Jpath=/api/meal/bulk/tx \
  -JexpectedCode=201 \
  -l docs/jmeter/meal_bulk_results.jtl
```

## Параметры

- `protocol` - протокол, по умолчанию `http`.
- `host` - адрес приложения, по умолчанию `localhost`.
- `port` - порт приложения, по умолчанию `8080`.
- `threads` - количество потоков для CRUD/search thread group'ов.
- `loops` - количество итераций для CRUD/search thread group'ов.
- `rampUp` - время разгона для CRUD/search thread group'ов.
- `demoThreads` - количество потоков для `Demo API Load`.
- `demoLoops` - количество итераций для `Demo API Load`.
- `demoRampUp` - время разгона для `Demo API Load`.
- `demoThreadCount` - параметр `threadCount` для race condition demo endpoint'ов.
- `demoIncrements` - параметр `incrementsPerThread` для race condition demo endpoint'ов.
- `testDate` - дата, которая подставляется в сценарии для `meal`, `note`, `body-parameters` и `water-intakes`.

## Что смотреть в результатах

Для отчёта обычно снимают:

- `Average`, `Median`, `95% Line`.
- `Throughput`.
- `Error %`.
- самые медленные thread group / endpoint'ы.
- различия между `bulk/no-tx`, `bulk/tx` и `bulk/tx/async`.
