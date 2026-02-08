# Spring Cloud Config Server Security Setup Guide

This guide explains how to secure your Spring Cloud Config Server and client applications using environment variables and BCrypt hashing.

## Overview

The security implementation removes hardcoded credentials from source code and configuration files, replacing them with environment variables and pre-hashed passwords for enhanced security.

## Security Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        Config Server                            │
│  ┌─────────────────┐    Environment Variables    ┌─────────────┐ │
│  │ SecurityConfig  │  ────────────────────────→ │ CONFIG_USER   │ │
│  │                 │                             │ CONFIG_PASS   │ │
│  │ Uses BCrypt     │                             │ _HASH         │ │
│  │ Hashed Password │                             └─────────────┘ │
│  └─────────────────┘                                             │
└─────────────────────────────────────────────────────────────────┘
                                │
                                │ HTTP Basic Auth (Plain Text)
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                        Item Service                             │
│  ┌─────────────────┐    Environment Variables    ┌─────────────┐ │
│  │ bootstrap.yml   │  ────────────────────────→ │ CONFIG_SERV │ │
│  │                 │                             │ ER_USERNAME │ │
│  │ Sends Plain     │                             │ CONFIG_SERV │ │
│  │ Text Password   │                             │ ER_PASSWORD │ │
│  └─────────────────┘                             └─────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

## Step 1: Generate BCrypt Hash

