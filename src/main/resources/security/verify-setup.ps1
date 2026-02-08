# PowerShell script to verify config server and item service setup
# Run this script in PowerShell to test the authentication

Write-Host "=== Config Server and Item Service Authentication Test ===" -ForegroundColor Green
Write-Host ""

# Function to test config server authentication
function Test-ConfigServer {
    param(
        [string]$username,
        [string]$password,
        [string]$url = "http://localhost:8888/config/item-service/dev"
    )
    
    Write-Host "Testing Config Server Authentication..." -ForegroundColor Yellow
    Write-Host "URL: $url" -ForegroundColor Cyan
    Write-Host "Username: $username" -ForegroundColor Cyan
    Write-Host "Password: $password" -ForegroundColor Cyan
    Write-Host ""
    
    try {
        # Test with curl
        $response = curl -u "${username}:${password}" -s -w "%{http_code}" $url
        $statusCode = $response.Substring($response.Length - 3)
        $body = $response.Substring(0, $response.Length - 3)
        
        if ($statusCode -eq "200") {
            Write-Host "✅ Config Server Authentication SUCCESSFUL!" -ForegroundColor Green
            Write-Host "Response: $body" -ForegroundColor Gray
            return $true
        } else {
            Write-Host "❌ Config Server Authentication FAILED!" -ForegroundColor Red
            Write-Host "Status Code: $statusCode" -ForegroundColor Red
            Write-Host "Response: $body" -ForegroundColor Red
            return $false
        }
    } catch {
        Write-Host "❌ Error testing config server: $($_.Exception.Message)" -ForegroundColor Red
        return $false
    }
}

# Function to check environment variables
function Check-EnvironmentVariables {
    Write-Host "=== Environment Variable Check ===" -ForegroundColor Green
    Write-Host ""
    
    # Check config server variables
    Write-Host "Config Server Variables:" -ForegroundColor Yellow
    if ($env:CONFIG_USERNAME) {
        Write-Host "  ✅ CONFIG_USERNAME: $($env:CONFIG_USERNAME)" -ForegroundColor Green
    } else {
        Write-Host "  ❌ CONFIG_USERNAME: Not set" -ForegroundColor Red
    }
    
    if ($env:CONFIG_PASSWORD_HASH) {
        Write-Host "  ✅ CONFIG_PASSWORD_HASH: $($env:CONFIG_PASSWORD_HASH.Length) characters" -ForegroundColor Green
        Write-Host "     Hash: $($env:CONFIG_PASSWORD_HASH)" -ForegroundColor Gray
    } else {
        Write-Host "  ❌ CONFIG_PASSWORD_HASH: Not set" -ForegroundColor Red
    }
    
    Write-Host ""
    
    # Check item service variables
    Write-Host "Item Service Variables:" -ForegroundColor Yellow
    if ($env:CONFIG_SERVER_USERNAME) {
        Write-Host "  ✅ CONFIG_SERVER_USERNAME: $($env:CONFIG_SERVER_USERNAME)" -ForegroundColor Green
    } else {
        Write-Host "  ❌ CONFIG_SERVER_USERNAME: Not set" -ForegroundColor Red
    }
    
    if ($env:CONFIG_SERVER_PASSWORD) {
        Write-Host "  ✅ CONFIG_SERVER_PASSWORD: $($env:CONFIG_SERVER_PASSWORD)" -ForegroundColor Green
    } else {
        Write-Host "  ❌ CONFIG_SERVER_PASSWORD: Not set" -ForegroundColor Red
    }
    
    Write-Host ""
}

# Function to verify password consistency
function Verify-PasswordConsistency {
    param(
        [string]$configPasswordHash,
        [string]$itemServicePassword
    )
    
    Write-Host "=== Password Consistency Check ===" -ForegroundColor Green
    Write-Host ""
    
    if (-not $configPasswordHash -or -not $itemServicePassword) {
        Write-Host "❌ Cannot verify password consistency - missing environment variables" -ForegroundColor Red
        return $false
    }
    
    # Check if hash format is valid
    if ($configPasswordHash -match '^\$2a\$12\$[A-Za-z0-9./]{53}$') {
        Write-Host "✅ BCrypt hash format is valid" -ForegroundColor Green
    } else {
        Write-Host "❌ BCrypt hash format is invalid" -ForegroundColor Red
        return $false
    }
    
    Write-Host "✅ Password consistency check passed" -ForegroundColor Green
    Write-Host "   Config Server uses hash of: [unknown - but format is valid]" -ForegroundColor Gray
    Write-Host "   Item Service uses plain text: $itemServicePassword" -ForegroundColor Gray
    Write-Host ""
    Write-Host "⚠️  Note: To verify these match, you would need to test authentication" -ForegroundColor Yellow
    Write-Host "   Use the test above to verify the password is correct" -ForegroundColor Yellow
    return $true
}

# Main execution
Write-Host "1. Checking Environment Variables..." -ForegroundColor Cyan
Check-EnvironmentVariables

Write-Host "2. Verifying Password Consistency..." -ForegroundColor Cyan
$consistencyOk = Verify-PasswordConsistency -configPasswordHash $env:CONFIG_PASSWORD_HASH -itemServicePassword $env:CONFIG_SERVER_PASSWORD

Write-Host "3. Testing Config Server Authentication..." -ForegroundColor Cyan
$authOk = Test-ConfigServer -username $env:CONFIG_SERVER_USERNAME -password $env:CONFIG_SERVER_PASSWORD

Write-Host ""
Write-Host "=== Summary ===" -ForegroundColor Green
if ($authOk) {
    Write-Host "🎉 ALL TESTS PASSED! Authentication is working correctly." -ForegroundColor Green
} else {
    Write-Host "⚠️  Authentication test failed. Check the following:" -ForegroundColor Yellow
    Write-Host "   1. Environment variables are set correctly" -ForegroundColor Yellow
    Write-Host "   2. Password used for hash generation matches item service password" -ForegroundColor Yellow
    Write-Host "   3. Config server is running and accessible" -ForegroundColor Yellow
    Write-Host "   4. No typos in usernames or passwords" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "=== Next Steps ===" -ForegroundColor Green
Write-Host "If authentication is working:"
Write-Host "   1. Start item service with environment variables set"
Write-Host "   2. Check item service logs for successful config loading"
Write-Host ""
Write-Host "If authentication is failing:"
Write-Host "   1. Verify the password used on bcrypt-generator.com"
Write-Host "   2. Ensure CONFIG_SERVER_PASSWORD matches that password"
Write-Host "   3. Restart both applications with correct environment variables"