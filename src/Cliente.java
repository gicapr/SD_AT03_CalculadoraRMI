import java.rmi.Naming;
import java.util.Scanner;
import java.util.Stack;
import java.rmi.RemoteException;

public class Cliente {
    public static void main(String[] args) {
        
        try (Scanner scanner = new Scanner(System.in)) {
            
            // Procura o serviço no registro RMI
            String objName = "//localhost/CalculadoraService";
            ICalculadora c = (ICalculadora) Naming.lookup(objName);
            System.out.println("Conectado ao servidor RMI.");
            
            while(true) {
                System.out.println("\n=== Calculadora RMI ===");
                System.out.println("1. Modo Decomposto (Cliente interpreta e o servidor calcula operações parte a parte)");
                System.out.println("2. Modo Direto (Servidor calcula a expressão completa)");
                System.out.println("3. Sair");
                System.out.print("Opção: ");
                
                int opt = scanner.nextInt();
                scanner.nextLine(); // limpar o buffer

                if(opt == 3) break;

                System.out.print("Digite a expressão (ex: (10+5)*2): ");
                String expr = scanner.nextLine();

                if (opt == 1) {
                    System.out.println("--- Iniciando Modo Decomposto ---");
                    // Parser que roda no cliente e faz chamadas remotas passo a passo
                    ParserCliente parser = new ParserCliente(c);
                    double res = parser.resolver(expr);
                    System.out.println("Resultado Final: " + res);

                } else if (opt == 2) {
                    System.out.println("--- Iniciando Modo Direto ---");
                    // Envia a expressão completa para o servidor
                    double res = c.calcularExpressao(expr);
                    System.out.println("Resultado Final: " + res);
                }
            }
            
        } catch (Exception e) {
            System.out.println("Erro no Cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Classe auxiliar para o Modo Decomposto
    static class ParserCliente {
        private ICalculadora calc;

        public ParserCliente(ICalculadora c) {
            this.calc = c;
        }

        public double resolver(String expression) throws RemoteException {
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
                        values.push(aplicarOpRemota(ops.pop(), values.pop(), values.pop()));
                    ops.pop();
                } else if (tokens[i] == '+' || tokens[i] == '-' || tokens[i] == '*' || tokens[i] == '/') {
                    while (!ops.empty() && temPrecedencia(tokens[i], ops.peek()))
                        values.push(aplicarOpRemota(ops.pop(), values.pop(), values.pop()));
                    ops.push(tokens[i]);
                }
            }
            while (!ops.empty())
                values.push(aplicarOpRemota(ops.pop(), values.pop(), values.pop()));

            return values.pop();
        }

        private boolean temPrecedencia(char op1, char op2) {
            if (op2 == '(' || op2 == ')') return false;
            if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) return false;
            return true;
        }

        private double aplicarOpRemota(char op, double b, double a) throws RemoteException {
            System.out.println("[CLIENTE] RPC Individual: " + a + " " + op + " " + b);
            switch (op) {
                case '+': return calc.soma(a, b);
                case '-': return calc.subtrai(a, b);
                case '*': return calc.multiplica(a, b);
                case '/': return calc.divide(a, b);
            }
            return 0;
        }
    }
}