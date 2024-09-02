package org.DesafioJDBC.cliente.model;

import org.DesafioJDBC.produto.model.Cliente;
import org.DesafioJDBC.produto.model.ItemPedido;
import org.DesafioJDBC.produto.model.Pedido;
import org.DesafioJDBC.cliente.pedido.view.ListarPedido;
import org.DesafioJDBC.produto.model.Produto;
import org.DesafioJDBC.produto.view.CriaConexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PedidoService implements ListarPedido {
    private Connection connection;

    public PedidoService(Connection connection) {
        this.connection = connection;
    }

    public int createPedido(Pedido pedidoParaInserir) throws SQLException {
        try {
            connection.setAutoCommit(false);

            String sqlPedido = "INSERT INTO Pedido ( idCliente ) VALUES (?)";

            try (PreparedStatement stmtPedido = connection.prepareStatement(sqlPedido)) {
                stmtPedido.setInt(1, pedidoParaInserir.getCliente().getId());
                stmtPedido.executeUpdate();
            }
            connection.commit();

            return buscarUltimoIdPedido();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return 0;
    }

    @Override
    public List<Pedido> getPedidos() {
        List<Pedido> pedidos = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM pedido");
            while (rs.next()) {
                int idPedido = rs.getInt("IdPedido");
                int idCliente = rs.getInt("idCliente");

                //buscar os clientes e os itens do pedido
                Cliente cliente = buscarClientePorId(idCliente);
                List<ItemPedido> itens = buscarItensPedidosPorIdPedido(idPedido);

                Pedido pedido = Pedido
                        .builder()
                        .cliente(cliente)
                        .itensPedidos(itens)
                        .build();

                pedidos.add(pedido);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pedidos;
    }

    private Cliente buscarClientePorId(int idCliente) throws SQLException {

        String sql = "SELECT idCliente, nome, cpf FROM Cliente WHERE idCliente = ?";

        Cliente cliente = null;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, idCliente);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {

                    String nome = rs.getString("nome");
                    String cpf = rs.getString("cpf");

                    cliente = new Cliente(idCliente, nome, cpf);
                }
            }
        } catch (SQLException e) {

            System.out.println("Erro ao buscar o cliente por ID: " + e.getMessage());
            throw e;
        }
        return cliente;
    }

    public List<ItemPedido> buscarItensPedidosPorIdPedido(int idPedido) throws SQLException {

        String sql = "SELECT * FROM ItemPedido WHERE idPedido = ?";

        List<ItemPedido> itensPedidos = new ArrayList<>();
        ProdutoService produtoService = new ProdutoService(CriaConexao.getConexao());

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, idPedido);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {

                    int idItemPedido = rs.getInt("idItemPedido");
                    int idProduto = rs.getInt("idProduto");
                    int quantidade = rs.getInt("quantidade");

                    Produto produto = produtoService.buscarProdutoPorId(idProduto);
                    ItemPedido item = new ItemPedido();
                    item.setId(idItemPedido);
                    item.setProduto(produto);
                    item.setQuantidade(quantidade);

                    itensPedidos.add(item);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar os itens do pedido por ID: " + e.getMessage());
            throw e;
        }
        return itensPedidos;
    }

    public Pedido getPedidoPorId(int idPedido) throws SQLException {
        String sql = "SELECT * FROM Pedido p WHERE p.idPedido = ?";

        Pedido pedido = new Pedido();
        ItemPedidoService itemPedidoService = new ItemPedidoService(CriaConexao.getConexao());
        ClienteService clienteService = new ClienteService(CriaConexao.getConexao());


        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idPedido);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {

                    int idCliente = rs.getInt("idCliente");
                    Cliente cliente = clienteService.getClienteById(idCliente);

                    pedido.setId(idPedido);
                    pedido.setCliente(cliente);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar o pedido por ID: " + e.getMessage());
            throw e;
        }
        return pedido;
    }

    public int buscarIdPedidoPorCliente(int idCliente) throws SQLException {
        PedidoService pedidoService = new PedidoService(CriaConexao.getConexao());
        String sql = "SELECT idPedido FROM Pedido WHERE idCliente = ?";

        int idPedido = 0;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    idPedido = rs.getInt("idPedido");
                    System.out.println("ID do Pedido encontrado: " + idPedido);
                } else {
                    System.out.println("Nenhum pedido encontrado para o cliente com ID: " + idCliente);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar o ID do pedido: " + e.getMessage());
            throw e;
        }
        return idPedido;
    }

    public int buscarUltimoIdPedido() throws SQLException {

        String sql = "SELECT max(idPedido) FROM pedido";
        ItemPedidoService itemPedidoService = new ItemPedidoService(CriaConexao.getConexao());

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int idPedido = rs.getInt(1);
                Pedido pedidoById = getPedidoPorId(idPedido);
                List<ItemPedido> itens = itemPedidoService.getItemPedidoByIdPedido(idPedido);
                pedidoById.setItensPedidos(itens);

                System.out.println("Dados do ultimo Pedido encontrado: " + pedidoById);
                return idPedido;
            }
            System.out.println("Nenhum pedido encontrado na tabela.");
        } catch (SQLException e) {
            System.out.println("Erro ao buscar o último ID do pedido: " + e.getMessage());
            throw e;
        }
        return 0;
    }

    public static void criarPedidoV2(Scanner terminal) throws SQLException {
        // Inicializando serviços de conexão
        Connection conection = CriaConexao.getConexao();
        PedidoService pedidoService = new PedidoService(conection);
        ItemPedidoService itemPedidoService = new ItemPedidoService(conection);
        ProdutoService produtoService = new ProdutoService(conection); // Serviço para buscar produtos
        ClienteService clienteService = new ClienteService(conection);

        // Solicitar o id do cliente
        System.out.println("Informe o id do cliente que deseja fazer um pedido:");
        int idCliente = terminal.nextInt();
        Cliente cliente = clienteService.getClienteById(idCliente);
        terminal.nextLine();  // Consumir a nova linha

        Pedido newPedido = Pedido
                .builder()
                .cliente(cliente)
                .build();

        // Inserir o novo pedido no banco
        pedidoService.createPedido(newPedido);
        System.out.println("Pedido criado com sucesso!");

        // Listar todos os produtos disponíveis para escolha
        System.out.println("Produtos disponíveis:");
        List<Produto> produtos = produtoService.getAllProdutos(); // Método para obter todos os produtos
        for (Produto produto : produtos) {
            System.out.printf("ID: %d - Nome: %s - Preço: %.2f%n", produto.getId(), produto.getNome(), produto.getValor());
        }
        // Adicionar itens ao pedido
        List<ItemPedido> itensPedidos = new ArrayList<>();
        while (true) {
            System.out.println("Informe o ID do produto que deseja adicionar ou digite 0 para finalizar:");
            int idProduto = terminal.nextInt();
            if (idProduto == 0) {
                break;
            }
            Produto produtoSelecionado = produtoService.buscarProdutoPorId(idProduto);
            if (produtoSelecionado == null) {
                System.out.println("Produto não encontrado. Tente novamente.");
                continue;
            }
            System.out.println("Informe a quantidade do produto:");
            int quantidade = terminal.nextInt();

            ItemPedido itemPedido = ItemPedido
                    .builder()
                    .produto(produtoSelecionado)
                    .quantidade(quantidade)
                    .build();

            itensPedidos.add(itemPedido);
            System.out.println("Item adicionado com sucesso!");
        }
        float total = 0;
        for (ItemPedido itemPedido : itensPedidos) {
            Produto produto = produtoService.buscarProdutoPorId(itemPedido.getProduto().getId());
            total += produto.getValor() * itemPedido.getQuantidade();
        }
        newPedido.setItensPedidos(itensPedidos);
        newPedido.setTotal(total);

        itemPedidoService.inserirItensPedido(itensPedidos);

        System.out.println("Pedido finalizado com sucesso! Total: " + total);
    }

    public List<Pedido> listarTodosPedidos() throws SQLException {
        String sql = "SELECT * FROM Pedido";
        List<Pedido> pedidos = new ArrayList<>();

        ClienteService clienteService = new ClienteService(CriaConexao.getConexao());
        PedidoService pedidoService = new PedidoService(CriaConexao.getConexao());

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int idPedido = rs.getInt("idPedido");
                int idCliente = rs.getInt("idCliente");

                Cliente cliente = clienteService.getClienteById(idCliente);
                Pedido pedidoo = pedidoService.getPedidoPorId(idPedido);

                // Corrigido para definir corretamente os atributos do pedido
                Pedido pedido = Pedido
                        .builder()
                        .id(idPedido) // Define o ID do pedido
                        .cliente(cliente) // Define o ID do cliente
                        .build();

                pedidos.add(pedido);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar pedidos: " + e.getMessage());
            throw e;
        }
        return pedidos;
    }

    public void listPedido(Scanner terminal) throws SQLException {
        List<Pedido> pedidos = listarTodosPedidos();

        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido foi encontrado.");
        } else {
            System.out.println("Lista de todos os pedidos:");
            for (Pedido pedido : pedidos) {
                // Corrigido para exibir o ID do pedido e o ID do cliente corretamente
                System.out.printf("\n ID Pedido: %d | ID Cliente: %d | Nome %s ",
                        pedido.getId(), pedido.getCliente().getId(), pedido.getCliente().getNome());
            }
        }
    }
    public void viewPedido(Scanner terminal) throws SQLException {
        // SQL para buscar o pedido pelo ID
        String sqlPedido = "SELECT * FROM Pedido WHERE idPedido = ?";

        String sqlItens = "SELECT idItemPedido FROM ItemPedido WHERE idPedido = ? ";

        ClienteService clienteService = new ClienteService(CriaConexao.getConexao());
        ItemPedidoService itemPedidoService = new ItemPedidoService(CriaConexao.getConexao());
        PedidoService pedidoService = new PedidoService(CriaConexao.getConexao());

        int ultimoID = pedidoService.buscarUltimoIdPedido();

        try (PreparedStatement stmtPedido = connection.prepareStatement(sqlPedido);
             PreparedStatement stmtItens = connection.prepareStatement(sqlItens)) {

            stmtPedido.setInt(1, ultimoID);

            try (ResultSet rsPedido = stmtPedido.executeQuery()) {
                if (rsPedido.next()) {

                    int idPedido = rsPedido.getInt("idPedido");
                    int idCliente = rsPedido.getInt("idCliente");

                    Cliente cliente = clienteService.getClienteById(idCliente);

                    stmtItens.setInt(1, idPedido);

                    int idItemPedido = 0;

                    try (ResultSet rsItens = stmtItens.executeQuery()) {
                        if (rsItens.next()) {
                            // Extrai o último ID do ItemPedido
                            idItemPedido = rsItens.getInt("idItemPedido");
                        }
                    }

                    List<ItemPedido> itemPedido = itemPedidoService.getItemPedidoById(idItemPedido);

                    System.out.println("=====================================");
                    System.out.println("Detalhes do Pedido:");
                    System.out.printf("ID Pedido: %d%n", idPedido);
                    System.out.printf("ID Cliente: %d%n", cliente.getId());
                    System.out.printf("Nome Cliente: %s%n", cliente.getNome());
                    System.out.printf("Último ID Item Pedido: %d%n", idItemPedido);
                    for (ItemPedido pedido : itemPedido) {
                        System.out.printf("Detalhes do Item: Produto ID: %d, Quantidade: %d%n",
                                pedido.getId(), pedido.getQuantidade());
                    }

                    System.out.println("=====================================");

                } else {
                    System.out.println("Pedido não encontrado com o ID: " + ultimoID);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar os dados do pedido: " + e.getMessage());
            throw e;
        }
    }
}