# sd1
Trabalho Prático 1 de Sistemas Distribuídos


# File servers (from root dir)
java -cp ./bin/ FileServerWS . localhost/myContactServer FileServer
java -cp ./bin/ FileServer . localhost/myContactServer FileServer


# Contact server (from root dir)
java -cp ./bin/ ContactServer


# Client (from root dir)
java -cp ./bin FileClient rmi://localhost/myContactServer c1


#Executar DropBox server
java -cp ./bin:.:"lib/*" DropboxServer . localhost/myContactServer FileServer


# Compilar tudo (from root dir)
javac -d ./bin -cp "lib/*" -sourcepath src src/*.java


# Wsimport (from src dir)
wsimport -d ../bin/ -s . -p ws http://localhost:8080/FileServerWs?wsdl


#Compile with jar in classpath
javac -d ./bin -cp "lib/*" -sourcepath src src/*.java


#comandos uteis
cp README.md FileServer@sync_dir/readme.md

mkdir FileServer@sync_dir/ola

rmdir FileServer@sync_dir/ola

rm FileServer@sync_dir/readme.md
