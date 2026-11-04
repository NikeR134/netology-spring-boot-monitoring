# Spring Boot Monitoring Demo

Учебный стенд мониторинга JVM с Prometheus и Grafana. Стек запускается одной командой:

```bash
docker compose up -d --build
```

## Сервисы

| Сервис | URL | Назначение |
|---|---|---|
| Spring Boot | http://localhost:8080 | Демоприложение и Actuator |
| Prometheus | http://localhost:9090 | Сбор метрик и правила алертов |
| Grafana | http://localhost:3000 | Дашборд (admin / admin) |

Grafana автоматически получает datasource Prometheus и импортирует дашборд. Prometheus собирает `/actuator/prometheus` каждые 5 секунд. Heap alert срабатывает, если использование heap выше 70% в течение 15 секунд.

## Демонстрационные endpoints

* `GET /api/ok` — 200
* `GET /api/not-found` — 404
* `GET /api/error` — 500
* `GET /api/allocate/{megabytes}` — выделяет и удерживает память (1–256 MiB за запрос, максимум 384 MiB на 2 минуты)
* `GET /actuator/health` — health check
* `GET /actuator/prometheus` — метрики Prometheus

Метрика `demo_controller_calls_total` считает вызовы контроллера. Дашборд также показывает использование и максимум heap, HTTP rate, RSS и доступность приложения.

## Воспроизведение алерта

Проверьте верхнюю границу heap в Prometheus (`jvm_memory_max_bytes{area="heap"}`), затем вызовите `/api/allocate/64` несколько раз, пока heap usage не превысит 70%. Через 15 секунд алерт `HighHeapUsage` будет виден в Prometheus в разделе Alerts. Выделения ограничены и автоматически освобождаются через 2 минуты.

## CI/CD

Workflow GitHub Actions в `.github/workflows/ci.yml` собирает JAR при push и pull request. Настройка развёртывания на сервере описана в `deploy/README.md`.

## Linux deploy

См. [deploy/README.md](deploy/README.md): Docker, systemd, nginx reverse proxy, firewall и логи journalctl.

