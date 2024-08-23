# Processamento de Pedidos
## Serviço OrderBatch

### Stack:
* Java21 + Spring Batch+Scheduler: java por ser a linguagem que domino mais, e o spring batch foi pensado para esse tipo de processamento "offline" com demanda alta de processamento
* Postgresql + flyway: modelo classico de bd relacional, usuario/cliente, ordens, linhas de ordem ... além disso o batch precisa de um bd relacional (mongo é experimental ainda)
* Aws S3: solução para armazenar arquivos (aqui estamos usando o localstack que emula o s3)

### Arquitetura:

* Aqui foi pensado em 2 serviços. O OrderApi para cuidar das apis rest, tanto para upload como de consulta dos dados e o OrderBatch para processamento dos arquivos carregados
* O spring batch foi pensado para esse tipo de processamento, nesse projeto foi adotado o modelo usando tasklet (classe S3Tasklet)
* O serviço pode rodar tanto standalone em um servidor (ec2 por exemplo), ou como container docker ou pod kubernates. Com pequeno ajustes pode rodar inclusive como aws lambda.
* O serviço foi pensado para poder processar diversos tipos de arquivos, aqui implementado apenas para o layout fornecido (de pedidos)
  * A classe TextProcessorFactory retorna o serviço correspondete ao tipo de registro que esta sendo processado, no caso uma instância do OrderRecordProcessor
  * Os textProcessor usam o TextRecordBuilder para processar as linhas do arquivo e retornar o objeto correspondente (anotions+reflection)
  
![alt text](https://github.com/jfrossetto/orderbatch/blob/master/desing.png?raw=true)


### Para Executar:
* O projeto usa o github actions para gerar a imagem docker do serviço e publicar no dockerhub sempre que commita na master
* via docker compose
  * após baixar o o projeto (git clone)
  * (via plugin) docker compose -f docker/docker-compose-app.yml up
  * ou docker-compose -f docker/docker-compose-app2.yml up
  
### Cobertura dos testes:

![alt text](https://github.com/jfrossetto/orderbatch/blob/master/coverage.png?raw=true)

### Board do Projeto:

![alt text](https://github.com/jfrossetto/orderbatch/blob/master/jira.png?raw=true)

