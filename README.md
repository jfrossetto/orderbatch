# Processamento de Pedidos
## Serviço OrderBatch

### Stack:
* Java21 + Spring Batch+Scheduler (v3) : java por ser a linguagem que domino mais, e o spring batch foi pensado para esse tipo de processamento "offline" com demanda alta de processamento
* Postgresql + flyway: modelo classico de bd relacional, usuario/cliente, ordens, linhas de ordem ... além disso o batch precisa de um bd relacional (mongo é experimental ainda)
* Aws S3: solução para armazenar arquivos (aqui estamos usando o localstack que emula o s3)

### Arquitetura:

* Aqui foi pensado em 2 serviços. O OrderApi para cuidar das apis rest, tanto para upload como de consulta dos dados e o OrderBatch para processamento dos arquivos carregados
* O spring batch foi pensado para esse tipo de processamento, nesse projeto foi adotado o modelo usando tasklet (classe S3Tasklet)
* O serviço pode rodar tanto standalone em um servidor (ec2 por exemplo), ou como container docker ou pod kubernates. Com pequeno ajustes pode rodar inclusive como aws lambda.
* O serviço foi pensado para poder processar diversos tipos de arquivos, aqui implementado apenas para o layout fornecido (de pedidos)
  * A classe TextProcessorFactory retorna o serviço correspondete ao tipo de registro que esta sendo processado, no caso uma instância do OrderRecordProcessor
  * Os textProcessor usam o TextRecordBuilder para processar as linhas do arquivo e retornar o objeto correspondente (anotions+reflection)
  * Os registros são salvos no banco de dados de forma batch
  
![alt text](https://github.com/jfrossetto/orderbatch/blob/master/desing.png?raw=true)

### Para Executar:
* O projeto usa o github actions para gerar a imagem docker do serviço e publicar no dockerhub sempre que commita na master
* via docker compose
  * após baixar o o projeto (git clone)
  * (via plugin) docker compose -f docker/docker-compose-app.yml up
  * ou docker-compose -f docker/docker-compose-app2.yml up
  * ao subir a aplicação já criar as tabelas no postgres via flyway e o bucket no localstack (s3) caso não exista ainda
  * se quiserem verificar as tabelas no banco, o mesmo sobe na port 5432 e as tabelas são users, orders e order_products 
  
### Cobertura dos testes:

* pluging do jacoco para gradle, configurado com 80% cobertura (apenas para as classes principais)

![alt text](https://github.com/jfrossetto/orderbatch/blob/master/coverage.png?raw=true)

### Board do Projeto:
 
* Para controle das atividades criei um projeto no jira bem simples, mais para acompanhar as task. Algumas continuaram em aberto, pois pretendo aproveitar o projeto para outros estudos. O Spring Batch era algo que ainda não tinha usado.

![alt text](https://github.com/jfrossetto/orderbatch/blob/master/jira.png?raw=true)

