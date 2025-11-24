import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class Servidor {
    public static void main(String[] args) {
        try {
            // Cria o registro RMI na porta 1099 localmente para que os clientes encontrem os serviços
            LocateRegistry.createRegistry(1099);
            
            // Instancia o objeto real que contém a implementação da calculadora
            ICalculadora c = new Calculadora();
            
            // Vincula o objeto 'c' ao nome "CalculadoraService" no registro RMI
            // O cliente usará este nome para obter a referência remota (stub)
            Naming.rebind("//localhost/CalculadoraService", c);
            System.out.println("Servidor da Calculadora pronto");
        } catch (Exception e) {
            System.out.println("Erro no Servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}