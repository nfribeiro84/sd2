# sd1
Trabalho Prático 1 de Sistemas Distribuídos

# File servers
java -cp ./bin/ FileServerWS . localhost/myContactServer FileServerWs
java -cp ./bin/ FileServer . localhost/myContactServer FileServer

# Contact server
java -cp ./bin/ ContactServer

# Client
java -cp ./bin FileClient rmi://localhost/myContactServer c1


# Compilar tudo (root dir)
javac -d ../bin *.java
javac -d ./bin -cp ./src/ src/*.java	


# Wsimport (from src dir)
wsimport -d ../bin/ -s . -p ws http://localhost:8080/FileServerWs?wsdl

