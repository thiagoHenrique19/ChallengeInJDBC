import org.DesafioJDBC.cliente.model.ClienteService;
import org.DesafioJDBC.cliente.model.PedidoService;
import org.DesafioJDBC.cliente.model.ProdutoService;
import org.DesafioJDBC.produto.view.CriaConexao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class Application {
    Connection connection = CriaConexao.getConexao();
    public static void main(String[] args) {
        try (Scanner terminal = new Scanner(System.in)) {
            Application app = new Application();
            app.menu(terminal);
        } catch (SQLException e) {
            System.out.println("Ocorreu um erro de SQL: " + e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public void menu(Scanner terminal) throws SQLException {
        int option = -1;
        while (option != 6) {
            System.out.println("Escolha o que você deseja fazer:");
            System.out.println("=======================================");
            System.out.println("1-- Adicionar um produto.");
            System.out.println("2-- Adicionar um cliente.");
            System.out.println("3-- Fazer um pedido.");
            System.out.println("4-- Ver os dados do ultimo pedido feito.");
            System.out.println("5-- Listar pedidos.");
            System.out.println("6-- Sair");

            option = Integer.parseInt(terminal.nextLine());
            executeOption(option, terminal);

        }
    }
    private void executeOption(int option, Scanner terminal) throws SQLException {
        ProdutoService produtoService = new ProdutoService(connection);
        ClienteService clienteService = new ClienteService(connection);
        PedidoService pedidoService = new PedidoService(connection);

        switch (option) {
            case 1:
                produtoService.receiverProdutcInfo();
                break;
            case 2:
                clienteService.createClient(terminal);
                break;
            case 3:
                pedidoService.criarPedidoV2();
                break;
            case 4:
                pedidoService.viewPedido(terminal);
                break;
            case 5:
                pedidoService.listPedido(terminal);
                break;
            case 6:
                System.out.println("Aplicação finalizada");
                break;
            default:
                System.out.println("Opção inválida");
        }
    }
}