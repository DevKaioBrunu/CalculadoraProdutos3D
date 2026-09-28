# Calculadora de Produtos 3D

Backend REST para calcular o custo e o preço de venda de produtos impressos em
impressora 3D. O projeto está em fase inicial e atualmente oferece o cálculo
manual de uma impressão; o cadastro de produtos, filamentos e uma interface web
ainda serão implementados.

## Tecnologias

- Java 21
- Spring Boot 4
- Spring Web MVC
- Spring Data JPA
- PostgreSQL 17
- Flyway
- Maven
- Docker e Docker Compose

## Como executar localmente

### Pré-requisitos

- JDK 21
- Maven 3.9 ou o Maven Wrapper incluído no projeto
- PostgreSQL acessível

Configure a conexão do banco pelas propriedades `SPRING_DATASOURCE_*` ou
ajuste `src/main/resources/application.properties`. Em seguida, execute:

```bash
./mvnw spring-boot:run
```

A aplicação ficará disponível em `http://localhost:8080`. As migrations do
Flyway serão executadas durante a inicialização.

## Como executar com Docker Compose

Defina a senha do PostgreSQL na variável `DB_PASSWORD` e suba os serviços:

```bash
export DB_PASSWORD=uma-senha-segura
docker compose up
```

O backend ficará disponível em `http://localhost:8080` e o PostgreSQL será
exposto apenas em `127.0.0.1:5432`.

## API disponível

### Calcular preço de uma impressão

```http
POST /api/calculadora/precificar
Content-Type: application/json
```

Exemplo:

```bash
curl -X POST http://localhost:8080/api/calculadora/precificar \
  -H 'Content-Type: application/json' \
  -d '{
    "filamentWeightGrams": 500,
    "filamentPricePerKg": 80,
    "printTimeHours": 10,
    "printerPowerWatts": 200,
    "energyPricePerKwh": 1,
    "laborCost": 10,
    "otherCosts": 5,
    "profitMarginPercentage": 50
  }'
```

Resposta esperada:

```json
{
  "filamentCost": 40.00,
  "energyCost": 2.00,
  "totalCost": 57.00,
  "salePrice": 85.50
}
```

Os valores numéricos não podem ser negativos. O cálculo considera:

```text
custo do filamento = gramas / 1000 × preço por kg
custo de energia = watts / 1000 × horas × preço do kWh
custo total = filamento + energia + mão de obra + outros custos
preço de venda = custo total × (1 + percentual de lucro / 100)
```

> `profitMarginPercentage` funciona atualmente como um acréscimo sobre o
> custo (markup). Ele ainda não representa margem sobre o preço final.

## Estado atual

Implementado:

- Endpoint REST de precificação.
- Cálculo de filamento, energia, custos adicionais e preço de venda.
- Validação básica dos dados de entrada.
- Arredondamento monetário para duas casas decimais.
- Migration inicial da tabela de filamentos.
- Teste unitário do cálculo principal.

Ainda planejado:

- API de cadastro e manutenção de filamentos.
- Cadastro de impressoras e seus custos de energia/depreciação.
- Cadastro de produtos e histórico de cálculos.
- Detalhamento de desperdício, manutenção, acabamento, embalagem, impostos e
  taxas de venda.
- Interface web para uso da calculadora.
- Tratamento padronizado de erros e testes de integração.
