import org.DesafioJDBC.cliente.model.ClienteService;
import org.DesafioJDBC.cliente.model.PedidoService;
import org.DesafioJDBC.cliente.model.ItemPedidoService;
import org.DesafioJDBC.cliente.model.ProdutoService;
import org.DesafioJDBC.produto.view.CriaConexao;
import java.sql.SQLException;
import java.util.Scanner;

public class Application {
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
    private void menu(Scanner terminal) throws SQLException {
        int option = -1;
        while (option != 6) {
            System.out.println("\nEscolha o que você deseja fazer:");
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
        ProdutoService produtoService = new ProdutoService(CriaConexao.getConexao());
        ClienteService clienteService = new ClienteService(CriaConexao.getConexao());
        ItemPedidoService itemPedidoService = new ItemPedidoService(CriaConexao.getConexao());
        PedidoService pedidoService = new PedidoService(CriaConexao.getConexao());

        switch (option) {
            case 1:
                produtoService.receiverProdutcInfo(terminal);
                break;
            case 2:
                clienteService.createClient(terminal);
                break;
            case 3:
                pedidoService.criarPedidoV2(terminal);
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
    private void cancelAlteracoes(Scanner terminal) throws SQLException {
        System.out.println("--------------------------");
        System.out.print("Você deseja cancelar todas as alterações? (s/n): ");
        String resposta = terminal.nextLine();

        if (resposta.equalsIgnoreCase("s")) {
            System.out.println("Todas as alterações foram canceladas.");
        } else {
            System.out.println("Nenhuma alteração foi cancelada.");
        }
    }
}