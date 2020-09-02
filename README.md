# prova-programador
Este é o projeto de candidatura à vaga de Programador Java
***
## Fazer o build da aplicação

	$ gradle wrapper --gradle-version=6.1.1

Após instalado o Gradle Wrapper, é possível fazer o build da aplicação

	$ gradlew clean build

## Executando a aplicação
Para executar a aplicação, basta iniciar o servidor provido pelo SpringBoot que o serviço de processamento de arquivos estará online.

	$ cd build/libs
	$ java -jar prova-programador.jar

### Tecnologias utilizadas
* JDK 11+
* Gradle Build Tool
* SpringBoot (2.3.3)
* Apache Commons
