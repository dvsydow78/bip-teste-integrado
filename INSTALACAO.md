# ⚙️ Guia de Instalação e Setup - BIP Teste Integrado

## 📋 Índice
1. [Requisitos de Sistema](#requisitos)
2. [Instalação do JDK](#jdk)
3. [Instalação do Maven](#maven)
4. [Instalação do Node.js](#nodejs)
5. [Configuração do Banco de Dados](#banco)
6. [Configuração do Projeto](#projeto)
7. [Verificação de Instalação](#verificação)
8. [Troubleshooting](#troubleshooting)

---

## 💻 Requisitos de Sistema

### Hardware Mínimo

| Recurso | Mínimo | Recomendado |
|---------|--------|------------|
| CPU | 2 cores | 4+ cores |
| RAM | 4 GB | 8+ GB |
| HD | 5 GB livres | 10+ GB |
| OS | Windows/Mac/Linux | - |

### Sistema Operacional

✅ **Suportados**:
- Windows 10/11
- macOS 10.15+
- Ubuntu 18.04 LTS+
- CentOS 7+
- Debian 10+

---

## ☕ Instalação do JDK 17

### Windows

#### Opção 1: Installer Automático

1. Acessar [Oracle JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
2. Fazer download: `jdk-17.x.x_windows-x64_bin.exe`
3. Executar installer com admin
4. Seguir wizard:
   - Install Path: `C:\Program Files\Java\jdk-17.x.x` (padrão)
   - Próximo até conclusão
5. Reiniciar o computador

#### Opção 2: Chocolatey (Package Manager)

```bash
# Instalar Chocolatey (se não tiver)
# https://chocolatey.org/install

# Instalar JDK 17
choco install openjdk17 -y

# Verificar
java -version
```

#### Opção 3: Portable (Sem Instalação)

1. Download: [Eclipse Adoptium](https://adoptium.net/temurin/releases/?version=17)
2. Extrair para: `C:\Program Files\jdk-17`
3. Configurar PATH (ver seção Configuração)

### Linux

#### Ubuntu/Debian

```bash
# Atualizar package list
sudo apt update

# Instalar OpenJDK 17
sudo apt install openjdk-17-jdk openjdk-17-jre -y

# Verificar
java -version
```

#### CentOS/RHEL

```bash
sudo yum install java-17-openjdk java-17-openjdk-devel -y
java -version
```

#### Fedora

```bash
sudo dnf install java-17-openjdk java-17-openjdk-devel -y
```

### macOS

#### Homebrew (Recomendado)

```bash
# Instalar Homebrew (se não tiver)
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Instalar JDK 17
brew install openjdk@17

# Criar symlink
sudo ln -sfn /usr/local/opt/openjdk@17/libexec/openjdk.jdk \
  /Library/Java/JavaVirtualMachines/openjdk-17.jdk

# Verificar
java -version
```

#### Manual

1. Download: [Eclipse Adoptium](https://adoptium.net/)
2. Descompactar em `/Library/Java/JavaVirtualMachines/`
3. Verificar instalação

### Configuração de Variáveis de Ambiente

#### Windows

**Para Windows 10/11**:

1. **Método 1: GUI**
   - Pesquisar "Variáveis de ambiente"
   - Clique em "Editar variáveis de ambiente do sistema"
   - Clique em "Variáveis de Ambiente..."
   - Nova variável:
     - **Nome**: `JAVA_HOME`
     - **Valor**: `C:\Program Files\Java\jdk-17.x.x`
   - Editar `Path`:
     - Adicionar: `C:\Program Files\Java\jdk-17.x.x\bin`
   - OK e restart terminal

**Para Verificar**:
```bash
echo %JAVA_HOME%
# Deve mostrar: C:\Program Files\Java\jdk-17.x.x

java -version
# Deve mostrar: openjdk version "17.x.x"
```

#### Linux/macOS

Adicionar ao `~/.bash_profile` ou `~/.zshrc`:

```bash
# Bash
echo 'export JAVA_HOME=/usr/libexec/java_home -v 17' >> ~/.bash_profile
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bash_profile

# Zsh (macOS)
echo 'export JAVA_HOME=/usr/libexec/java_home -v 17' >> ~/.zshrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.zshrc

# Recarregar
source ~/.bash_profile  # ou source ~/.zshrc
```

---

## 🔨 Instalação do Maven 3.9+

### Windows

#### Opção 1: Instalador

1. Download: [Apache Maven](https://maven.apache.org/download.cgi)
   - Arquivo: `apache-maven-3.9.x-bin.zip`
2. Extrair em: `C:\Program Files\Apache\maven-3.9.x`
3. Configurar variáveis:
   - **MAVEN_HOME**: `C:\Program Files\Apache\maven-3.9.x`
   - **PATH**: Adicionar `%MAVEN_HOME%\bin`

#### Opção 2: Chocolatey

```bash
choco install maven -y
mvn -version
```

### Linux/macOS

#### Ubuntu/Debian

```bash
sudo apt install maven -y
mvn -version
```

#### Homebrew (macOS)

```bash
brew install maven

# Se houver múltiplas versões
brew link maven@3.9
```

#### Manual (Linux/macOS)

```bash
# Download
wget https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz

# Extrair
tar -xzf apache-maven-3.9.6-bin.tar.gz
sudo mv apache-maven-3.9.6 /opt/maven

# Configurar PATH
echo 'export PATH=/opt/maven/bin:$PATH' >> ~/.bashrc
source ~/.bashrc
```

### Verificação

```bash
mvn -version
# Apache Maven 3.9.x
# Maven home: ...
# Java version: 17.x.x
```

### Configuração do settings.xml (Opcional)

Editar `~/.m2/settings.xml`:

```xml
<settings>
  <mirrors>
    <mirror>
      <id>aliyun</id>
      <mirrorOf>central</mirrorOf>
      <name>Aliyun Maven Repository</name>
      <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
  </mirrors>
</settings>
```

---

## 📦 Instalação do Node.js e npm

### Windows

#### Opção 1: Installer

1. Download: [nodejs.org](https://nodejs.org/) - LTS recomendado
   - Arquivo: `node-vxx.x.x-x64.msi`
2. Executar como admin
3. Próximo até conclusão
4. Reiniciar

#### Opção 2: Chocolatey

```bash
choco install nodejs -y
```

#### Verificar

```bash
node --version
npm --version
```

### Linux

#### Ubuntu/Debian

```bash
# Opção 1: Repositório padrão
sudo apt update
sudo apt install nodejs npm -y

# Opção 2: NodeSource (versão mais nova)
curl -sL https://deb.nodesource.com/setup_20.x | sudo -E bash -
sudo apt install nodejs -y
```

#### CentOS/RHEL

```bash
curl -sL https://rpm.nodesource.com/setup_20.x | sudo bash -
sudo yum install nodejs -y
```

### macOS

#### Homebrew

```bash
brew install node
```

#### MacPorts

```bash
sudo port install nodejs20 +universal
```

### Verificação

```bash
node --version    # v20.x.x
npm --version     # 11.x.x
```

### Atualizar npm (se necessário)

```bash
# Windows (PowerShell como admin)
npm install -g npm@latest

# Linux/macOS
sudo npm install -g npm@latest
```

---

## 🗄️ Configuração do Banco de Dados

### MySQL 8.0+

#### Windows

1. Download: [MySQL Installer](https://dev.mysql.com/downloads/installer/)
2. Executar `mysql-installer-web-community-x.x.x.x.msi`
3. Setup Wizard:
   - Setup Type: Developer Default
   - Product Configuration
   - MySQL Server Configuration:
     - Port: 3306
     - Server Type: Development
   - MySQL Server User Accounts:
     - Root Password: sua_senha_segura
   - Configure MySQL as a Windows Service
   - Start MySQL Server

#### Linux (Ubuntu)

```bash
# Instalar
sudo apt update
sudo apt install mysql-server -y

# Secure installation
sudo mysql_secure_installation

# Iniciar serviço
sudo systemctl start mysql
sudo systemctl enable mysql
```

#### macOS

```bash
# Homebrew
brew install mysql

# Iniciar
brew services start mysql

# Configurar
mysql_secure_installation
```

### Criar Banco de Dados e Usuário

```bash
# Conectar ao MySQL
mysql -u root -p
# Digite sua senha

# SQL Commands
CREATE DATABASE bip_teste;
CREATE USER 'bip_user'@'localhost' IDENTIFIED BY 'bip_password';
GRANT ALL PRIVILEGES ON bip_teste.* TO 'bip_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

### Executar Scripts SQL

```bash
# Conectar com novo usuário
mysql -u bip_user -p bip_teste

# Dentro do MySQL
SOURCE /path/to/db/schema.sql;
SOURCE /path/to/db/seed.sql;
```

### PostgreSQL (Alternativa)

#### Instalação

**Windows**: Download [PostgreSQL](https://www.postgresql.org/download/)

**Linux**:
```bash
sudo apt install postgresql postgresql-contrib -y
sudo systemctl start postgresql
```

#### Criar Banco

```bash
# Conectar como postgres
psql -U postgres

# SQL Commands
CREATE DATABASE bip_teste;
CREATE USER bip_user WITH PASSWORD 'bip_password';
GRANT ALL PRIVILEGES ON DATABASE bip_teste TO bip_user;
\c bip_teste
\i /path/to/db/schema.sql
\i /path/to/db/seed.sql
```

---

## 🚀 Configuração do Projeto

### 1. Clonar Repositório

```bash
git clone <repository-url>
cd bip-teste-integrado
```

### 2. Configurar Banco de Dados

**Editar** `backend-module/src/main/resources/application.properties`:

#### Para MySQL

```properties
# MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/bip_teste?useTimezone=true&serverTimezone=UTC&allowPublicKeyRetrieval=true&useSSL=false
spring.datasource.username=bip_user
spring.datasource.password=bip_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true

# Server
server.port=8080
server.servlet.context-path=/
```

#### Para PostgreSQL

```properties
# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/bip_teste
spring.datasource.username=bip_user
spring.datasource.password=bip_password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQL12Dialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
```

### 3. Compilar Backend

```bash
# Dentro da pasta raiz
mvn clean install

# Ou apenas o backend
mvn -f backend-module/pom.xml clean install
```

**Saída esperada**:
```
[INFO] BUILD SUCCESS
[INFO] Total time: XX.XXs
```

### 4. Configurar Frontend

```bash
cd frontend/bip-frontend

# Instalar dependências
npm install

# Verificar instalação
npm --version
```

### 5. Verificar Variáveis de Ambiente (Frontend)

**Editar** `frontend/bip-frontend/src/environments/environment.ts`:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080'
};
```

### 6. Testar Compilações

#### Backend

```bash
mvn clean test -f backend-module/pom.xml
```

#### Frontend

```bash
cd frontend/bip-frontend
npm test
```

---

## ✅ Verificação de Instalação

### 1. Verificar Todas as Ferramentas

Criar script `verificar.sh` (Linux/macOS):

```bash
#!/bin/bash

echo "=== Verificação de Instalação ==="
echo ""

echo "Java:"
java -version
echo ""

echo "Maven:"
mvn -version
echo ""

echo "Node.js:"
node --version
echo ""

echo "npm:"
npm --version
echo ""

echo "Git:"
git --version
echo ""

echo "MySQL (verificando conexão):"
mysql -u root -e "SELECT VERSION();" 2>/dev/null || echo "MySQL não conectado"
echo ""

echo "=== Verificação Concluída ==="
```

### 2. Estrutura de Directories

```bash
# Verificar estrutura
tree bip-teste-integrado -L 2 -I 'target|node_modules|.git'

# Se tree não existir:
find bip-teste-integrado -maxdepth 2 -type d -not -path '*/target*' -not -path '*/.git*' | sort
```

### 3. Connectivity Checks

```bash
# Verificar banco de dados
mysql -u bip_user -p -D bip_teste -e "SELECT COUNT(*) FROM beneficiario;"

# Verificar portas
# Windows: netstat -tuln | findstr "3306 8080 4200"
# Linux: netstat -tuln | grep -E "3306|8080|4200"
```

---

## 🐛 Troubleshooting

### ❌ "Java não encontrado"

**Windows**:
```bash
# Verificar caminho
echo %JAVA_HOME%

# Se vazio, adicionar variável como descrito acima
# Fechar e reabrir PowerShell/CMD
```

**Linux/macOS**:
```bash
which java
# Se vazio, adicionar ao PATH
export PATH=$JAVA_HOME/bin:$PATH
```

### ❌ "Maven não encontrado"

```bash
# Verificar instalação
mvn -version

# Se não encontrado, instalar novamente ou adicionar PATH
export PATH=/path/to/maven/bin:$PATH
```

### ❌ "npm ERR! code EACCES"

```bash
# Linux/macOS - Problema de permissões
sudo npm install -g npm@latest

# Ou usar nvm (Node Version Manager) - mais seguro
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash
nvm install 20
nvm use 20
```

### ❌ "Conectar ao banco de dados falhou"

```bash
# Verificar se MySQL está rodando
# Windows: net start MySQL80
# Linux: sudo systemctl status mysql

# Verificar credentials
mysql -u bip_user -p -h localhost bip_teste -e "SELECT 1;"

# Verificar se banco existe
mysql -u root -p -e "SHOW DATABASES;"
```

### ❌ "Port 3306 já em uso"

```bash
# Windows PowerShell (admin)
Get-NetTCPConnection -LocalPort 3306

# Linux
lsof -i :3306

# Kill processo (se necessário)
kill -9 <PID>
```

### ❌ "Port 8080 já em uso"

```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux
lsof -i :8080
kill -9 <PID>
```

### ❌ "Port 4200 já em uso"

```bash
# Usar porta diferente
ng serve --port 4201
```

---

## 📚 Próximos Passos

1. ✅ Seguir guia [DOCUMENTACAO.md](DOCUMENTACAO.md)
2. ✅ Revisar [ARQUITETURA.md](ARQUITETURA.md)
3. ✅ Executar aplicação (ver DOCUMENTACAO.md - Execução dos Módulos)
4. ✅ Rodar testes
5. ✅ Explorar código

---

**Versão**: 1.0  
**Data**: 4 de Março de 2026
