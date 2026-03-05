# 👨‍💻 Guia do Desenvolvedor - BIP Teste Integrado

## 📑 Índice
1. [Convenções de Código](#convenções)
2. [Estrutura de Pastas](#estrutura)
3. [Como Contribuir](#contribuir)
4. [Workflow de Desenvolvimento](#workflow)
5. [Debugging](#debugging)
6. [Performance](#performance)
7. [Segurança](#segurança)
8. [FAQ do Desenvolvedor](#faq)

---

## 📝 Convenções de Código

### Nomenclatura - Java

#### Classes
```java
// ✅ Correto: PascalCase
public class BeneficioController { }
public class TransferenciaService { }
public class SaldoInsuficienteException { }

// ❌ Errado: camelCase ou snake_case
public class beneficioController { }
public class transferencia_service { }
```

#### Métodos e Variáveis
```java
// ✅ Correto: camelCase
public void criarBeneficiario() { }
private Long beneficiarioId;

// ❌ Errado: PascalCase ou snake_case
public void CriarBeneficiario() { }
private Long BeneficiarioId;
private Long beneficiario_id;
```

#### Constantes
```java
// ✅ Correto: SCREAMING_SNAKE_CASE
public static final String API_BASE_PATH = "/api/";
public static final Integer TIMEOUT_MILLIS = 5000;

// ❌ Errado: camelCase
public static final String apiBasePath = "/api/";
```

#### DTOs
```java
// ✅ Request/Response pattern
public class BeneficioRequest {
    // Input do usuário
}

public class BeneficioResponse {
    // Output para usuário
}

// ❌ Evitar
public class BeneficioDTO { }
public class BeneficioData { }
```

#### Repositories
```java
// ✅ Correto
public interface BeneficiarioRepository extends JpaRepository<Beneficiario, Long> { }

// ❌ Errado
public interface BeneficiarioDao extends JpaRepository { }
public interface BeneficiarioRepository extends Repository { }
```

### Nomenclatura - TypeScript/Angular

```typescript
// ✅ Classes: PascalCase
export class BeneficioService { }
export class BeneficioComponent { }

// ✅ Propriedades: camelCase
private beneficiarios: Beneficiario[] = [];
public selectedBeneficiario: Beneficiario;

// ✅ Métodos: camelCase
criarBeneficiario() { }
updateSaldo() { }

// ✅ Constantes: SCREAMING_SNAKE_CASE
const APP_TITLE = 'BIP - Gestão de Beneficiários';
const API_TIMEOUT = 5000;

// ✅ Interfaces: PascalCase com prefixo I (opcional)
export interface IBeneficiario { }
// Ou sem prefixo (mais moderno)
export interface Beneficiario { }

// ✅ Enums: PascalCase valores SCREAMING_SNAKE_CASE
export enum TransferenciaStatus {
  EM_PROCESSAMENTO = 'EM_PROCESSAMENTO',
  SUCESSO = 'SUCESSO',
  ERRO = 'ERRO'
}
```

### Formatação de Código

#### Java - SUN Code Conventions

```java
public class Example {
    // Indentação: 4 espaços
    private String field;
    
    // Métodos públicos first
    public void publicMethod() {
        // 1. Documentação
        // 2. Validações
        // 3. Lógica principal
        // 4. Return
    }
    
    // Métodos privados
    private void privateMethod() {
    }
}
```

Configuração no IDE:
- **Eclipse**: Window → Preferences → Java → Code Style → Formatter
- **IntelliJ**: Settings → Code Style → Java
- **VSCode**: Instalar Extension "Extension Pack for Java"

#### TypeScript/Angular

```typescript
// Usar Prettier (já configurado)
import { Component } from '@angular/core';

export class MyComponent {
  variableName: string = 'value';
  
  constructor() {
  }
  
  methodName(): void {
    // 2 espaços de indentação (Prettier padrão)
  }
}
```

Configuração:
```bash
# Aplicar formatação
npx prettier --write "src/**/*.{ts,html,scss}"

# Verificar sem aplicar
npx prettier --check "src/**/*.{ts,html,scss}"
```

### Documentação de Código

#### JavaDoc
```java
/**
 * Realiza transferência de valores entre duas contas.
 * 
 * <p>Valida saldo suficiente antes de realizar a operação.
 * A operação é atômica e usa optimistic locking.</p>
 *
 * @param contaOrigemId ID da conta de origem (não pode ser null)
 * @param contaDestinoId ID da conta de destino (não pode ser null)
 * @param valor Valor a transferir (deve ser positivo)
 * 
 * @return {@link TransferenciaResponse} com dados da transferência
 * 
 * @throws SaldoInsuficienteException se saldo < valor
 * @throws ContaNaoEncontradaException se conta não existe
 * @throws ConflitoDadosException se conflito de versão (optimistic lock)
 * 
 * @see TransferenciaRequest
 * @see TransferenciaResponse
 * 
 * @since 1.0
 */
public TransferenciaResponse transferir(
    Long contaOrigemId, 
    Long contaDestinoId, 
    BigDecimal valor) {
    // implementação
}
```

#### TypeScript/Angular

```typescript
/**
 * Serviço para operações com beneficiários.
 * 
 * Responsável por:
 * - Comunicação com API backend
 * - Transformação de dados
 * - Cache local
 */
export class BeneficioService {
  
  /**
   * Obtém lista de todos os beneficiários.
   * 
   * @returns Observable com array de beneficiários
   * 
   * @example
   * this.beneficioService.listar().subscribe(
   *   (beneficios) => console.log(beneficios)
   * );
   */
  listar(): Observable<Beneficiario[]> {
    return this.http.get<Beneficiario[]>(`${this.API_URL}/beneficios`);
  }
}
```

#### Comentários Inline

```java
// ✅ Bom: Explica o "por quê", não o "o quê"
// Validar saldo ANTES de transferir para evitar debitar conta inválida
if (conta.getSaldo().compareTo(valor) < 0) {

// ❌ Ruim: Óbvio pelo código
// Verificar se saldo é menor que valor
if (conta.getSaldo().compareTo(valor) < 0) {

// ❌ Ruim: Comentário desatualizado
// TODO: Remover este código em 2024
//public void metodoAntigo() { }
```

---

## 📂 Estrutura de Pastas

### Backend Module

```
backend-module/
├── src/
│   ├── main/
│   │   ├── java/com/example/backend/
│   │   │   ├── BackendApplication.java      # Entrada da aplicação
│   │   │   │
│   │   │   ├── config/                      # Configurações
│   │   │   │   ├── EjbServiceConfig.java
│   │   │   │   ├── JpaConfig.java
│   │   │   │   └── JacksonConfig.java       # Serialização JSON
│   │   │   │
│   │   │   ├── controller/                  # REST Endpoints
│   │   │   │   ├── BeneficioController.java
│   │   │   │   ├── TransferenciaController.java
│   │   │   │   └── HealthController.java    # Health checks
│   │   │   │
│   │   │   ├── service/                     # Business Logic
│   │   │   │   ├── BeneficioService.java
│   │   │   │   ├── TransferenciaService.java
│   │   │   │   └── ValidationService.java   # Validações
│   │   │   │
│   │   │   ├── mapper/                      # DTO ↔ Entity
│   │   │   │   ├── BeneficioMapper.java
│   │   │   │   └── TransferenciaMapper.java
│   │   │   │
│   │   │   ├── dto/                         # Data Transfer Objects
│   │   │   │   ├── beneficio/
│   │   │   │   │   ├── BeneficioRequest.java
│   │   │   │   │   └── BeneficioResponse.java
│   │   │   │   ├── transferencia/
│   │   │   │   │   ├── TransferenciaRequest.java
│   │   │   │   │   └── TransferenciaResponse.java
│   │   │   │   └── error/
│   │   │   │       └── ErrorResponse.java
│   │   │   │
│   │   │   ├── exception/                   # Custom Exceptions
│   │   │   │   ├── ApplicationException.java
│   │   │   │   ├── SaldoInsuficienteException.java
│   │   │   │   ├── RecursoNaoEncontradoException.java
│   │   │   │   ├── ConflitoDadosException.java
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   │
│   │   │   └── util/                        # Utilities
│   │   │       ├── DateUtil.java
│   │   │       ├── MaskUtil.java            # CPF, etc
│   │   │       └── CurrencyUtil.java
│   │   │
│   │   └── resources/
│   │       ├── application.properties       # Configuração
│   │       ├── application-dev.properties
│   │       ├── application-prod.properties
│   │       └── logback-spring.xml           # Logging
│   │
│   └── test/
│       ├── java/com/example/backend/
│       │   ├── controller/
│       │   │   └── BeneficioControllerTest.java
│       │   ├── service/
│       │   │   └── BeneficioServiceTest.java
│       │   └── integration/
│       │       └── BeneficioIntegrationTest.java
│       │
│       └── resources/
│           └── application-test.properties
│
├── pom.xml
└── README.md
```

### EJB Module

```
ejb-module/
├── src/
│   ├── main/java/com/example/ejb/
│   │   ├── model/                          # Entidades JPA
│   │   │   ├── Beneficiario.java
│   │   │   ├── ContaBancaria.java
│   │   │   └── Transferencia.java
│   │   │
│   │   ├── repository/                     # Data Access Layer
│   │   │   ├── BeneficiarioRepository.java
│   │   │   ├── ContaBancariaRepository.java
│   │   │   └── TransferenciaRepository.java
│   │   │
│   │   ├── service/                        # EJB Services
│   │   │   ├── BeneficioEjbService.java
│   │   │   ├── TransferenciaEjbService.java
│   │   │   └── BaseEjbService.java         # Abstract base
│   │   │
│   │   ├── exception/                      # Domain Exceptions
│   │   │   ├── DomainException.java
│   │   │   ├── SaldoInsuficienteException.java
│   │   │   └── BeneficiarioNaoEncontradoException.java
│   │   │
│   │   ├── dto/                            # DTOs do EJB
│   │   │   ├── BeneficiarioDTO.java
│   │   │   └── ContaBancariaDTO.java
│   │   │
│   │   └── util/                           # Utilities
│   │       └── EjbUtil.java
│   │
│   └── test/
│       └── java/com/example/ejb/
│           ├── service/
│           │   └── BeneficioEjbServiceTest.java
│           └── repository/
│               └── BeneficiarioRepositoryTest.java
│
└── pom.xml
```

### Frontend Module

```
frontend/bip-frontend/
├── src/
│   ├── app/
│   │   ├── app.routes.ts                   # Configuração de rotas
│   │   ├── app.config.ts                   # Configuração da app
│   │   ├── app.tsx                         # Root component
│   │   ├── app.scss                        # Estilos globais
│   │   │
│   │   ├── core/                           # Singleton services
│   │   │   ├── services/
│   │   │   │   ├── beneficio.service.ts    # HTTP calls
│   │   │   │   ├── auth.service.ts         # Autenticação
│   │   │   │   └── notification.service.ts # Toast/Alerts
│   │   │   │
│   │   │   └── interceptors/
│   │   │       ├── api.interceptor.ts      # HTTP interceptor
│   │   │       └── error.interceptor.ts    # Error handling
│   │   │
│   │   ├── features/                       # Feature modules
│   │   │   └── beneficios/
│   │   │       ├── models/
│   │   │       │   ├── beneficiario.model.ts
│   │   │       │   └── transferencia.model.ts
│   │   │       │
│   │   │       ├── pages/
│   │   │       │   ├── list/
│   │   │       │   │   └── beneficio-list.component.ts
│   │   │       │   ├── detail/
│   │   │       │   │   └── beneficio-detail.component.ts
│   │   │       │   └── edit/
│   │   │       │       └── beneficio-edit.component.ts
│   │   │       │
│   │   │       └── beneficio.service.ts
│   │   │
│   │   └── shared/                         # Shared components
│   │       ├── components/
│   │       │   ├── loader/
│   │       │   └── error-message/
│   │       │
│   │       ├── directives/
│   │       │   └── phone-mask.directive.ts
│   │       │
│   │       └── pipes/
│   │           ├── currency.pipe.ts
│   │           └── date-br.pipe.ts
│   │
│   ├── environments/
│   │   ├── environment.ts                  # Produção
│   │   └── environment.development.ts      # Desenvolvimento
│   │
│   ├── main.ts                             # Point of entry
│   ├── index.html
│   └── styles.scss                         # Estilos globais
│
├── public/                                 # Assets estáticos
│   ├── favicon.ico
│   └── logo.png
│
├── angular.json                            # Configuração Angular
├── tsconfig.json                           # Configuração TypeScript
├── tsconfig.app.json                       # Config específica app
├── tsconfig.spec.json                      # Config testes
├── package.json
├── package-lock.json
└── README.md
```

### Database

```
db/
├── schema.sql                              # DDL - Estrutura das tabelas
├── seed.sql                                # DML - Dados iniciais
└── migrations/                             # (Futuro) Migrações incrementais
    └── 001_initial_schema.sql
```

---

## 🤝 Como Contribuir

### 1. Criar Feature Branch

```bash
# Padrão: feature/{descricao} ou fix/{descricao}
git checkout -b feature/novo-endpoint-beneficios
git checkout -b fix/validacao-cpf

# Commits frequentes e bem descritos
git commit -m "feat: adicionar endpoint GET /api/beneficios/{id}"
git commit -m "fix: corrigir validação de saldo em transferência"
```

### 2. Padrão de Commit

Seguir [Conventional Commits](https://www.conventionalcommits.org/pt-br/):

```
<tipo>(<escopo>): <descrição>

<corpo (opcional)>

<rodapé (opcional)>
```

**Tipos**:
- `feat`: Nova funcionalidade
- `fix`: Correção de bug
- `docs`: Alterações em documentação
- `style`: Formatação, sem mudança lógica
- `refactor`: Refatoração sem mudança funcional
- `perf`: Otimização de performance
- `test`: Adicionar/alterar testes
- `chore`: Mudanças build, CI, deps

**Exemplos**:
```
feat(controller): adicionar endpoint de transferência

fix(service): corrigir cálculo de saldo em transações concorrentes

docs: atualizar guia de instalação

refactor(mapper): extrair lógica comum para classe base
```

### 3. Pull Request

1. **Criar PR** com template:
```markdown
## Descrição
(Describir as mudanças)

## Tipo de Mudança
- [ ] Bug fix
- [ ] Nova feature
- [ ] Breaking change
- [ ] Doc update

## Checklist
- [ ] Código testado localmente
- [ ] Testes adicionados/atualizados
- [ ] Documentação atualizada
- [ ] Sem conflitos com main
```

2. **Aguardar Review**
   - Mínimo 1 aprovação requerida
   - Resolver comentários de review

3. **Merge** quando aprovado

---

## 🔄 Workflow de Desenvolvimento

### Desenvolvimento Local

```bash
# 1. Criar branch
git checkout -b feature/minha-feature

# 2. Fazer changes
vim backend-module/src/.../MyClass.java

# 3. Compilar
mvn clean install -DskipTests

# 4. Executar tests
mvn test

# 5. Iniciar backend
mvn -f backend-module spring-boot:run

# 6. Em outro terminal, iniciar frontend
cd frontend/bip-frontend && npm start

# 7. Testar manualmente em http://localhost:4200

# 8. Commit
git add .
git commit -m "feat: descrição das mudanças"

# 9. Push
git push origin feature/minha-feature

# 10. Abrir PR no GitHub
```

### Sincronizar com Main

```bash
# Buscar atualizações
git fetch origin

# Rebase (preferido)
git rebase origin/main

# Ou merge (alternativa)
git merge origin/main

# Resolver conflitos se houver
# Depois push --force-with-lease se rebase
git push --force-with-lease
```

---

## 🐛 Debugging

### Backend - Java

#### Usando IntelliJ IDEA

1. **Adicionar Breakpoint**
   - Clicar na margem esquerda da linha
   - Ícone vermelho aparece

2. **Iniciar em Debug Mode**
   - Run → Debug 'BackendApplication'
   - Ou usar atalho: Shift+F9

3. **Controles Debug**
   - **Step Over** (F10): Próxima linha
   - **Step Into** (F11): Entra em função
   - **Step Out** (Shift+F11): Sai da função
   - **Resume** (F9): Continua até próximo breakpoint
   - **Evaluate Expression** (Alt+F9): Executar código

4. **Watch Variables**
   - Aba "Variables" mostra todas as vars locais
   - Aba "Watches" para expressões personalizadas

#### Usando VSCode

```bash
# 1. Instalar Extension: Extension Pack for Java
# 2. Configurar launch.json

{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Launch Current File",
      "request": "launch",
      "mainClass": "${file}",
      "cwd": "${workspaceFolder}/backend-module"
    }
  ]
}

# 3. Adicionar breakpoint e F5
```

### Frontend - Angular/TypeScript

#### Browser DevTools

1. **Abrir DevTools**: F12
2. **Sources Tab**:
   - Adicionar breakpoint
   - Executar ação desejada
   - DevTools pausa na linha

3. **Console**:
```javascript
// Acessar componente
ng.probe(document.querySelector('app-root')).componentInstance

// Chamar método
document.querySelector('app-root').method()
```

#### VSCode Debugger

```json
// .vscode/launch.json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "chrome",
      "request": "attach",
      "name": "Attach",
      "port": 9222,
      "pathMapping": {
        "/": "${workspaceRoot}/",
        "/src": "${workspaceRoot}/frontend/bip-frontend/src"
      }
    }
  ]
}
```

### Logging

#### Java

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BeneficioService {
    private static final Logger log = 
        LoggerFactory.getLogger(BeneficioService.class);
    
    public void criar(BeneficioRequest req) {
        log.debug("Criando beneficiário: {}", req.getNome());
        log.info("Beneficiário criado com sucesso");
        log.warn("Saldo baixo: {}", req.getSaldo());
        log.error("Erro ao processar beneficiário", exception);
    }
}
```

Configuração em `application.properties`:
```properties
logging.level.com.example.backend=DEBUG
logging.level.org.springframework=INFO
logging.pattern.console=%d{HH:mm:ss.SSS} [%thread] %-5level %msg%n
```

#### TypeScript

```typescript
export class BeneficioService {
  constructor(private http: HttpClient) {}
  
  listar() {
    console.log('Chamando API...');
    return this.http.get('/api/beneficios').pipe(
      tap(data => console.log('Dados recebidos:', data)),
      catchError(err => {
        console.error('Erro na API:', err);
        return throwError(err);
      })
    );
  }
}
```

---

## ⚡ Performance

### Backend Optimization

#### 1. Queries Otimizadas

```java
// ❌ Ruim: N+1 Query Problem
List<Beneficiario> beneficiarios = repo.findAll();
for (Beneficiario b : beneficiarios) {
    // Isto gera uma query POR beneficiário!
    List<ContaBancaria> contas = b.getContas();
}

// ✅ Bom: Eager Loading
@Query("SELECT b FROM Beneficiario b JOIN FETCH b.contas")
List<Beneficiario> findAllWithContas();

List<Beneficiario> beneficiarios = repo.findAllWithContas();
for (Beneficiario b : beneficiarios) {
    // Dados já carregados
    List<ContaBancaria> contas = b.getContas();
}
```

#### 2. Caching

```java
@Service
public class BeneficioService {
    
    @Cacheable("beneficiarios")
    public BeneficioResponse obter(Long id) {
        // Primeira chamada: executa query
        // Próximas chamadas: retorna do cache
    }
    
    @CacheEvict("beneficiarios")
    public void atualizar(Long id, BeneficioRequest req) {
        // Invalida cache após update  
    }
}
```

Configurar em `application.properties`:
```properties
spring.cache.type=caffeine
spring.cache.caffeine.spec=maximumSize=500,expireAfterWrite=10m
```

#### 3. Pagination

```java
// ❌ Ruim: Carregar todos
List<Beneficiario> all = repo.findAll();

// ✅ Bom: Paginar
Page<Beneficiario> page = repo.findAll(
    PageRequest.of(0, 20, Sort.by("nome"))
);

// Controller
@GetMapping
public Page<BeneficioResponse> listar(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "20") int size) {
    
    Page<Beneficiario> beneficiarios = 
        service.listar(PageRequest.of(page, size));
    return beneficiarios.map(mapper::toResponse);
}
```

### Frontend Optimization

#### 1. OnPush Change Detection

```typescript
@Component({
  selector: 'app-beneficio',
  template: `...`,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BeneficioComponent {
  // Só re-render quando Input muda ou evento dispara
}
```

#### 2. TrackBy em *ngFor

```typescript
// ❌ Ruim: Re-cria todos os elementos
<div *ngFor="let item of items">{{item.nome}}</div>

// ✅ Bom: Apenas adiciona/remove
<div *ngFor="let item of items; trackBy: trackByFn">
  {{item.nome}}
</div>

trackByFn(index: number, item: Beneficiario): number {
  return item.id;
}
```

#### 3. Lazy Loading de Rotas

```typescript
const routes: Routes = [
  {
    path: 'beneficios',
    loadChildren: () => import('./features/beneficios/beneficios.module')
      .then(m => m.BeneficiosModule)
  }
];
```

---

## 🔐 Segurança

### Backend

#### 1. Validação de Input

```java
@PostMapping
public ResponseEntity<?> criar(
    @Valid @RequestBody BeneficioRequest req) {
    // Spring valida automaticamente
}

// DTO
public class BeneficioRequest {
    @NotEmpty(message = "Nome é obrigatório")
    @Size(min = 3, max = 100)
    private String nome;
    
    @Pattern(regexp = "^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$")
    private String cpf;
    
    @Positive(message = "Saldo deve ser positivo")
    private BigDecimal saldo;
}
```

#### 2. SQL Injection Prevention

```java
// ❌ Ruim NUNCA FAZER
String query = "SELECT * FROM beneficiario WHERE cpf='" + cpf + "'";
List result = entityManager.createNativeQuery(query).getResultList();

// ✅ Bom: Parameterized Queries
@Query("SELECT b FROM Beneficiario b WHERE b.cpf = ?1")
Beneficiario findByCpf(String cpf);
```

#### 3. CORS Configuration

```java
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins("http://localhost:4200")
                    .allowedMethods("GET", "POST", "PUT", "DELETE")
                    .allowedHeaders("*")
                    .maxAge(3600);
            }
        };
    }
}
```

### Frontend

#### 1. XSS Prevention

```typescript
// ❌ Ruim
<div [innerHTML]="userInput"></div>

// ✅ Bom: Angular sanitiza
<div>{{userInput}}</div>

// Se precisa HTML seguro
import { DomSanitizer } from '@angular/platform-browser';
<div [innerHTML]="sanitizer.sanitize(SecurityContext.HTML, userInput)">
</div>
```

#### 2. HTTPS Only

```typescript
// environment.ts
export const environment = {
  apiUrl: 'https://api.example.com'  // Sempre HTTPS em produção
};
```

---

## ❓ FAQ do Desenvolvedor

### P: Como adicionar nova dependência?

**Backend (Maven)**:
```bash
# Pesquisar em https://mvnrepository.com
# Adicionar em pom.xml e executar
mvn clean install
```

**Frontend (npm)**:
```bash
npm install nome-do-pacote
npm install --save-dev nome-dev-dependencies
```

### P: Como gerar código boilerplate?

**Spring Boot** tem gerador: https://start.spring.io/

**Angular CLI**:
```bash
ng generate component features/beneficios/list
ng generate service core/services/beneficio
ng generate interface features/beneficios/beneficiario
```

### P: Como testar endpoint sem Postman?

```bash
# curl
curl -X GET http://localhost:8080/api/beneficios

# HTTPie (mais legível)
http GET localhost:8080/api/beneficios

# VSCode REST Client Extension
GET http://localhost:8080/api/beneficios
```

### P: Como resetar o banco de dados?

```bash
# MySQL
mysql -u root -p bip_teste < db/schema.sql
mysql -u root -p bip_teste < db/seed.sql

# PostgreSQL
psql -U bip_user -d bip_teste -f db/schema.sql
psql -U bip_user -d bip_teste -f db/seed.sql
```

### P: Erro "The bean definition does not have a constructor"

**Solução**: Adicionar constructor vazio em classe com @Component/@Service

```java
@Service
public class MyService {
    // Spring precisa do constructor padrão (mesmo que vazio)
    public MyService() {}
    
    public MyService(Dependency dep) {
        this.dependency = dep;
    }
}
```

---

**Versão**: 1.0  
**Data**: 4 de Março de 2026
