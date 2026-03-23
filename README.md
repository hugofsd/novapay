# NovaPay

NovaPay é um projeto de estudo que simula um núcleo bancário com microserviços, explorando comunicação assíncrona via Kafka, autenticação JWT e roteamento centralizado com Spring Cloud Gateway.

## O que o projeto cobre na prática

- Arquitetura de microserviços com responsabilidades isoladas
- Comunicação assíncrona com Apache Kafka (producers e consumers)
- Autenticação stateless com JWT sem Spring Security completo
- Roteamento centralizado com Spring Cloud Gateway MVC
- Migrations de banco de dados com Flyway
- Infraestrutura containerizada com Docker Compose

---

## Arquitetura

```
Cliente
  │
  ▼
Gateway (8080) — autenticação JWT
  │
  ├──► account-service     (8081) ──► Kafka: account-events
  │
  ├──► transaction-service (8082) ──► Kafka: transaction-events
  │
  ├──► fraud-service       (8083) ──◄── Kafka: transaction-events
  │                                ──► Kafka: fraud-events
  │
  └──► notification-service (8084) ──◄── Kafka: account-events
                                    ──◄── Kafka: fraud-events
```

| Serviço              | Porta | Banco           | Responsabilidade                          |
|----------------------|-------|-----------------|-------------------------------------------|
| gateway              | 8080  | db_gateway      | Roteamento + autenticação JWT             |
| account-service      | 8081  | db_account      | Criação e gestão de contas                |
| transaction-service  | 8082  | db_transaction  | Transferências, depósitos e saques        |
| fraud-service        | 8083  | db_fraud        | Análise de fraude via Kafka               |
| notification-service | 8084  | db_notification | Notificações disparadas por eventos Kafka |

---

## Tecnologias

- **Java 17** + **Spring Boot 3.5.11**
- **Spring Cloud Gateway MVC** — roteamento centralizado
- **JWT** (jjwt 0.12.3) — autenticação stateless
- **Apache Kafka** + **Zookeeper** — mensageria assíncrona
- **MySQL 8** — um banco isolado por serviço
- **Flyway** — migrations de banco de dados
- **Docker** + **Docker Compose** — infraestrutura containerizada
- **Lombok** — redução de boilerplate

---

## Pré-requisitos

- Docker Desktop com WSL2 habilitado
- Java 17
- Maven (ou usar o `./mvnw` de cada projeto)

---

## Como rodar

### 1. Subir a infraestrutura (Docker)

```bash
cd novapay-infra
docker compose up -d
```

Aguarde todos os containers ficarem `healthy`:

```bash
docker compose ps
```

> Para comandos detalhados de infraestrutura, veja [novapay-infra/INFRA-COMMANDS.md](novapay-infra/INFRA-COMMANDS.md)

### 2. Subir os serviços

Rode cada serviço na ordem abaixo pelo IntelliJ ou via terminal:

```bash
# 1. account-service
cd novapay-account-service/account-service
./mvnw spring-boot:run

# 2. transaction-service
cd novapay-transaction-service/transaction-service
./mvnw spring-boot:run

# 3. fraud-service
cd novapay-fraud-service/fraud-service
./mvnw spring-boot:run

# 4. notification-service
cd novapay-notification-service/notification-service
./mvnw spring-boot:run

# 5. gateway (por último)
cd novapay-gateway/gateway
./mvnw spring-boot:run
```

> A ordem importa: o gateway deve subir por último pois depende dos outros serviços estarem disponíveis.

---

## Autenticação

Todos os endpoints (exceto `/auth/register` e `/auth/login`) exigem um token JWT válido.

**1. Registrar usuário:**
```
POST http://localhost:8080/auth/register
Content-Type: application/json

{
  "username": "seu_usuario",
  "password": "sua_senha"
}
```

**2. Fazer login e obter o token:**
```
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "username": "seu_usuario",
  "password": "sua_senha"
}
```

Resposta:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer"
}
```

**3. Usar o token nos próximos requests:**
```
Authorization: Bearer <token>
```

> O token expira em **24 horas**.

---

## Endpoints

### Gateway — Auth
| Método | Rota             | Descrição              | Auth |
|--------|------------------|------------------------|------|
| POST   | /auth/register   | Registrar usuário      | Não  |
| POST   | /auth/login      | Login e obter token    | Não  |

### Account Service — via Gateway (8080)
| Método | Rota                    | Descrição              |
|--------|-------------------------|------------------------|
| POST   | /accounts               | Criar conta            |
| GET    | /accounts               | Listar contas          |
| GET    | /accounts/{id}          | Buscar conta por ID    |
| PATCH  | /accounts/{id}/status   | Atualizar status       |

### Transaction Service — via Gateway (8080)
| Método | Rota                         | Descrição                       |
|--------|------------------------------|---------------------------------|
| POST   | /transactions                | Criar transação (TRANSFER/DEPOSIT/WITHDRAWAL) |
| GET    | /transactions                | Listar transações               |
| GET    | /transactions/{id}           | Buscar transação por ID         |
| GET    | /transactions/account/{id}   | Listar por conta                |

### Fraud Service — via Gateway (8080)
| Método | Rota        | Descrição                        |
|--------|-------------|----------------------------------|
| GET    | /fraud/{id} | Buscar análise por transação ID  |

### Notification Service — via Gateway (8080)
| Método | Rota           | Descrição             |
|--------|----------------|-----------------------|
| GET    | /notifications | Listar notificações   |

---

## Testes

A collection do Postman com todos os endpoints está em:

```
novapay-infra/NovaPay.postman_collection.json
```

Importe no Postman via **File → Import** e use na seguinte ordem:

1. `POST /auth/register` — criar usuário
2. `POST /auth/login` — obter token
3. `POST /accounts` — criar conta de origem
4. `POST /accounts` — criar conta de destino
5. `POST /transactions` — realizar transferência
6. `GET /fraud/{id}` — verificar análise de fraude
7. `GET /notifications` — verificar notificações geradas

### Kafka UI

Acesse `http://localhost:8090` para visualizar os tópicos e mensagens em tempo real.

---

## Banco de dados

| Serviço              | Container          | Porta | Banco           |
|----------------------|--------------------|-------|-----------------|
| account-service      | mysql-account      | 3316  | db_account      |
| transaction-service  | mysql-transaction  | 3307  | db_transaction  |
| fraud-service        | mysql-fraud        | 3308  | db_fraud        |
| notification-service | mysql-notification | 3309  | db_notification |
| gateway              | mysql-gateway      | 3310  | db_gateway      |

**Usuário:** `novapay` | **Senha:** `novapay_pass`

```bash
# Exemplo — acessar banco do account-service
docker exec -it mysql-account mysql -u novapay -pnovapay_pass db_account
```

---

## Variáveis de ambiente

O arquivo `novapay-infra/.env` contém as credenciais usadas pelo Docker Compose:

```env
MYSQL_ROOT_PASSWORD=novapay_root
MYSQL_USER=novapay
MYSQL_PASSWORD=novapay_pass
MYSQL_DB_GATEWAY=db_gateway
MYSQL_DB_ACCOUNT=db_account
MYSQL_DB_TRANSACTION=db_transaction
MYSQL_DB_FRAUD=db_fraud
MYSQL_DB_NOTIFICATION=db_notification
```
