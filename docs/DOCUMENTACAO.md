# 📚 Documentação - BIP Teste Integrado

## 📋 Índice
1. [Visão Geral](#visão-geral)
2. [Pré-requisitos](#pré-requisitos)
3. [Instalação e Configuração](#instalação-e-configuração)
4. [Estrutura do Projeto](#estrutura-do-projeto)
5. [Execução dos Módulos](#execução-dos-módulos)
6. [Arquitetura](#arquitetura)
7. [Guia de Desenvolvimento](#guia-de-desenvolvimento)
8. [Documentação de API](#documentação-de-api)
9. [Testes](#testes)
10. [Solução de Problemas](#solução-de-problemas)

---

## 🎯 Visão Geral

**BIP Teste Integrado** é uma aplicação multi-camadas (full-stack) que demonstra a integração entre:

- **Backend**: Spring Boot 3.5.11 com Java 17
- **EJB**: Jakarta EJB 4.0 com persistência JPA
- **Frontend**: Angular 21 com TypeScript e Angular Material
- **Banco de Dados**: Estrutura relacional com scripts SQL

O projeto é uma solução completa de gerenciamento de **benefícios** e **transferências**, integrando componentes distribuídos com tratamento de concorrência e validações de negócio.

### 🔑 Características Principais

- ✅ Arquitetura em camadas (Database → EJB → Backend → Frontend)
- ✅ Integração Spring Boot com EJB
- ✅ API REST com manipulação de erros estruturada
- ✅ Interface Angular responsiva com Material Design
- ✅ Testes unitários e de integração
- ✅ Scripts de CI/CD com GitHub Actions
- ✅ Validações de negócio (saldo, transferências, locking)

---

## 📦 Pré-requisitos

Antes de iniciar, certifique-se de ter instalado:

| Ferramenta | Versão Mínima | Propósito |
|------------|--------------|----------|
| **Java JDK** | 17+ | Compilação e execução do backend |
| **Maven** | 3.9.0+ | Build e gerenciamento de dependências Java |
| **Node.js** | 20+ | Execução do Angular e gerenciador de pacotes |
| **npm** | 11.0.0+ | Gerenciar dependências do frontend |
| **Git** | Qualquer | Controle de versão |
| **Banco de Dados** | MySQL/PostgreSQL | Persistência de dados |

### ✅ Verificar Instalações

```bash
# Verificar Java
java -version

# Verificar Maven
mvn -version

# Verificar Node.js e npm
node --version
npm --version
```

---

## 🔧 Instalação e Configuração

### 1️⃣ Clonar o Repositório

```bash
git clone <repository-url>
cd bip-teste-integrado
```

### 2️⃣ Configurar Banco de Dados

#### Opção A: MySQL Local

```bash
# Criar banco de dados
mysql -u root -p
CREATE DATABASE bip_teste;
USE bip_teste;

# Executar scripts
SOURCE db/schema.sql;
SOURCE db/seed.sql;
```

#### Opção B: PostgreSQL Local

```bash
# Criar banco de dados
psql -U postgres
CREATE DATABASE bip_teste;

# Conectar e executar scripts
\c bip_teste
\i db/schema.sql
\i db/seed.sql
```

### 3️⃣ Configurar Backend

O arquivo `backend-module/src/main/resources/application.properties` contém as configurações:

```properties
# Banco de dados (ajustar conforme seu BD)
spring.datasource.url=jdbc:mysql://localhost:3306/bip_teste?useTimezone=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=validate

# Porta do servidor
server.port=8080
```

### 4️⃣ Compilar Módulos Java

```bash
# Compilar tudo
mvn clean install

# Ou compilar módulos específicos
mvn -f backend-module/pom.xml clean install
mvn -f ejb-module/pom.xml clean install
```

### 5️⃣ Configurar Frontend

```bash
cd frontend/bip-frontend

# Instalar dependências
npm install

# Configurar URL da API (se necessário)
# Editar src/environments/environment.ts
```

---

## 📂 Estrutura do Projeto

```
bip-teste-integrado/
├── docs/                          # Documentação e critérios
│   └── README.md
├── db/                            # Scripts de banco de dados
│   ├── schema.sql                 # Criação das tabelas
│   └── seed.sql                   # Dados iniciais
├── ejb-module/                    # Módulo EJB (Serviços & Modelo)
│   ├── src/main/java/
│   │   └── com/example/ejb/
│   │       ├── model/             # Entidades JPA
│   │       ├── repository/        # Interfaces repositório
│   │       ├── service/           # Serviços de negócio
│   │       ├── exception/         # Exceções customizadas
│   │       └── dto/               # Data Transfer Objects
│   └── pom.xml
├── backend-module/                # Módulo Backend (Spring Boot)
│   ├── src/main/java/
│   │   └── com/example/backend/
│   │       ├── config/            # Configurações Spring
│   │       ├── controller/        # Endpoints REST
│   │       ├── service/           # Lógica de aplicação
│   │       ├── mapper/            # Conversão de objetos
│   │       ├── exception/         # Tratamento de erros
│   │       ├── dto/               # DTOs do backend
│   │       └── BackendApplication.java
│   ├── src/main/resources/
│   │   └── application.properties # Configurações
│   └── pom.xml
├── frontend/                      # Módulo Frontend (Angular)
│   └── bip-frontend/
│       ├── src/
│       │   ├── app/
│       │   │   ├── core/          # Serviços e interceptadores
│       │   │   ├── features/      # Componentes de funcionalidades
│       │   │   ├── app.config.ts  # Configuração da aplicação
│       │   │   └── app.routes.ts  # Rotas
│       │   ├── environments/      # Variáveis de ambiente
│       │   └── main.ts            # Entrada da aplicação
│       ├── package.json
│       ├── angular.json
│       └── tsconfig.json
├── .github/
│   └── workflows/                 # CI/CD pipelines
├── pom.xml                        # POM do projeto raiz (multi-módulo)
└── DOCUMENTACAO.md               # Este arquivo
```

---

## 🚀 Execução dos Módulos

### Backend - Spring Boot

#### Terminal 1: Executar Servidor

```bash
# Opção A: Direto pela IDE
# Use "Run: BackendApplication" (terminal já configurado)

# Opção B: Linha de comando
cd backend-module
mvn spring-boot:run

# Opção C: Executar JAR compilado
java -jar backend-module/target/backend-module-0.0.1-SNAPSHOT.jar
```

**Saída esperada:**
```
Started BackendApplication in 2.5 seconds (JVM running for 3.1s)
Tomcat started on port(s): 8080 (http)
...
```

**Endpoints disponíveis:**
- `GET http://localhost:8080/api/beneficios` - Listar benefícios
- `GET http://localhost:8080/api/beneficios/{id}` - Obter benefício
- `POST http://localhost:8080/api/beneficios` - Criar benefício
- `PUT http://localhost:8080/api/beneficios/{id}` - Atualizar benefício
- `DELETE http://localhost:8080/api/beneficios/{id}` - Deletar benefício
- `POST http://localhost:8080/api/beneficios/{id}/transferencia` - Transferir valores

### Frontend - Angular

#### Terminal 2: Executar Servidor de Desenvolvimento

```bash
cd frontend/bip-frontend

# Opção A: Comando npm
npm start

# Opção B: Angular CLI
ng serve

# Com porta customizada
ng serve --port 4200
```

**Saída esperada:**
```
✔ Compiled successfully.
✔ Server ready on http://localhost:4200
```

**Acessar Application:**
- Navegador: `http://localhost:4200`
- Hot reload automático ao salvar arquivos

### Compilação para Produção

```bash
# Frontend
cd frontend/bip-frontend
npm run build
# Output: dist/

# Backend
mvn clean package -DskipTests
# Output: target/backend-module-0.0.1-SNAPSHOT.jar
```

---

## 🏗️ Arquitetura

### Fluxo de Dados

```
┌──────────────────────────────────────────────────────────────┐
│                      FRONTEND (Angular)                      │
│                    - Interface do usuário                     │
│                    - Requisições HTTP                         │
└────────────────────┬─────────────────────────────────────────┘
                     │
                     ↓ HTTP/REST
┌──────────────────────────────────────────────────────────────┐
│                BACKEND (Spring Boot)                          │
│  ┌────────────────────────────────────────────────────────┐  │
│  │            API REST Controllers                         │  │
│  │  - BeneficioController                                 │  │
│  │  - TransferenciaController                             │  │
│  └────────────────────┬─────────────────────────────────┘  │
│                       │                                      │
│  ┌────────────────────▼─────────────────────────────────┐  │
│  │            Business Services                          │  │
│  │  - BeneficioService                                   │  │
│  │  - TransferenciaService                               │  │
│  └────────────────────┬─────────────────────────────────┘  │
│                       │                                      │
│  ┌────────────────────▼─────────────────────────────────┐  │
│  │            EJB Integration                            │  │
│  │  - Chamadas para BeneficioEjbService                  │  │
│  │  - Chamadas para TransferenciaEjbService              │  │
│  └────────────────────┬─────────────────────────────────┘  │
└────────────────────┬─────────────────────────────────────────┘
                     │
                     ↓ JNDI/RMI
┌──────────────────────────────────────────────────────────────┐
│                   EJB MODULE                                  │
│  ┌────────────────────────────────────────────────────────┐  │
│  │            EJB Services (Lógica de Negócio)            │  │
│  │  - BeneficioEjbService                                 │  │
│  │      • Gerenciar beneficiários                         │  │
│  │      • Validar regras de negócio                       │  │
│  │  - TransferenciaEjbService                             │  │
│  │      • Transferências entre contas                     │  │
│  │      • Validação de saldo                              │  │
│  │      • Locking otimista                                │  │
│  └────────────────────┬─────────────────────────────────┘  │
│                       │                                      │
│  ┌────────────────────▼─────────────────────────────────┐  │
│  │         JPA Repositories                              │  │
│  │  - Acesso a dados persistentes                         │  │
│  └────────────────────┬─────────────────────────────────┘  │
└────────────────────┬─────────────────────────────────────────┘
                     │
                     ↓ JDBC/SQL
┌──────────────────────────────────────────────────────────────┐
│              DATABASE (MySQL/PostgreSQL)                      │
│  ┌────────────────────────────────────────────────────────┐  │
│  │  Tables:                                               │  │
│  │  - beneficiario (ID, Nome, Saldo, Versão)             │  │
│  │  - conta_bancaria (ID, Benef_ID, Saldo, Versão)       │  │
│  │  - transferencia (ID, Conta_Origem, Valor, Status)    │  │
│  └────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────┘
```

### Padrões de Design Utilisados

| Padrão | Implementação | Localização |
|--------|---------------|------------|
| **MVC** | Spring Controller → Service → Repository | backend-module/ |
| **DTO** | Data Transfer Objects | */dto/ |
| **Repository** | JPA Repository Pattern | ejb-module/repository/ |
| **Service** | Business Logic Separation | */service/ |
| **Dependency Injection** | Spring @Autowired | backend-module/ |
| **Exception Handling** | Custom Exceptions + GlobalExceptionHandler | */exception/ |
| **Optimistic Locking** | @Version no JPA | ejb-module/model/ |

---

## 💻 Guia de Desenvolvimento

### Adicionando Novo Endpoint

**1. Criar DTO** (`backend-module/src/main/java/com/example/backend/dto/`)

```java
public class NovaDtoRequest {
    private String campo1;
    private Integer campo2;
    // getters e setters
}
```

**2. Criar Controller** (`backend-module/src/main/java/com/example/backend/controller/`)

```java
@RestController
@RequestMapping("/api/novo")
public class NovoController {
    
    @Autowired
    private NovoService service;
    
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody NovaDtoRequest request) {
        // implementação
        return ResponseEntity.ok().build();
    }
}
```

**3. Criar Service** (`backend-module/src/main/java/com/example/backend/service/`)

```java
@Service
public class NovoService {
    
    @Autowired
    private NovoEjbService ejbService;
    
    public void processar(NovaDtoRequest request) {
        // chamar EJB se necessário
        // aplicar lógica adicional
    }
}
```

### Tratamento de Erros

O projeto implementa tratamento centralizado de exceções:

```java
// Lançar exceção customizada
throw new RecursoNaoEncontradoException("Recurso não encontrado");

// GlobalExceptionHandler captura e responde:
{
    "timestamp": "2026-03-04T10:30:00",
    "status": 404,
    "mensagem": "Recurso não encontrado",
    "caminho": "/api/beneficios/999"
}
```

### Validações de Negócio

Exemplo de validação em transferência:

```java
public void trasferencia(Long contaOrigem, Long contaDestino, BigDecimal valor) {
    // 1. Validar conta origem existe
    // 2. Validar saldo suficiente
    // 3. Validar versão (optimistic locking)
    // 4. Atualizar saldos em transação
    // 5. Registrar transferência
}
```

---

## 📡 Documentação de API

### Beneficiário - Endpoints

#### 1. Listar Todos os Beneficiários

```http
GET /api/beneficios HTTP/1.1
Host: localhost:8080
Content-Type: application/json
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "nome": "João Silva",
    "cpf": "123.456.789-00",
    "saldo": 1500.00,
    "versao": 1,
    "contas": [...]
  }
]
```

#### 2. Obter Beneficiário por ID

```http
GET /api/beneficios/1 HTTP/1.1
Host: localhost:8080
```

**Response (200 OK):**
```json
{
  "id": 1,
  "nome": "João Silva",
  "cpf": "123.456.789-00",
  "saldo": 1500.00,
  "versao": 1
}
```

**Response (404 Not Found):**
```json
{
  "status": 404,
  "mensagem": "Beneficiário não encontrado"
}
```

#### 3. Criar Novo Beneficiário

```http
POST /api/beneficios HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
  "nome": "Maria Santos",
  "cpf": "987.654.321-00",
  "saldo": 5000.00
}
```

**Response (201 Created):**
```json
{
  "id": 2,
  "nome": "Maria Santos",
  "cpf": "987.654.321-00",
  "saldo": 5000.00,
  "versao": 1
}
```

#### 4. Atualizar Beneficiário

```http
PUT /api/beneficios/1 HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
  "nome": "João da Silva",
  "cpf": "123.456.789-00",
  "saldo": 2000.00
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "nome": "João da Silva",
  "saldo": 2000.00,
  "versao": 2
}
```

#### 5. Deletar Beneficiário

```http
DELETE /api/beneficios/1 HTTP/1.1
Host: localhost:8080
```

**Response (204 No Content):**
```
(sem corpo)
```

### Transferência - Endpoints

#### 1. Realizar Transferência

```http
POST /api/beneficios/1/transferencia HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
  "contaOrigemId": 1,
  "contaDestinoId": 2,
  "valor": 500.00
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "contaOrigemId": 1,
  "contaDestinoId": 2,
  "valor": 500.00,
  "status": "SUCESSO",
  "dataTransferencia": "2026-03-04T10:30:00Z"
}
```

**Response (400 Bad Request) - Saldo insuficiente:**
```json
{
  "status": 400,
  "mensagem": "Saldo insuficiente para realizar a transferência"
}
```

**Response (409 Conflict) - Conflito de versão:**
```json
{
  "status": 409,
  "mensagem": "Dados foram modificados por outro usuário. Tente novamente."
}
```

### Códigos HTTP Utilizados

| Código | Significado | Exemplo |
|--------|------------|---------|
| **200** | OK | Operação bem-sucedida |
| **201** | Created | Recurso criado com sucesso |
| **204** | No Content | Deletado com sucesso |
| **400** | Bad Request | Dados inválidos |
| **404** | Not Found | Recurso não encontrado |
| **409** | Conflict | Conflito (versão, saldo) |
| **500** | Internal Server Error | Erro no servidor |

---

## 🧪 Testes

### Executar Testes

```bash
# Backend: Testes unitários
cd backend-module
mvn test

# Frontend: Testes com Vitest
cd frontend/bip-frontend
npm test

# Cobertura de testes
mvn test jacoco:report
```

### Estrutura de Testes

```
backend-module/src/test/java/
├── com/example/backend/
│   ├── controller/
│   │   └── BeneficioControllerTest.java
│   ├── service/
│   │   └── BeneficioServiceTest.java
│   └── BeneficioServiceIntegrationTest.java

frontend/bip-frontend/src/app/
├── app.spec.ts
└── features/
    └── beneficios/
        └── beneficio.service.spec.ts
```

### Exemplo de Teste Unitário

```java
@SpringBootTest
public class BeneficioServiceTest {
    
    @Autowired
    private BeneficioService service;
    
    @Test
    public void deveCriarBeneficiarioComSucesso() {
        // Arrange
        BeneficioRequest request = new BeneficioRequest("João", "123.456.789-00", new BigDecimal("1000"));
        
        // Act
        BeneficioResponse resultado = service.criar(request);
        
        // Assert
        assertNotNull(resultado.getId());
        assertEquals("João", resultado.getNome());
    }
}
```

---

## 🔍 Solução de Problemas

### ❌ Backend não conecta ao banco de dados

**Erro:**
```
SQLException: Cannot get a connection, pool error Timeout waiting for idle object
```

**Solução:**
1. Verificar se o banco está rodando
2. Validar credenciais em `application.properties`
3. Verificar firewall/porta
4. Reiniciar serviço do banco

```bash
# MySQL
sudo systemctl restart mysql

# PostgreSQL
sudo systemctl restart postgresql
```

### ❌ Frontend não consegue conectar ao backend

**Erro:**
```
ERROR in http://localhost:8080/api/beneficios - Failed to fetch
```

**Solução:**
1. Verificar se backend está rodando (porta 8080)
2. CORS pode estar bloqueado - adicionar em `application.properties`:

```properties
server.servlet.context-path=/
```

3. Verificar URL da API em `environment.ts`:

```typescript
export const environment = {
  apiUrl: 'http://localhost:8080'
};
```

### ❌ Erro de compilação Maven

**Erro:**
```
[ERROR] COMPILATION ERROR
[ERROR] cannot find symbol - class SomeClass
```

**Solução:**
```bash
# Limpar cache e reconstruir
mvn clean install -U

# Deletar pasta .m2 se problema persistir
rm -rf ~/.m2/repository
mvn clean install
```

### ❌ Porta 8080 já em uso

**Erro:**
```
Address already in use
```

**Solução:**
```bash
# Encontrar processo usando porta 8080
lsof -i :8080

# Kill processo
kill -9 <PID>

# Ou usar porta diferente
java -jar app.jar --server.port=9090
```

### ❌ npm ERR! while executing package.json scripts

**Solução:**
```bash
# Limpar cache npm
npm cache clean --force

# Deletar node_modules e reinstalar
rm -rf node_modules package-lock.json
npm install
```

---

## 📚 Referências e Documentação Externos

- [Spring Boot 3.5 Documentation](https://spring.io/projects/spring-boot)
- [Jakarta EJB 4.0](https://jakarta.ee/specifications/enterprise-beans/)
- [Angular 21 Guide](https://angular.io/guide/setup-local)
- [JPA/Hibernate Documentation](https://hibernate.org/orm/documentation/)
- [RESTful Web Services Best Practices](https://restfulapi.net/)

---

## 📞 Suporte e Contribuições

Para dúvidas ou contribuições:

1. Verificar documentação existente em `/docs`
2. Consultar README.md do projeto
3. Revisar comentários no código
4. Abrir issue no repositório

---

**Versão:** 1.0  
**Última Atualização:** 4 de Março de 2026  
**Autor:** Time de Desenvolvimento
