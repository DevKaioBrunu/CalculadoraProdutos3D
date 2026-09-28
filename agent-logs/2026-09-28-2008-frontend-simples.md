# Front-end simples para testes

- Data: 2026-09-28 20:08
- Solicitação do usuário: Criar um front-end simples para testar a aplicação como usuário.

## O que foi feito
- Criada a página `src/main/resources/static/index.html`.
- Criado o estilo responsivo em `src/main/resources/static/styles.css`.
- Criada a integração JavaScript em `src/main/resources/static/app.js`.
- A interface permite calcular preços e executar cadastro, listagem, edição e exclusão de filamentos.

## Decisões e suposições
- Foi usado HTML, CSS e JavaScript puro para evitar novas dependências e permitir que o Spring Boot sirva a interface diretamente em `/`.
- A interface usa os endpoints existentes e assume execução no mesmo host, evitando necessidade de CORS.
- A exclusão solicita confirmação no navegador antes de chamar a API.

## Testes
- Verificada a estrutura dos arquivos e a ausência de alterações nas migrations existentes.
- Build e testes automatizados não puderam ser executados porque o ambiente continua sem Java/Maven e com Maven Wrapper incompleto.

## Pendências ou próximos passos
- Executar a aplicação com PostgreSQL configurado e acessar `http://localhost:8080/` para validar o fluxo completo.
