# Развёртывание на Linux (Ubuntu 22.04+)

Проект содержит Dockerfile, Compose, systemd unit, nginx reverse proxy и Prometheus/Grafana provisioning.

1. Установите Docker Engine и Compose plugin, затем скопируйте репозиторий в `/opt/monitoring-demo`.
2. В каталоге проекта выполните `docker compose up -d --build`. Откройте `http://SERVER:8080/actuator/health`, Prometheus — `:9090`, Grafana — `:3000` (логин/пароль `admin`/`admin`, смените пароль после первого входа).
3. Чтобы управлять приложением через systemd, выполните `sudo cp deploy/myapp.service /etc/systemd/system/`, затем `sudo systemctl daemon-reload && sudo systemctl enable --now myapp.service`.
4. Установите nginx (`sudo apt install nginx`), скопируйте `deploy/nginx.conf` в `/etc/nginx/sites-available/monitoring-demo`, включите ссылку в `sites-enabled`, выполните `sudo nginx -t && sudo systemctl reload nginx`.
5. При включённом UFW разрешите HTTP: `sudo ufw allow 80/tcp`.
6. Проверьте `curl http://localhost/api/ok`; изучайте логи через `docker compose logs -f app` или `journalctl -u myapp.service -f`.

Нагрузочный пример: несколько раз запросите `/api/allocate/64`. Приложение удерживает выделенные блоки до 2 минут и ограничивает их размер, чтобы демонстрация не вызывала неограниченный OOM.
