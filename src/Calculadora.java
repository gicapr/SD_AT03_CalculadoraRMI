import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Stack;

public class Calculadora extends UnicastRemoteObject implements ICalculadora {
    
    public Calculadora() throws RemoteException {
        super();
    }

    // --- Implementação dos métodos das operações básicas adicionados na interface da calculadora RMI ---
    public double soma(double a, double b) throws RemoteException {
        System.out.println("Calculando: " + a + " + " + b);
        return a + b;
    }

    public double subtrai(double a, double b) throws RemoteException {
        System.out.println("Calculando: " + a + " - " + b);
        return a - b;
    }

    public double multiplica(double a, double b) throws RemoteException {
        System.out.println("Calculando: " + a + " * " + b);
        return a * b;
    }

    public double divide(double a, double b) throws RemoteException {
        System.out.println("Calculando: " + a + " / " + b);
        if (b == 0) throw new RemoteException("Divisão por zero!");
        return a / b;
    }

    // --- Modo direto: Servidor recebe a expressão matemática e calcula toda a expressão ---
    public double calcularExpressao(String expressao) throws RemoteException {
        System.out.println("Processando expressão completa (Server-side): " + expressao);
        try {
            // Parser interno para avaliar a expressão matemática
            return new ParserInterno().resolver(expressao);
        } catch (Exception e) {
            throw new RemoteException("Erro ao calcular no servidor: " + e.getMessage());
        }
    }

    // Classe auxiliar interna para realizar o cálculo matemático da expressão
    private class ParserInterno {
        public double resolver(String expression) {
            Stack<Double> values = new Stack<>();
            Stack<Character> ops = new Stack<>();
            char[] tokens = expression.toCharArray();

            for (int i = 0; i < tokens.length; i++) {
                if (tokens[i] == ' ') continue;

                if (tokens[i] >= '0' && tokens[i] <= '9') {
                    StringBuilder sbuf = new StringBuilder();
                    while (i < tokens.length && ((tokens[i] >= '0' && tokens[i] <= '9') || tokens[i] == '.'))
                        sbuf.append(tokens[i++]);
                    values.push(Double.parseDouble(sbuf.toString()));
                    i--;
                } else if (tokens[i] == '(') {
                    ops.push(tokens[i]);
                } else if (tokens[i] == ')') {
                    while (ops.peek() != '(')
                        values.push(aplicarOpLocal(ops.pop(), values.pop(), values.pop()));
                    ops.pop();
                } else if (tokens[i] == '+' || tokens[i] == '-' || tokens[i] == '*' || tokens[i] == '/') {
                    while (!ops.empty() && temPrecedencia(tokens[i], ops.peek()))
                        values.push(aplicarOpLocal(ops.pop(), values.pop(), values.pop()));
                    ops.push(tokens[i]);
                }
            }
            while (!ops.empty())
                values.push(aplicarOpLocal(ops.pop(), values.pop(), values.pop()));

            return values.pop();
        }

        private boolean temPrecedencia(char op1, char op2) {
            if (op2 == '(' || op2 == ')') return false;
            if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) return false;
            return true;
        }

        private double aplicarOpLocal(char op, double b, double a) {
            switch (op) {
                case '+': return a + b;
                case '-': return a - b;
                case '*': return a * b;
                case '/': return a / b;
            }
            return 0;
        }
    }
}