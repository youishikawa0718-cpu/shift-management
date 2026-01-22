#!/bin/bash
# EC2 Initial Setup Script for Shift Management System
# Run this script on a fresh Amazon Linux 2023 or Ubuntu 22.04 instance

set -e

echo "=== Updating system packages ==="
if [ -f /etc/amazon-linux-release ]; then
    sudo dnf update -y
    sudo dnf install -y docker git
else
    sudo apt-get update
    sudo apt-get install -y docker.io docker-compose git
fi

echo "=== Installing Docker Compose ==="
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

echo "=== Starting Docker service ==="
sudo systemctl start docker
sudo systemctl enable docker

echo "=== Adding user to docker group ==="
sudo usermod -aG docker $USER

echo "=== Creating application directory ==="
sudo mkdir -p /opt/shift-management
sudo chown $USER:$USER /opt/shift-management

echo "=== Setup complete ==="
echo "Please log out and log back in for docker group changes to take effect."
echo "Then run: cd /opt/shift-management && ./deploy.sh"
