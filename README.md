# Descrição do Projeto
Sistema completo de encurtamento de URLs construído com Java 17 e AWS Lambda. Inclui dois microsserviços: um para encurtar URLs longas e outro para redirecionamento com controle de expiração. Utiliza S3 para armazenamento persistente e oferece APIs REST escaláveis e serverless.

# Principais características:

- Encurtamento de URLs com IDs únicos de 8 caracteres
- Redirecionamento automático com validação de expiração
- Arquitetura serverless (AWS Lambda + S3)
- Controle de tempo de vida das URLs
- APIs REST simples e eficientes
