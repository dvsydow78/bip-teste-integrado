# 🏗️ Guia de Arquitetura - BIP Teste Integrado

## 📑 Índice
1. [Visão Geral da Arquitetura](#visão-geral)
2. [Camadas da Aplicação](#camadas)
3. [Componentes Principais](#componentes)
4. [Fluxo de Requisição](#fluxo)
5. [Padrões de Design](#padrões)
6. [Tratamento de Transações](#transações)
7. [Tratamento de Concorrência](#concorrência)

---

## 🎯 Visão Geral

A aplicação segue um modelo **em três camadas (3-tier)** com uma camada adicional de integração:

```
┌─────────────────────────────────┐
│    PRESENTATION LAYER           │
│  (Angular Frontend)             │
└─────────────┬───────────────────┘
              │ HTTP/REST
┌─────────────▼───────────────────┐
│    APPLICATION LAYER            │
│  (Spring Boot Backend)          │
└─────────────┬───────────────────┘
              │ JNDI/RMI/Java API
┌─────────────▼───────────────────┐
│    BUSINESS LOGIC LAYER         │
│  (Jakarta EJB Services)         │
└─────────────┬───────────────────┘
              │ JPA/SQL
┌─────────────▼───────────────────┐
│    DATA ACCESS LAYER            │
│  (Database)                     │
└─────────────────────────────────┘
```

### 📊 Características da Arquitetura

- **Separação de Responsabilidades**: Cada camada tem um propósito específico
- **Escalabilidade**: EJB pode ser deployado em servidor separado
- **Testabilidade**: Camadas podem ser testadas independentemente
- **Reutilização**: Serviços EJB podem ser usados por múltiplos clientes
- **Concorrência**: Mecanismos de locking para operações simultâneas

---

## 🔧 Camadas da Aplicação

### 1️⃣ Camada de Apresentação (Frontend)

**Tecnologia**: Angular 21 + TypeScript + Angular Material

**Responsabilidades**:
- Interface responsiva do usuário
- Validações de formulário
- Consumo de API REST
- Gerenciamento de estado local

**Estrutura**:
```
frontend/bip-frontend/src/app/
├── core/                    # Serviços compartilhados
│   ├── interceptors/        # HTTP Interceptors
│   └── services/            # BeneficioService, etc
├── features/                # Componentes por funcionalidade
│   └── beneficios/
│       ├── pages/           # Páginas/Componentes principais
│       ├── models/          # Tipos/Interfaces TypeScript
│       └── beneficio.service.ts
├── app.routes.ts            # Configuração de rotas
└── app.config.ts            # Configuração global
```

**Exemplo de Fluxo**:
```
Usuário Clica → Componente Angular → Service HTTP → Interceptor
→ GET /api/beneficios → Backend → Response JSON → 
Component → Template Render
```

### 2️⃣ Camada de Aplicação (Backend)

**Tecnologia**: Spring Boot 3.5 + Java 17

**Responsabilidades**:
- Expor APIs REST
- Orquestrar chamadas aos serviços EJB
- Validações de entrada
- Tratamento de erros e exceções

**Componentes**:

#### Controllers
```java
@RestController
@RequestMapping("/api/beneficios")
public class BeneficioController {
    // Endpoints HTTP
}
```

#### Services
```java
@Service
public class BeneficioService {
    // Lógica de aplicação
    // Chamadas aos EJBs
}
```

#### Mappers
```java
public class BeneficioMapper {
    // Converte entre DTOs e EntidadesJPA
}
```

#### Configurações
```java
@Configuration
public class EjbServiceConfig {
    // Configuração de EJBs
    // Beans compartilhados
}
```

### 3️⃣ Camada de Negócio (EJB)

**Tecnologia**: Jakarta EJB 4.0 + JPA 3.1

**Responsabilidades**:
- Implementar regras de negócio
- Acesso a dados via JPA
- Controle de transações
- Validações críticas de negócio

**Serviços Principais**:

#### BeneficioEjbService
```java
@Stateless
public class BeneficioEjbService {
    // CRUD de beneficiários
    // Validações de negócio
    // Gerenciamento de contas
}
```

#### TransferenciaEjbService
```java
@Stateless
public class TransferenciaEjbService {
    // Validar saldo
    // Executar transferências
    // Optimistic Locking
    // Rollback automático
}
```

### 4️⃣ Camada de Acesso a Dados

**Tecnologia**: JPA + Hibernate + JDBC

**Responsabilidades**:
- Mapeamento Objeto-Relacional
- Executar queries SQL
- Gerenciar entidades JPA
- Connection pooling

**Entidades**:
```java
@Entity
@Table(name = "beneficiario")
public class Beneficiario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Version
    private Long versao;  // Optimistic Locking
    
    private String nome;
    private BigDecimal saldo;
    
    @OneToMany
    private List<ContaBancaria> contas;
}
```

---

## 🔌 Componentes Principais

### Backend Application Structure

```
backend-module/
├── config/
│   ├── EjbServiceConfig.java       # Configuração EJB inject
│   └── JpaConfig.java              # Configuração JPA
├── controller/
│   ├── BeneficioController.java     # Endpoints beneficiário
│   └── TransferenciaController.java # Endpoints transferência
├── dto/
│   ├── BeneficioRequest.java        # Input DTO
│   ├── BeneficioResponse.java       # Output DTO
│   └── TransferenciaRequest.java
├── exception/
│   ├── RecursoNaoEncontradoException
│   ├── SaldoInsuficienteException
│   ├── ConflitoDadosException
│   └── GlobalExceptionHandler.java
├── mapper/
│   └── BeneficioMapper.java         # Conversões
├── service/
│   ├── BeneficioService.java        # Lógica aplicação
│   └── TransferenciaService.java
└── BackendApplication.java
```

### EJB Module Structure

```
ejb-module/
├── exception/
│   ├── SaldoInsuficienteException
│   └── BeneficiarioNaoEncontradoException
├── model/
│   ├── Beneficiario.java           # Entidade JPA
│   ├── ContaBancaria.java
│   └── Transferencia.java
├── repository/
│   ├── BeneficiarioRepository.java # Spring Data JPA
│   ├── ContaBancariaRepository.java
│   └── TransferenciaRepository.java
├── service/
│   ├── BeneficioEjbService.java    # EJB Stateless
│   └── TransferenciaEjbService.java
└── dto/
    └── (Data Transfer Objects)
```

---

## 📨 Fluxo de Requisição

### Exemplo: Criar Novo Beneficiário

```
1. FRONTEND
   └─ Formulário → Service → POST /api/beneficios

2. BACKEND CONTROLLER
   └─ @PostMapping /beneficios
   └─ Validação básica
   └─ Chama BeneficioService.criar()

3. BACKEND SERVICE
   └─ Mapeia DTO → Entidade
   └─ Chama BeneficioEjbService.criar()

4. EJB SERVICE
   └─ Valida regras de negócio
   └─ Salva no BD (JPA)
   └─ Retorna entidade criada

5. MAPPER
   └─ Converte Entidade → DTO Response

6. CONTROLLER
   └─ HTTP 201 + JSON Response

7. FRONTEND
   └─ Recebe resposta
   └─ Atualiza lista
   └─ Renderiza UI
```

### Exemplo: Transferência de Valores

```
1. FRONTEND
   └─ Form: Conta Origem, Conta Destino, Valor
   └─ POST /api/beneficios/{id}/transferencia

2. BACKEND CONTROLLER
   └─ Extract parâmetros
   └─ Validação HTTPStatus 400

3. BACKEND SERVICE
   └─ Arquivo IDs válidos
   └─ Chama TransferenciaEjbService

4. EJB SERVICE (TRANSACIONAL)
   └─ @TransactionAttribute(REQUIRED)
   └─ Begin Transaction
   │
   ├─ Locked Conta Origem (Pessimistic Lock)
   ├─ Validar saldo > valor
   ├─ Validate versão (Optimistic Lock)
   │
   ├─ Debita valor de origem
   ├─ Credita valor destino
   ├─ Registra transferência
   │
   ├─ Commit Transaction
   └─ Return Resultado

5. Error Handling
   └─ Saldo insuficiente → Throw Exception
   └─ Versão diferente → Rollback automático
   └─ Erro BD → Rollback automático

6. RESPONSE
   └─ Sucesso: 200 OK
   └─ Erro: 400/409 + mensagem
```

---

## 🎨 Padrões de Design

### 1. Data Transfer Object (DTO)

**Propósito**: Desacoplar estrutura interna de comunicação externa

```java
// Request
public class TransferenciaRequest {
    private Long contaOrigemId;
    private Long contaDestinoId;
    private BigDecimal valor;
}

// Response
public class TransferenciaResponse {
    private Long id;
    private BigDecimal valor;
    private String status;
    private LocalDateTime dataTransferencia;
}
```

**Benefícios**:
- ✅ Segurança (não expõe entidades JPA)
- ✅ Flexibilidade (muda DTO sem afetar BD)
- ✅ Validação (pode ter @NotNull, @Positive)

### 2. Repository Pattern

**Propósito**: Abstrair acesso a dados

```java
public interface BeneficiarioRepository 
    extends JpaRepository<Beneficiario, Long> {
    
    Optional<Beneficiario> findByCpf(String cpf);
    List<Beneficiario> findByNomeContainsIgnoreCase(String nome);
}
```

**Spring Data JPA** fornece:
- ✅ CRUD automático
- ✅ Query generation
- ✅ Paginação
- ✅ Sorting

### 3. Service Layer

**Propósito**: Concentrar lógica de negócio

```java
@Service
public class BeneficioService {
    
    @Autowired
    private BeneficiarioRepository repo;
    
    @Autowired
    private BeneficioEjbService ejb;
    
    // Orquestração de operações
    public BeneficioResponse criar(BeneficioRequest req) {
        // 1. Validações
        // 2. Chamar EJB
        // 3. Converter resultado
        // 4. Retornar DTO
    }
}
```

### 4. Exception Handling

**Padrão**: Custom Exceptions + Global Handler

```java
// Custom Exception
public class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException(String msg) {
        super(msg);
    }
}

// Global Handler
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<?> handleSaldoInsuficiente(
        SaldoInsuficienteException ex) {
        
        ErrorResponse error = new ErrorResponse(
            400, 
            ex.getMessage(), 
            LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(error);
    }
}
```

### 5. Dependency Injection

**Spring Boot**:
```java
@Service
public class MyService {
    @Autowired
    private BeneficiarioRepository repository;  // Injetado
}
```

**Benefícios**:
- ✅ Loose Coupling
- ✅ Testabilidade (Mock objects)
- ✅ Configuração centralizada

---

## 💾 Tratamento de Transações

### Níveis de Isolamento

| Nível | Description | Phantom Read | Dirty Read | Non-repeatable Read |
|-------|-------------|-------------|-----------|-------------------|
| READ_UNCOMMITTED | Lê dados não consolidados | ✅ | ✅ | ✅ |
| READ_COMMITTED | Lê apenas dados consolidados | ✅ | ❌ | ✅ |
| REPEATABLE_READ | Lê linha não muda | ✅ | ❌ | ❌ |
| SERIALIZABLE | Total isolamento | ❌ | ❌ | ❌ |

**Configuração**:
```java
@Service
public class TransferenciaService {
    
    @Transactional(
        isolation = Isolation.REPEATABLE_READ,
        propagation = Propagation.REQUIRED
    )
    public void transferir(Long origem, Long destino, BigDecimal valor) {
        // Operação transacional
        // Rollback automático em exceção
    }
}
```

### Demarcação de Transações

**Spring Boot** (Container Managed):
```java
@Transactional
public void operacao() {
    // Begin implícito
    // Commit automático ao retorno
    // Rollback automático em RuntimeException
}
```

**EJB** (Manual):
```java
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public void operacao() {
    // Bean Managed Transaction
    // Controle fino da transação
}
```

---

## 🔒 Tratamento de Concorrência

### Optimistic Locking

**Problema**: Múltiplas requisições modificando mesmo registro

**Solução**: Add version field

```java
@Entity
public class Beneficiario {
    // ...
    @Version
    private Long versao;
}

// Operação
beneficiario.setSaldo(novo_saldo);
beneficiarioRepository.save(beneficiario);
// Hibernate: WHERE id=? AND versao=?
// Se versão diferente → OptimisticLockingFailureException
```

**Fluxo**:
```
Cliente A                      BD                     Cliente B
GET /id=1 (v=1)     ←───────── reads v=1
                               
                               GET /id=1 (v=1) ←───── reads v=1

PUT /id=1 (v=1)     ─────────→ UPDATE WHERE v=1
novo_saldo=100                 UPDATE: v=2, saldo=100
                               ✅ Success

                               PUT /id=1 (v=1) ─────→
                               novo_saldo=200
                               UPDATE WHERE v=1
                               ❌ Failed! (v is now 2)
                               409 Conflict
```

**Handling**:
```java
try {
    beneficiarioRepository.save(beneficiario);
} catch (OptimisticLockingFailureException e) {
    throw new ConflitoDadosException(
        "Dados foram modificados. Recarregue e tente novamente."
    );
}
```

### Pessimistic Locking

**Para operações críticas**:

```java
@Query("SELECT b FROM Beneficiario b WHERE b.id = ?1")
@Lock(LockModeType.PESSIMISTIC_WRITE)
Beneficiario obterComLock(Long id);

// Locked até fim da transação
```

### Detalhes de Implementação

**Transferência com Locks**:

```java
@Transactional(isolation = Isolation.SERIALIZABLE)
public void transferir(Long origem, Long destino, BigDecimal valor) {
    // 1. LOCK ORIGEM (Exclusive)
    ContaBancaria contaOrigem = 
        contaRepository.obterComLock(origem);
    
    // 2. Validar versão
    ContaBancaria dbOrigem = contaRepository.findById(origem).get();
    if (!dbOrigem.getVersao().equals(contaOrigem.getVersao())) {
        throw new ConflitoDadosException("Conta foi modificada");
    }
    
    // 3. LOCK DESTINO (Exclusive)
    ContaBancaria contaDestino = 
        contaRepository.obterComLock(destino);
    
    // 4. Validações
    if (contaOrigem.getSaldo().compareTo(valor) < 0) {
        throw new SaldoInsuficienteException("Saldo insuficiente");
    }
    
    // 5. Atualizar
    contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(valor));
    contaDestino.setSaldo(contaDestino.getSaldo().add(valor));
    
    // 6. Persiste (dentro da transação)
    contaRepository.save(contaOrigem);
    contaRepository.save(contaDestino);
    
    // 7. Automatic rollback on exception
    // 8. Lock release at transaction end
}
```

---

## 📚 Decisões Arquiteturais

### ✅ Por que EJB Separado?

- **Reutilização**: Pode ser usado por outros clientes (Web, Mobile, Desktop)
- **Escalabilidade**: Deployar em servidor separado se necessário
- **Regras de Negócio**: Centralizar lógica crítica

### ❌ Por que NÃO usar microserviços?

- Projeto de pequeno/médio porte
- Complexidade desnecessária
- Operacional mais fácil com monolito

### ✅ Por que Angular Frontend?

- Modern framework com ecosystem robusto
- TypeScript para type-safety
- Material Design integrado
- Comunidade ativa

---

**Versão**: 1.0  
**Data**: 4 de Março de 2026
