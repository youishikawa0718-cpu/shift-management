#!/bin/bash
# SSL Certificate Renewal Script
# Add to crontab: 0 0 1 * * /opt/shift-management/deploy/ssl-renew.sh

set -e

APP_DIR="/opt/shift-management"
cd $APP_DIR

echo "=== Renewing SSL certificates ==="

docker run --rm \
    -v $APP_DIR/certbot/conf:/etc/letsencrypt \
    -v $APP_DIR/certbot/www:/var/www/certbot \
    certbot/certbot renew --quiet

# Reload nginx to pick up new certificates
docker-compose -f docker-compose.ssl.yml exec frontend nginx -s reload

echo "=== SSL renewal complete ==="