### Option 1: Online BCrypt Generator (Recommended)
1. Visit [bcrypt-generator.com](https://bcrypt-generator.com)
2. Enter your password (e.g., "configpass")
3. Set rounds to 12 (recommended)
4. Copy the generated hash: `$2a$12$IYMqMmmaKE/qWKzP6pSBhwif8NKWH70ZoUPIkEHevCg+uLYdP5I48`

### Option 2: Command Line (if available)
```bash
# Using bcrypt command (if installed)
bcrypt configpass

# Using Node.js
node -e "console.log(require('bcrypt').hashSync('configpass', 12))"
```

## Step 2: Configure Config Server

### Set Environment Variables
```bash
# Linux/macOS
export CONFIG_USERNAME="configuser"
export CONFIG_PASSWORD_HASH="$2a$12$IYMqMmmaKE/qWKzP6pSBhwif8NKWH70ZoUPIkEHevCg+uLYdP5I48"

# Windows Command Prompt
set CONFIG_USERNAME=configuser
set CONFIG_PASSWORD_HASH=$2a$12$IYMqMmmaKE/qWKzP6pSBhwif8NKWH70ZoUPIkEHevCg+uLYdP5I48

# Windows PowerShell
$env:CONFIG_USERNAME="configuser"
$env:CONFIG_PASSWORD_HASH="$2a$12$IYMqMmmaKE/qWKzP6pSBhwif8NKWH70ZoUPIkEHevCg+uLYdP5I48"
```

### Start Config Server
```bash
cd config-server
mvn spring-boot:run
```

## Step 3: Configure Item Service Client

### Set Client Environment Variables
```bash
# Linux/macOS
export CONFIG_SERVER_USERNAME="configuser"
export CONFIG_SERVER_PASSWORD="configpass"  # Plain text password

# Windows Command Prompt
set CONFIG_SERVER_USERNAME=configuser
set CONFIG_SERVER_PASSWORD=configpass

# Windows PowerShell
$env:CONFIG_SERVER_USERNAME="configuser"
$env:CONFIG_SERVER_PASSWORD="configpass"
```

### Start Item Service
```bash
cd item-service
mvn spring-boot:run
```

## Environment Variable Reference

### Config Server Variables
| Variable Name | Description | Example |
|---------------|-------------|---------|
| `CONFIG_USERNAME` | Username for basic authentication | `configuser` |
| `CONFIG_PASSWORD_HASH` | BCrypt hash of the password | `$2a$12$IYMqMmmaKE/qWKzP6pSBhwif8NKWH70ZoUPIkEHevCg+uLYdP5I48` |

### Client Variables
| Variable Name | Description | Example |
|---------------|-------------|---------|
| `CONFIG_SERVER_USERNAME` | Username for config server | `configuser` |
| `CONFIG_SERVER_PASSWORD` | Plain text password for config server | `configpass` |
| `CONFIG_SERVER_URI` | Config server URL (optional) | `http://localhost:8888/config` |

## Production Deployment

### Docker

#### Config Server
```dockerfile
FROM openjdk:21-jre-slim
COPY target/config-server.jar app.jar
ENV CONFIG_USERNAME=configuser
ENV CONFIG_PASSWORD_HASH=$2a$12$IYMqMmmaKE/qWKzP6pSBhwif8NKWH70ZoUPIkEHevCg+uLYdP5I48
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

#### Item Service
```dockerfile
FROM openjdk:21-jre-slim
COPY target/item-service.jar app.jar
ENV CONFIG_SERVER_USERNAME=configuser
ENV CONFIG_SERVER_PASSWORD=configpass
ENV CONFIG_SERVER_URI=http://config-server:8888/config
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Kubernetes

#### Config Server Secrets
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: config-server-secrets
type: Opaque
stringData:
  CONFIG_USERNAME: configuser
  CONFIG_PASSWORD_HASH: $2a$12$IYMqMmmaKE/qWKzP6pSBhwif8NKWH70ZoUPIkEHevCg+uLYdP5I48
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: config-server
spec:
  template:
    spec:
      containers:
      - name: config-server
        envFrom:
        - secretRef:
            name: config-server-secrets
```

#### Item Service Secrets
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: item-service-secrets
type: Opaque
stringData:
  CONFIG_SERVER_USERNAME: configuser
  CONFIG_SERVER_PASSWORD: configpass
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: item-service
spec:
  template:
    spec:
      containers:
      - name: item-service
        envFrom:
        - secretRef:
            name: item-service-secrets
        - configMapRef:
            name: config-server-config
```

### AWS ECS/EKS

Use AWS Secrets Manager or Parameter Store:

```bash
# Store in AWS Secrets Manager
aws secretsmanager create-secret \
  --name "config-server-credentials" \
  --secret-string '{"CONFIG_USERNAME":"configuser","CONFIG_PASSWORD_HASH":"$2a$12$IYMqMmmaKE/qWKzP6pSBhwif8NKWH70ZoUPIkEHevCg+uLYdP5I48"}'

aws secretsmanager create-secret \
  --name "item-service-config-credentials" \
  --secret-string '{"CONFIG_SERVER_USERNAME":"configuser","CONFIG_SERVER_PASSWORD":"configpass"}'
```

## Testing the Configuration

### Test Config Server Authentication
```bash
# Test with curl
curl -u configuser:configpass http://localhost:8888/config/item-service/dev

# Should return configuration JSON
```

### Test Item Service Connection
```bash
# Start item service with environment variables set
cd item-service
mvn spring-boot:run

# Check logs for successful config server connection
# Look for: "Located environment: name=item-service, profiles=[dev], label=null"
```

## Troubleshooting

### Config Server Won't Start
```
Error: Environment variable 'CONFIG_USERNAME' must be set
```
**Solution:** Set the required environment variables before starting the server.

### Item Service Can't Connect to Config Server
```
Error: 401 Unauthorized
```
**Solution:** 
1. Verify `CONFIG_SERVER_USERNAME` and `CONFIG_SERVER_PASSWORD` are set correctly
2. Ensure the password matches the one used to generate the BCrypt hash
3. Check that the config server is running and accessible

### Environment Variables Not Found
```bash
# Check if variables are set
echo $CONFIG_USERNAME
echo $CONFIG_PASSWORD_HASH
```

**Solution:** Restart your terminal or set variables in the current session.

## Security Best Practices

1. **Use Strong Passwords**: Generate complex passwords for your config server
2. **Rotate Credentials Regularly**: Change passwords periodically
3. **Environment Isolation**: Use different credentials for dev/staging/production
4. **Use HTTPS in Production**: Encrypt communication between client and server
5. **Limit Access**: Only grant access to necessary team members
6. **Monitor Access**: Log authentication attempts for security monitoring
7. **Use Secret Management**: Use Kubernetes secrets, AWS Secrets Manager, or similar tools

## Migration from Hardcoded Credentials

If migrating from previous hardcoded configuration:

1. **Generate new password hash** using the steps above
2. **Set environment variables** for config server
3. **Set environment variables** for client applications
4. **Remove hardcoded credentials** from source code and config files
5. **Test the new configuration** thoroughly
6. **Update deployment scripts** to set environment variables

## Next Steps

1. **Set environment variables** in your deployment environment
2. **Generate new password hash** using the provided methods
3. **Update deployment scripts** to set environment variables
4. **Test authentication** with the new configuration
5. **Remove old hardcoded credentials** from any remaining configuration

This implementation provides a secure, production-ready solution for managing Spring Cloud Config Server credentials while maintaining the existing BCrypt security infrastructure.