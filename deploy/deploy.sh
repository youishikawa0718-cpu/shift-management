#!/bin/bash
# Deployment Script for Shift Management System
# Usage: ./deploy.sh [build|start|stop|restart|logs|status]

set -e

APP_DIR="/opt/shift-management"
COMPOSE_FILE="docker-compose.prod.yml"

cd $APP_DIR

case "$1" in
    build)
        echo "=== Building containers ==="
        docker-compose -f $COMPOSE_FILE build --no-cache
        ;;
    start)
        echo "=== Starting services ==="
        docker-compose -f $COMPOSE_FILE up -d
        echo "=== Waiting for services to be healthy ==="
        sleep 10
        docker-compose -f $COMPOSE_FILE ps
        ;;
    stop)
        echo "=== Stopping services ==="
        docker-compose -f $COMPOSE_FILE down
        ;;
    restart)
        echo "=== Restarting services ==="
        docker-compose -f $COMPOSE_FILE down
        docker-compose -f $COMPOSE_FILE up -d
        ;;
    logs)
        docker-compose -f $COMPOSE_FILE logs -f ${2:-}
        ;;
    status)
        docker-compose -f $COMPOSE_FILE ps
        ;;
    update)
        echo "=== Pulling latest changes ==="
        git pull origin main
        echo "=== Rebuilding and restarting ==="
        docker-compose -f $COMPOSE_FILE build
        docker-compose -f $COMPOSE_FILE up -d
        ;;
    *)
        echo "Usage: $0 {build|start|stop|restart|logs|status|update}"
        echo ""
        echo "Commands:"
        echo "  build   - Build Docker images"
        echo "  start   - Start all services"
        echo "  stop    - Stop all services"
        echo "  restart - Restart all services"
        echo "  logs    - View logs (optionally specify service: logs backend)"
        echo "  status  - Show service status"
        echo "  update  - Pull latest code and redeploy"
        exit 1
        ;;
esac
