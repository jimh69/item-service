# PowerShell script to set up environment variables for config server and item service
# Run this script to quickly set up the environment variables

Write-Host "=== Config Server and Item Service Environment Setup ===" -ForegroundColor Green
Write-Host ""

# Get user input for credentials
Write-Host "Please enter your configuration:" -ForegroundColor Yellow
Write-Host ""

$username = Read-Host "Config Server Username (default: configuser)"
if (-not $username) { $username = "configuser" }

$hash = Read-Host "Config Server Password Hash (from bcrypt-generator.com)"
if (-not $hash) {
    Write-Host "❌ Password hash is required. Please generate one at bcrypt-generator.com" -ForegroundColor Red
    exit
}

$password = Read-Host "Item Service Password (must match the password used for hash generation)"
if (-not $password) {
    Write-Host "❌ Password is required and must match the password used for hash generation" -ForegroundColor Red
    exit
}

# Set environment variables
Write-Host ""
Write-Host "Setting environment variables..." -ForegroundColor Cyan

# Config Server variables
$env:CONFIG_USERNAME = $username
$env:CONFIG_PASSWORD_HASH = $hash

# Item Service variables
$env:CONFIG_SERVER_USERNAME = $username
$env:CONFIG_SERVER_PASSWORD = $password

Write-Host ""
Write-Host "✅ Environment variables set successfully!" -ForegroundColor Green
Write-Host ""
Write-Host "Config Server Variables:" -ForegroundColor Yellow
Write-Host "  CONFIG_USERNAME: $env:CONFIG_USERNAME" -ForegroundColor Gray
Write-Host "  CONFIG_PASSWORD_HASH: $env:CONFIG_PASSWORD_HASH" -ForegroundColor Gray
Write-Host ""
Write-Host "Item Service Variables:" -ForegroundColor Yellow
Write-Host "  CONFIG_SERVER_USERNAME: $env:CONFIG_SERVER_USERNAME" -ForegroundColor Gray
Write-Host "  CONFIG_SERVER_PASSWORD: $env:CONFIG_SERVER_PASSWORD" -ForegroundColor Gray
Write-Host ""

# Verify the hash format
if ($hash -match '^\$2a\$12\$[A-Za-z0-9./]{53}$') {
    Write-Host "✅ BCrypt hash format is valid" -ForegroundColor Green
} else {
    Write-Host "⚠️  Warning: BCrypt hash format may be invalid" -ForegroundColor Yellow
    Write-Host "   Expected format: $2a$12$[53 characters]" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "=== Next Steps ===" -ForegroundColor Green
Write-Host "1. Start config server with these environment variables set"
Write-Host "2. Start item service with these environment variables set"
Write-Host "3. Run .\verify-setup.ps1 to test the authentication"
Write-Host ""
Write-Host "⚠️  Note: These environment variables are only set for this PowerShell session" -ForegroundColor Yellow
Write-Host "   To make them permanent, use:" -ForegroundColor Yellow
Write-Host "   [System.Environment]::SetEnvironmentVariable('CONFIG_USERNAME', '$username', 'User')" -ForegroundColor Yellow
Write-Host "   [System.Environment]::SetEnvironmentVariable('CONFIG_PASSWORD_HASH', '$hash', 'User')" -ForegroundColor Yellow
Write-Host "   [System.Environment]::SetEnvironmentVariable('CONFIG_SERVER_USERNAME', '$username', 'User')" -ForegroundColor Yellow
Write-Host "   [System.Environment]::SetEnvironmentVariable('CONFIG_SERVER_PASSWORD', '$password', 'User')" -ForegroundColor Yellow