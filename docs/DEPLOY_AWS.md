# Guía de Deploy en AWS — Innovatech Solutions

Esta guía explica paso a paso cómo configurar la infraestructura en AWS Academy y activar el pipeline de CI/CD para desplegar la aplicación automáticamente desde GitHub.

---

## Tabla de contenidos

1. [Arquitectura general](#1-arquitectura-general)
2. [Requisitos previos](#2-requisitos-previos)
3. [Paso 1 — Crear la VPC y las redes](#3-paso-1--crear-la-vpc-y-las-redes)
4. [Paso 2 — Crear el NAT Gateway](#4-paso-2--crear-el-nat-gateway)
5. [Paso 3 — Crear el IAM Role](#5-paso-3--crear-el-iam-role)
6. [Paso 4 — Crear los Security Groups](#6-paso-4--crear-los-security-groups)
7. [Paso 5 — Lanzar las 3 instancias EC2](#7-paso-5--lanzar-las-3-instancias-ec2)
8. [Paso 6 — Instalar Docker en cada instancia](#8-paso-6--instalar-docker-en-cada-instancia)
9. [Paso 7 — Crear los repositorios ECR](#9-paso-7--crear-los-repositorios-ecr)
10. [Paso 8 — Configurar los Secrets en GitHub](#10-paso-8--configurar-los-secrets-en-github)
11. [Paso 9 — Activar el deploy](#11-paso-9--activar-el-deploy)
12. [Verificación final](#12-verificación-final)
13. [Renovar credenciales de AWS Academy](#13-renovar-credenciales-de-aws-academy)
14. [Solución de problemas comunes](#14-solución-de-problemas-comunes)

---

## 1. Arquitectura general

La aplicación se despliega en **3 instancias EC2 separadas**, cada una con una responsabilidad distinta:

```
Internet (usuarios / navegador)
         │
         │ puerto 3000 (UI)
         │ puerto 9000 (API)
         ▼
┌─────────────────────────────────┐
│        EC2-Frontend             │  ← Subnet PÚBLICA
│  Next.js (3000)                 │     IP pública: SÍ
│  API Gateway (9000)             │
└────────────────┬────────────────┘
                 │ IP privada (dentro de la VPC)
                 │ puertos 8081, 8082, 8083
                 ▼
┌─────────────────────────────────┐
│        EC2-Backend              │  ← Subnet PRIVADA
│  project-service   (8081)       │     IP pública: NO
│  resource-service  (8082)       │     Sale a internet vía NAT Gateway
│  analytics-service (8083)       │
└────────────────┬────────────────┘
                 │ IP privada (dentro de la VPC)
                 │ puertos 5434, 5435, 5436
                 ▼
┌─────────────────────────────────┐
│        EC2-Database             │  ← Subnet PRIVADA
│  PostgreSQL proyectos  (5435)   │     IP pública: NO
│  PostgreSQL recursos   (5434)   │     Sale a internet vía NAT Gateway
│  PostgreSQL analytics  (5436)   │
└─────────────────────────────────┘
```

**Por qué esta arquitectura:**
- La base de datos y los microservicios nunca están expuestos a internet directamente, lo que reduce la superficie de ataque.
- El API Gateway actúa como único punto de entrada al sistema desde el exterior.
- El NAT Gateway permite que las instancias privadas salgan a internet (para descargar imágenes Docker, comunicarse con AWS SSM) sin recibir conexiones entrantes.

---

## 2. Requisitos previos

- Cuenta de **AWS Academy** activa con créditos disponibles.
- Acceso al repositorio de GitHub con permisos de **Settings → Secrets**.
- **Región**: todo se crea en `us-east-1` (N. Virginia).

---

## 3. Paso 1 — Crear la VPC y las redes

### 3.1 Crear la VPC

```
AWS Console → VPC → Your VPCs → Create VPC
```

| Campo | Valor |
|-------|-------|
| Name tag | `innovatech-vpc` |
| IPv4 CIDR block | `10.0.0.0/16` |
| Tenancy | Default |

### 3.2 Crear las subredes

```
VPC → Subnets → Create subnet
```

Crear **2 subredes** dentro de la VPC `innovatech-vpc`:

**Subred pública** (EC2-Frontend vivirá aquí):

| Campo | Valor |
|-------|-------|
| Name | `innovatech-subnet-public` |
| Availability Zone | `us-east-1a` |
| IPv4 CIDR | `10.0.1.0/24` |

**Subred privada** (EC2-Backend y EC2-Database vivirán aquí):

| Campo | Valor |
|-------|-------|
| Name | `innovatech-subnet-private` |
| Availability Zone | `us-east-1a` |
| IPv4 CIDR | `10.0.2.0/24` |

### 3.3 Crear el Internet Gateway

El Internet Gateway permite que la subred pública tenga acceso a internet.

```
VPC → Internet Gateways → Create internet gateway
```

| Campo | Valor |
|-------|-------|
| Name | `innovatech-igw` |

Luego de crearlo, **adjuntarlo a la VPC**:
```
Actions → Attach to VPC → seleccionar innovatech-vpc
```

### 3.4 Crear la tabla de rutas de la subred pública

```
VPC → Route Tables → Create route table
```

| Campo | Valor |
|-------|-------|
| Name | `innovatech-rtb-public` |
| VPC | `innovatech-vpc` |

Agregar ruta de internet:
```
Seleccionar la tabla → Routes → Edit routes → Add route
Destino: 0.0.0.0/0
Target: Internet Gateway → innovatech-igw
```

Asociar la subred pública:
```
Subnet associations → Edit subnet associations → innovatech-subnet-public
```

---

## 4. Paso 2 — Crear el NAT Gateway

El NAT Gateway permite que las instancias de la subred privada salgan a internet (necesario para que el SSM Agent se comunique con AWS y para descargar imágenes Docker desde ECR).

> **Importante:** el NAT Gateway debe crearse en la **subred pública**, no en la privada.

```
VPC → NAT Gateways → Create NAT gateway
```

| Campo | Valor |
|-------|-------|
| Name | `innovatech-nat` |
| Subnet | `innovatech-subnet-public` ← obligatorio |
| Connectivity type | **Public** ← obligatorio |
| Elastic IP | Click en **Allocate Elastic IP** |

### 4.1 Crear la tabla de rutas de la subred privada

```
VPC → Route Tables → Create route table
```

| Campo | Valor |
|-------|-------|
| Name | `innovatech-rtb-private` |
| VPC | `innovatech-vpc` |

Agregar ruta hacia el NAT:
```
Seleccionar la tabla → Routes → Edit routes → Add route
Destino: 0.0.0.0/0
Target: NAT Gateway → innovatech-nat
```

Asociar la subred privada:
```
Subnet associations → Edit subnet associations → innovatech-subnet-private
```

---

## 5. Paso 3 — IAM Role (AWS Academy)

> **AWS Academy ya tiene un role creado por defecto llamado `LabRole`.** No es necesario crear uno nuevo ni agregarle políticas — ya tiene los permisos necesarios para SSM y ECR.

El `LabRole` incluye permisos amplios que cubren:
- Comunicación con AWS SSM (permite que GitHub Actions ejecute comandos en las EC2 sin SSH)
- Descarga de imágenes desde ECR
- Acceso general a los servicios de AWS del laboratorio

**Al crear cada EC2, en el campo IAM Instance Profile seleccionar: `LabRole`**

Así se ve en la consola al lanzar una instancia:
```
Advanced details → IAM instance profile → LabRole
```

> Este mismo role se asigna a las **3 instancias EC2**.

---

## 6. Paso 4 — Crear los Security Groups

Los Security Groups controlan qué tráfico puede entrar y salir de cada instancia.

```
EC2 → Security Groups → Create security group
```

### SG para EC2-Frontend (`sg-frontend`)

VPC: `innovatech-vpc`

**Reglas de entrada (Inbound):**

| Tipo | Puerto | Origen | Por qué |
|------|--------|--------|---------|
| Custom TCP | 3000 | 0.0.0.0/0 | Acceso público al frontend Next.js |
| Custom TCP | 9000 | 0.0.0.0/0 | Acceso público al API Gateway (los navegadores lo necesitan) |

**Reglas de salida (Outbound):** dejar el valor por defecto (Allow all).

---

### SG para EC2-Backend (`sg-backend`)

VPC: `innovatech-vpc`

**Reglas de entrada (Inbound):**

| Tipo | Puerto | Origen | Por qué |
|------|--------|--------|---------|
| Custom TCP | 8081 | `sg-frontend` | Solo el API Gateway puede llamar a project-service |
| Custom TCP | 8082 | `sg-frontend` | Solo el API Gateway puede llamar a resource-service |
| Custom TCP | 8083 | `sg-frontend` | Solo el API Gateway puede llamar a analytics-service |

> No se abre el puerto 22 (SSH) porque el acceso se hace a través de AWS SSM.

**Reglas de salida (Outbound):** dejar el valor por defecto (Allow all).

---

### SG para EC2-Database (`sg-database`)

VPC: `innovatech-vpc`

**Reglas de entrada (Inbound):**

| Tipo | Puerto | Origen | Por qué |
|------|--------|--------|---------|
| Custom TCP | 5435 | `sg-backend` | Solo los microservicios acceden a la DB de proyectos |
| Custom TCP | 5434 | `sg-backend` | Solo los microservicios acceden a la DB de recursos |
| Custom TCP | 5436 | `sg-backend` | Solo los microservicios acceden a la DB de analytics |

**Reglas de salida (Outbound):** dejar el valor por defecto (Allow all).

---

## 7. Paso 5 — Lanzar las 3 instancias EC2

```
EC2 → Instances → Launch instances
```

### EC2-Frontend

| Campo | Valor |
|-------|-------|
| Name | `innovatech-frontend` |
| AMI | Amazon Linux 2023 |
| Instance type | `t3.small` |
| Key pair | No requiere (se usa SSM) |
| VPC | `innovatech-vpc` |
| Subnet | `innovatech-subnet-public` |
| Auto-assign public IP | **Enable** |
| Security Group | `sg-frontend` |
| IAM Instance Profile | `LabRole` |

**User data** (pegar en Advanced → User data):

```bash
#!/bin/bash
dnf update -y
dnf install -y docker
systemctl enable docker
systemctl start docker
usermod -aG docker ec2-user
curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-linux-x86_64" \
  -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose
mkdir -p /home/ec2-user/innovatech
chown ec2-user:ec2-user /home/ec2-user/innovatech
systemctl enable amazon-ssm-agent
systemctl start amazon-ssm-agent
```

---

### EC2-Backend

| Campo | Valor |
|-------|-------|
| Name | `innovatech-backend` |
| AMI | Amazon Linux 2023 |
| Instance type | `t3.medium` |
| Key pair | No requiere (se usa SSM) |
| VPC | `innovatech-vpc` |
| Subnet | `innovatech-subnet-private` |
| Auto-assign public IP | **Disable** |
| Security Group | `sg-backend` |
| IAM Instance Profile | `LabRole` |

**User data:** el mismo script que EC2-Frontend.

---

### EC2-Database

| Campo | Valor |
|-------|-------|
| Name | `innovatech-database` |
| AMI | Amazon Linux 2023 |
| Instance type | `t3.medium` |
| Key pair | No requiere (se usa SSM) |
| VPC | `innovatech-vpc` |
| Subnet | `innovatech-subnet-private` |
| Auto-assign public IP | **Disable** |
| Security Group | `sg-database` |
| IAM Instance Profile | `LabRole` |

**User data:** el mismo script que EC2-Frontend.

---

### Verificar que SSM detecta las instancias

Esperar 3-5 minutos después de lanzar las instancias, luego ir a:

```
Systems Manager → Fleet Manager
```

Las 3 instancias deben aparecer con estado **Online**. Si no aparecen:
- Verificar que el IAM Role está asignado correctamente.
- Verificar que la subred privada tiene la ruta `0.0.0.0/0 → NAT Gateway`.

---

## 8. Paso 6 — Instalar Docker en cada instancia

> Si el User Data se ejecutó correctamente, Docker ya está instalado. Verificar con el siguiente paso antes de continuar.

Conectarse a cada instancia usando Session Manager:

```
EC2 → Instances → seleccionar instancia → Connect → Session Manager → Connect
```

Verificar Docker en cada una:

```bash
docker --version
docker-compose --version
systemctl status docker
```

Si Docker no está instalado (el User Data puede fallar en la primera ejecución), instalar manualmente:

```bash
sudo dnf install -y docker
sudo systemctl enable docker
sudo systemctl start docker
sudo usermod -aG docker ec2-user
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-linux-x86_64" \
  -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

---

## 9. Paso 7 — Crear los repositorios ECR

ECR (Elastic Container Registry) es el registro privado de Docker en AWS donde se almacenan las imágenes de la aplicación.

```
ECR → Repositories → Create repository
```

Crear **5 repositorios** con visibilidad **Private**:

| Nombre del repositorio |
|------------------------|
| `innovatech-api-gateway` |
| `innovatech-project-service` |
| `innovatech-resource-service` |
| `innovatech-analytics-service` |
| `innovatech-frontend` |

> Las demás opciones se dejan por defecto.

---

## 10. Paso 8 — Configurar los Secrets en GitHub

Los secrets son variables cifradas que GitHub Actions usa durante el pipeline. Nunca quedan expuestos en los logs.

```
GitHub → Repositorio → Settings → Secrets and variables → Actions → New repository secret
```

### Secrets de AWS Academy (se renuevan cada ~4 horas)

Obtenerlos desde:
```
AWS Academy → Módulo del laboratorio → AWS Details → AWS CLI
```

| Secret | Descripción |
|--------|-------------|
| `AWS_ACCESS_KEY_ID` | Clave de acceso temporal de la sesión |
| `AWS_SECRET_ACCESS_KEY` | Clave secreta temporal de la sesión |
| `AWS_SESSION_TOKEN` | Token de sesión (obligatorio en Academy) |

### Secrets fijos (no cambian)

| Secret | Dónde encontrarlo |
|--------|-------------------|
| `AWS_ACCOUNT_ID` | Consola AWS → esquina superior derecha → número de 12 dígitos |
| `EC2_FRONTEND_INSTANCE_ID` | EC2 → instancia `innovatech-frontend` → campo Instance ID (ej: `i-0abc123...`) |
| `EC2_BACKEND_INSTANCE_ID` | EC2 → instancia `innovatech-backend` → campo Instance ID |
| `EC2_DB_INSTANCE_ID` | EC2 → instancia `innovatech-database` → campo Instance ID |
| `EC2_FRONTEND_PUBLIC_IP` | EC2 → instancia `innovatech-frontend` → Public IPv4 address |
| `EC2_BACKEND_PRIVATE_IP` | EC2 → instancia `innovatech-backend` → Private IPv4 address |
| `EC2_DB_PRIVATE_IP` | EC2 → instancia `innovatech-database` → Private IPv4 address |

**Por qué cada IP:**

- `EC2_FRONTEND_PUBLIC_IP`: la imagen del frontend se construye con la URL del API Gateway embebida. Como el API Gateway corre en EC2-Frontend, se usa su IP pública para que los navegadores de los usuarios puedan alcanzarlo.
- `EC2_BACKEND_PRIVATE_IP`: el API Gateway necesita saber la dirección interna de los microservicios para enrutar las peticiones. Usa la IP privada porque ambas instancias están dentro de la misma VPC.
- `EC2_DB_PRIVATE_IP`: los microservicios se conectan a la base de datos usando su IP privada dentro de la VPC.

---

## 11. Paso 9 — Activar el deploy

El pipeline de deploy se activa automáticamente cuando se hace push a la rama `deploy`.

```bash
git checkout deploy
git push origin deploy
```

O también se puede activar manualmente desde GitHub:
```
GitHub → Actions → Deploy to AWS EC2 → Run workflow → Run workflow
```

### Qué hace el pipeline

```
push a rama "deploy"
        │
        ├─ backend-tests  → corre todos los tests de Maven (Spring Boot)
        ├─ frontend-tests → corre lint y tests unitarios (Vitest)
        │
        ▼ solo si ambos pasan
        Build & Push to ECR
        │  - Construye las 5 imágenes Docker
        │  - Las sube al ECR con el tag :latest
        │  - La imagen del frontend incluye la URL del API Gateway
        │
        ▼
        Deploy Database (via SSM)
        │  - Envía docker-compose.db.yml a EC2-Database
        │  - Levanta los 3 contenedores PostgreSQL
        │
        ▼
        Deploy Backend (via SSM)
        │  - Descarga las imágenes desde ECR
        │  - Levanta los 3 microservicios apuntando a la IP de la DB
        │
        ▼
        Deploy Frontend (via SSM)
           - Descarga las imágenes desde ECR
           - Levanta Next.js y API Gateway apuntando a la IP del backend
```

**Qué es SSM y por qué lo usamos:**
AWS Systems Manager (SSM) permite ejecutar comandos en las instancias EC2 sin necesidad de abrir el puerto SSH (22) ni gestionar claves privadas. GitHub Actions se autentica con AWS usando los credentials del secret y envía comandos que el SSM Agent (proceso que corre dentro de cada EC2) ejecuta de forma segura.

---

## 12. Verificación final

Una vez que el pipeline termina en verde, verificar que la aplicación está funcionando:

**Frontend:**
```
http://<EC2_FRONTEND_PUBLIC_IP>:3000
```

**API Gateway:**
```
http://<EC2_FRONTEND_PUBLIC_IP>:9000/api/auth/login
```
Debe responder con JSON (400 o 401, no un error de conexión).

**Verificar SSM desde la consola:**
```
Systems Manager → Fleet Manager → las 3 instancias deben estar Online
```

**Verificar imágenes en ECR:**
```
ECR → Repositories → cada repositorio debe tener al menos 1 imagen con tag :latest
```

---

## 13. Renovar credenciales de AWS Academy

Las credenciales de AWS Academy expiran aproximadamente cada **4 horas**. Cuando el pipeline falla con el error:

```
Error: The security token included in the request is expired
```

Se deben renovar los 3 secrets:

1. Ir a **AWS Academy → Módulo del laboratorio → AWS Details → AWS CLI**
2. Copiar los nuevos valores de `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY` y `AWS_SESSION_TOKEN`
3. Actualizar los 3 secrets en **GitHub → Settings → Secrets and variables → Actions**
4. Volver a correr el workflow desde **Actions → Re-run jobs**

> No es necesario hacer un nuevo push. El botón "Re-run jobs" vuelve a ejecutar el pipeline con los secrets actualizados.

---

## 14. Solución de problemas comunes

### Las instancias no aparecen en Fleet Manager

**Causa:** el IAM Role no está asignado o el NAT Gateway no está configurado correctamente.

**Verificar:**
1. EC2 → instancia → Security → IAM Role → debe decir `LabRole`
2. VPC → Route Tables → tabla de la subred privada → debe tener `0.0.0.0/0 → nat-xxx`
3. VPC → NAT Gateways → el NAT debe estar en la **subred pública** con una **Elastic IP** asignada y estado **Available**

---

### Error `InvalidInstanceId` al hacer deploy

**Causa:** SSM no reconoce la instancia. Normalmente el IAM Role no está asignado.

**Solución:**
```
EC2 → seleccionar instancia → Actions → Security → Modify IAM role → LabRole
```

---

### Docker no está instalado en la instancia

**Causa:** el User Data no se ejecutó al crear la instancia.

**Solución:** conectarse via Session Manager y ejecutar:
```bash
sudo dnf install -y docker
sudo systemctl enable docker
sudo systemctl start docker
sudo usermod -aG docker ec2-user
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-linux-x86_64" \
  -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

---

### El frontend muestra error de conexión al API

**Causa:** `EC2_FRONTEND_PUBLIC_IP` en los secrets no coincide con la IP actual de la instancia.

**Nota:** las instancias EC2 cambian de IP pública cada vez que se detienen y vuelven a iniciar. Para evitar esto, asignar una **Elastic IP** a EC2-Frontend:
```
EC2 → Elastic IPs → Allocate Elastic IP → Associate → seleccionar EC2-Frontend
```
Luego actualizar el secret `EC2_FRONTEND_PUBLIC_IP` con la nueva IP fija y hacer un nuevo push.

---

### El deploy del backend falla con error de conexión a la DB

**Causa:** `EC2_DB_PRIVATE_IP` incorrecto, o la base de datos aún no terminó de iniciar.

**Verificar** la IP privada de EC2-Database:
```
EC2 → instancia innovatech-database → Private IPv4 address
```
Actualizar el secret `EC2_DB_PRIVATE_IP` si no coincide.
