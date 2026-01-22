#!/bin/bash
# SSL Setup Script using Let's Encrypt
# Usage: ./ssl-setup.sh yourdomain.com your@email.com

set -e

DOMAIN=$1
EMAIL=$2

if [ -z "$DOMAIN" ] || [ -z "$EMAIL" ]; then
    echo "Usage: $0 <domain> <email>"
    echo "Example: $0 shift.example.com admin@example.com"
    exit 1
fi

APP_DIR="/opt/shift-management"
cd $APP_DIR

echo "=== Setting up SSL for $DOMAIN ==="

# Create directories for certbot
mkdir -p ./certbot/conf
mkdir -p ./certbot/www

# Update .env with domain
if grep -q "DOMAIN_NAME" .env; then
    sed -i "s/DOMAIN_NAME=.*/DOMAIN_NAME=$DOMAIN/" .env
else
    echo "DOMAIN_NAME=$DOMAIN" >> .env
fi

# Stop existing containers
docker-compose -f docker-compose.ssl.yml down 2>/dev/null || true

# Start nginx temporarily for certificate challenge
echo "=== Starting temporary nginx for certificate challenge ==="
docker-compose -f docker-compose.ssl.yml up -d frontend

# Wait for nginx to start
sleep 5

# Get certificate
echo "=== Obtaining SSL certificate ==="
docker run --rm \
    -v $APP_DIR/certbot/conf:/etc/letsencrypt \
    -v $APP_DIR/certbot/www:/var/www/certbot \
    certbot/certbot certonly \
    --webroot \
    --webroot-path=/var/www/certbot \
    --email $EMAIL \
    --agree-tos \
    --no-eff-email \
    -d $DOMAIN

# Restart with SSL configuration
echo "=== Restarting with SSL enabled ==="
docker-compose -f docker-compose.ssl.yml down
docker-compose -f docker-compose.ssl.yml up -d

echo ""
echo "=== SSL Setup Complete ==="
echo "Your site is now available at: https://$DOMAIN"
echo ""
echo "Certificate will auto-renew. To manually renew:"
echo "  ./deploy/ssl-renew.sh"
