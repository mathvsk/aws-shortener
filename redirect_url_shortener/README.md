# Redirect URL Shortener

Serviço serverless em Java que redireciona URLs encurtadas para URLs originais, com controle de expiração. Roda em AWS Lambda com armazenamento no S3.

## Como funciona
1. Usuário acessa URL encurtada → API Gateway → Lambda
2. Lambda busca dados da URL no S3  
3. Se não expirou: redireciona (HTTP 302)
4. Se expirou: retorna erro (HTTP 410)

## Tecnologias
- Java 17 + Maven para construção
- AWS Lambda + S3 para execução e armazenamento
- Jackson (JSON) para serialização/deserialização
- Lombok para simplificação de código

## Estrutura de dados no S3
```json
{
    "originalUrl": "https://exemplo.com",
    "expirationTime": 1735689600
}
```
Arquivos salvos como: `{codigo}.json` (ex: `abc123.json`)

## Como usar

### Acessar URL encurtada
Para acessar uma URL encurtada, faça uma requisição GET para o endpoint, passando o `código` da URL como parte do caminho.

### Respostas
- **302**: Redirecionamento bem-sucedido
- **410**: URL expirada  
- **400**: URL inválida

## Configurar na AWS

### Requisitos AWS

- Permissões S3: `GetObject`
- Permissões CloudWatch Logs
- Variável de ambiente: `AWS_BUCKET_NAME`

### Compilar
```bash
mvn clean package
```

### Deploy AWS
  1. Crie ou utilize um bucket S3.
  2. Faça upload do arquivo JAR gerado.
  3. Crie uma função Lambda com o runtime Java 17.
  4. Configure o hadnler para `com.matheus.redirect_url_shortener.Main::handleRequest`.
  5. Configure a variavel de ambiente `AWS_BUCKET_NAME` com o nome do bucket S3.

## Pré-requisitos projeto
- Java 17+
- Maven 3.6+