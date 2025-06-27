# URL Shortener

Serviço serverless de encurtamento de URLs usando AWS Lambda e S3.

## Como Funciona

Recebe uma URL longa e retorna um ID único de 8 caracteres. Os dados são armazenados no Amazon S3.

## Tecnologias

- Java 17 + Maven para construção
- AWS Lambda + S3 para execução e armazenamento
- Jackson para serialização/deserialização
- Lombok para simplificação de código

## Como configurar na AWS

## Requisitos AWS

- Permissões S3: `PutObject`, `GetObject`
- Permissões CloudWatch Logs
- Variável de ambiente: `S3_BUCKET_NAME`

### Build
```bash
mvn clean package
```

### Deploy na AWS
1. Crie um bucket S3
2. Faça upload do JAR para Lambda
3. Configure handler: `com.matheus.url_shortener.Main::handleRequest`
4. Defina a variavel de ambiente `S3_BUCKET_NAME` com o nome do bucket S3.

## API

**POST** Request:
```json
{
    "originalUrl": "https://exemplo.com/url-muito-longa",
    "expirationTime": "1735689600000"
}
```

**Response**:
```json
{
    "shortUrl": "a1b2c3d4"
}
```

## Pré-requisitos projeto
- Java 17+
- Maven 3.6+