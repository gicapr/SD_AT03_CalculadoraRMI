# SD_AT03_CalculadoraRMI

Aplicação cliente_servidor que utiliza Java RMI, para uma calculadora que possui dois modos:

MODO A - O Cliente utiliza um parser de pilhas para quebrar a expressão matemática em pequenas partes binárias. Para cada operação que é identificada, o Cliente abre uma conexão e faz uma chamada remota separada ao servidor, que retorna o resultado daquela operação.

MODO B - O Servidor concentra todo o processo, pois o Cliente apenas envia a string com a expressão matemática bruta em uma única chamada remota. O servidor recebe a expressão, a interpreta com seu parser local, e retorna o seu resultado para o Cliente.
