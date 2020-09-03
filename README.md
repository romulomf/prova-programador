# prova-programador
Este é o projeto de candidatura à vaga de Programador Java
***
## Fazer o build da aplicação

Para fazer o build da aplicação é necessário ter o [gradle](https://gradle.org/install/) instalado. A partir da instalação local do gradle, é recomendável instalar o gradle wrapper dentro da aplicação pois ele não está empacotado junto com o código fonte.

	$ gradle wrapper --gradle-version=6.6.1

Após instalado o Gradle Wrapper, é possível fazer o build da aplicação

	$ gradlew clean build

## Executando a aplicação
Para executar a aplicação, basta iniciar o servidor provido pelo SpringBoot que o serviço de processamento de arquivos estará online.

	$ cd build/libs
	$ java -jar prova-programador.jar

### Tecnologias utilizadas
* JDK 11
* Gradle Build Tool
* SpringBoot (2.3.3)
* Apache Commons
