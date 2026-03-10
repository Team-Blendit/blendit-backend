# Blendit Lightsail Deployment

## Architecture
- `main` push builds `:blendit-api:bootJar` and deploys over SSH to Lightsail.
- Lightsail runs `nginx`, `redis-server`, and the Spring Boot JAR on the same instance.
- MySQL uses Lightsail Managed Database.
- Uploaded files live on the instance under `/home/ubuntu/app/local-uploads` and are exposed through `https://blendit.kr/uploads`.

## GitHub Actions Secrets
- `LIGHTSAIL_HOST`
- `LIGHTSAIL_USER`
- `LIGHTSAIL_SSH_PRIVATE_KEY`
- `LIGHTSAIL_PORT` (optional, defaults to `22`)

## Server Layout
- App root: `/home/ubuntu/app`
- Releases: `/home/ubuntu/app/releases/<git-sha>`
- Current symlink: `/home/ubuntu/app/current`
- Uploads: `/home/ubuntu/app/local-uploads`
- Env file: `/etc/blendit/blendit-api.env`
- Logs: `/var/log/blendit`

## AWS Checklist
1. Create a Lightsail Ubuntu instance and attach a static IP.
2. Create a Lightsail Managed Database for MySQL and record host, port, DB name, username, and password.
3. Point `blendit.kr` DNS to the Lightsail static IP.
4. Open only `22`, `80`, and `443` on the Lightsail networking page.
5. Install packages on the instance:

```bash
sudo apt-get update
sudo apt-get install -y openjdk-17-jre-headless nginx redis-server certbot python3-certbot-nginx
```

6. Create runtime directories:

```bash
sudo mkdir -p /etc/blendit /var/log/blendit
sudo chown ubuntu:ubuntu /var/log/blendit
mkdir -p /home/ubuntu/app/releases /home/ubuntu/app/local-uploads
```

7. Configure Redis for local-only access by setting `bind 127.0.0.1 ::1` and `protected-mode yes` in `/etc/redis/redis.conf`, then restart Redis.
8. Create `/etc/blendit/blendit-api.env` with production secrets:

```bash
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
SPRING_DATASOURCE_URL=jdbc:mysql://<lightsail-db-endpoint>:3306/<db-name>?useSSL=true&serverTimezone=Asia/Seoul&characterEncoding=UTF-8
SPRING_DATASOURCE_USERNAME=<db-user>
SPRING_DATASOURCE_PASSWORD=<db-password>
REDIS_HOST=127.0.0.1
REDIS_PORT=6379
JWT_SECRET_KEY=<32-byte-or-longer-secret>
KAKAO_CLIENT_ID=<kakao-client-id>
KAKAO_CLIENT_SECRET=<kakao-client-secret>
KAKAO_REDIRECT_URI=<frontend-kakao-callback-url>
GOOGLE_CLIENT_ID=<google-client-id>
GOOGLE_CLIENT_SECRET=<google-client-secret>
GOOGLE_REDIRECT_URI=<frontend-google-callback-url>
STORAGE_LOCAL_ROOT_DIR=/home/ubuntu/app/local-uploads
STORAGE_PUBLIC_BASE_URL=https://blendit.kr/uploads
```

9. Install the systemd unit from a checked-out copy of this repo using `infra/systemd/blendit-api.service`:

```bash
sudo cp infra/systemd/blendit-api.service /etc/systemd/system/blendit-api.service
sudo systemctl daemon-reload
sudo systemctl enable blendit-api
```

10. Install the nginx site from a checked-out copy of this repo using `infra/nginx/blendit-api.conf` and enable it:

```bash
sudo cp infra/nginx/blendit-api.conf /etc/nginx/sites-available/blendit-api
sudo ln -s /etc/nginx/sites-available/blendit-api /etc/nginx/sites-enabled/blendit-api
sudo nginx -t
sudo systemctl reload nginx
```

11. Issue the TLS certificate:

```bash
sudo certbot --nginx -d blendit.kr -d www.blendit.kr
```

12. Add the Lightsail SSH secrets to GitHub Actions.
13. Merge to `main` and let the deploy workflow upload the JAR and restart `blendit-api`.

## Verification
1. Confirm the service is running:

```bash
sudo systemctl status blendit-api
```

2. Check the local app health path:

```bash
curl http://127.0.0.1:8080/api/blendit/auth/ping
```

3. Check external HTTPS:

```bash
curl https://blendit.kr/api/blendit/auth/ping
```

4. Upload a test image and confirm the returned `https://blendit.kr/uploads/...` URL is reachable.
