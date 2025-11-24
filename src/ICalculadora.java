import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ICalculadora extends Remote {
    
    // Evolução do projeto original: Alterado para double para suportar a operação de divisão e números reais
    public double soma(double a, double b) throws RemoteException;

    // Evolução do projeto original: Foram adicionadas as operações de subtração, multiplicação e divisão
    public double subtrai(double a, double b) throws RemoteException;
    public double multiplica(double a, double b) throws RemoteException;
    public double divide(double a, double b) throws RemoteException;

    // Evolução do projeto original: Adição do cálculo de expressões matemáticas
    public double calcularExpressao(String expressao) throws RemoteException;
}
