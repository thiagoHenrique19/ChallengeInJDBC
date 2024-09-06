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
import java.util.Objects;
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

                Pedido pedido = Pedido.builder().cliente(cliente).itensPedidos(itens).build();

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
        ProdutoService produtoService = new ProdutoService(connection);

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
        ClienteService clienteService = new ClienteService(connection);


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

        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            System.out.println("Nenhum pedido encontrado na tabela.");
        } catch (SQLException e) {
            System.out.println("Erro ao buscar o último ID do pedido: " + e.getMessage());
            throw e;
        }
        return 0;
    }

    public void criarPedidoV2() throws SQLException {
        Scanner terminal = new Scanner(System.in);
        // Inicializando serviços de conexão
        PedidoService pedidoService = new PedidoService(connection);
        ItemPedidoService itemPedidoService = new ItemPedidoService(connection);
        ProdutoService produtoService = new ProdutoService(connection);
        ClienteService clienteService = new ClienteService(connection);

        clienteService.getAllClientes();

        // Solicitar o id do cliente
        System.out.println("Informe o id do cliente que deseja fazer um pedido:");
        int idCliente = terminal.nextInt();
        Cliente cliente = clienteService.getClienteById(idCliente);
        terminal.nextLine();  // Consumir a nova linha

        Pedido newPedido = Pedido.builder().cliente(cliente).build();

        int id = pedidoService.createPedido(newPedido);
        newPedido.setId(id);

        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║                 Produtos Disponíveis               ║");
        System.out.println("╠════════════════════════════════════════════════════╣");

        List<Produto> produtos = produtoService.getAllProdutos();

        for (Produto produto : produtos) {
            System.out.printf("║ ID: %02d - Nome: %-14s - Preço: R$ %-8.2f ║%n",
                    produto.getId(),
                    produto.getNome(),
                    produto.getValor());
        }

        System.out.println("╚════════════════════════════════════════════════════╝");

        List<ItemPedido> itensPedidos = new ArrayList<>();
        boolean verificarSeDesejaContinuar = true;
        while (verificarSeDesejaContinuar) {
            System.out.println("Informe o ID do produto que voce deseja para fazer o pedido:");
            int idProduto = terminal.nextInt();

            while (idProduto == 0) {
                System.out.println("O ID 0 é inválido, tente novamente");
                idProduto = terminal.nextInt();
            }

            Produto produtoSelecionado = produtoService.buscarProdutoPorId(idProduto);
            if (produtoSelecionado == null) {
                System.out.println("Produto não encontrado. Tente novamente.");
                continue;
            }
            System.out.println("Informe a quantidade do produto:");
            int quantidade = terminal.nextInt();

            while (quantidade == 0) {
                System.out.println("A quantidade é inválida, tente novamente");
                quantidade = terminal.nextInt();
            }

            ItemPedido itemPedido = ItemPedido.builder().produto(produtoSelecionado).quantidade(quantidade).build();

            itensPedidos.add(itemPedido);
            System.out.println("Item adicionado com sucesso!");
            System.out.println("Deseja adicionar mais produtos? (s/n)");
            String escolha = terminal.next();
            if (escolha.equalsIgnoreCase("n")) {
                verificarSeDesejaContinuar = false;
            } else if (!(escolha.equalsIgnoreCase("s"))) {
                System.out.println("Utilize s/n por gentileza");
            }
        }
        float total = 0;
        for (ItemPedido itemPedido : itensPedidos) {
            if (itemPedido.getProduto().getId() != 0) {
                Produto produto = produtoService.buscarProdutoPorId(itemPedido.getProduto().getId());
                total += produto.getValor() * itemPedido.getQuantidade();
            }
        }

        newPedido.setItensPedidos(itensPedidos);
        newPedido.setTotal(total);
        itemPedidoService.inserirItensPedido(itensPedidos, newPedido);
        System.out.println("Pedido finalizado com sucesso! Total: " + total);
    }

    public List<Pedido> listarTodosPedidos() throws SQLException {
        String sql = "SELECT * FROM Pedido";
        List<Pedido> pedidos = new ArrayList<>();

        ClienteService clienteService = new ClienteService(connection);

        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int idPedido = rs.getInt("idPedido");
                int idCliente = rs.getInt("idCliente");

                Cliente cliente = clienteService.getClienteById(idCliente);

                Pedido pedido = Pedido.builder().id(idPedido).cliente(cliente).build();

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
            // Exibe todos os pedidos
            String bordaSuperior = "╔════════════════════════════════════════════════════════════════╗";
            String bordaInferior = "╚════════════════════════════════════════════════════════════════╝";
            String divisoria = "╠════════════════════════════════════════════════════════════════╣";

            System.out.println(bordaSuperior);
            System.out.println("║                       Lista de Pedidos                         ║");
            for (Pedido pedido : pedidos) {

                System.out.println(divisoria);
                System.out.printf("║ ID Pedido: %-5d |ID Cliente: %-5d |Nome: %-20s║%n",
                        pedido.getId(),
                        pedido.getCliente().getId(),
                        pedido.getCliente().getNome());
            }
            System.out.print(bordaInferior);

            System.out.println("\nDeseja ver os dados de um pedido específico? (s/n)");
            String resposta = terminal.nextLine().trim().toLowerCase();

            if (resposta.equals("s")) {
                System.out.println("Informe o ID do pedido que deseja ver:");
                int idPedido = Integer.parseInt(terminal.nextLine());

                Pedido pedidoEspecifico = getPedidoPorId(idPedido);

                if (pedidoEspecifico != null) {
                    exibirDetalhesPedido(pedidoEspecifico);
                } else {
                    System.out.println("Pedido não encontrado.");
                }
            } else {
                System.out.println("o Item pedido não foi encontrado");
            }
        }
    }

    private void exibirDetalhesPedido(Pedido pedido) throws SQLException {
// Definindo bordas e divisórias com um comprimento uniforme e ajustado
        String bordaSuperior = "╔══════════════════════════════════════╗";
        String bordaInferior = "╚══════════════════════════════════════╝";
        String divisoria = "╠══════════════════════════════════════╣";
        String divisoriaInterna = "╟──────────────────────────────────────╢";

// Cabeçalho do pedido
        System.out.println(bordaSuperior);
        System.out.println("║            Detalhes do Pedido        ║");
        System.out.println(divisoria);

// Detalhes do pedido
        System.out.printf("║ ID Pedido:        %-18d ║%n", pedido.getId());

// Instancia o serviço para buscar os itens do pedido
        ItemPedidoService itemPedidoService = new ItemPedidoService(connection);
        pedido.setItensPedidos(itemPedidoService.getItemPedidoByIdPedido(pedido.getId()));

// Loop para imprimir os itens do pedido
        for (ItemPedido item : pedido.getItensPedidos()) {
            System.out.println(divisoriaInterna); // Divisória interna entre os itens
            System.out.printf("║ Nome do Produto:  %-18s ║%n", item.getProduto().getNome());
            System.out.printf("║ Valor do Produto: R$ %-15.2f ║%n", item.getProduto().getValor());
            System.out.printf("║ Quantidade:       %-18d ║%n", item.getQuantidade());
        }

// Detalhes do cliente
        System.out.println(divisoria);
        System.out.printf("║ ID Cliente:       %-18d ║%n", pedido.getCliente().getId());
        System.out.printf("║ Nome Cliente:     %-18s ║%n", pedido.getCliente().getNome());

        System.out.println(bordaInferior);
    }

    public void viewPedido(Scanner terminal) throws SQLException {
        String sqlPedido = "SELECT * FROM Pedido WHERE idPedido = ?";

        String sqlItens = "SELECT idItemPedido FROM ItemPedido WHERE idPedido = ? ";

        ClienteService clienteService = new ClienteService(connection);
        ItemPedidoService itemPedidoService = new ItemPedidoService(connection);
        PedidoService pedidoService = new PedidoService(connection);

        int ultimoID = pedidoService.buscarUltimoIdPedido();
        Pedido pedidoById = getPedidoPorId(ultimoID);
        List<ItemPedido> itens = itemPedidoService.getItemPedidoByIdPedido(ultimoID);
        pedidoById.setItensPedidos(itens);

        try (PreparedStatement stmtPedido = connection.prepareStatement(sqlPedido); PreparedStatement stmtItens = connection.prepareStatement(sqlItens)) {

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

                    List<ItemPedido> itemPedidos = itemPedidoService.getItemPedidoById(idItemPedido);
                    double total = pedidoById.calcularTotal();

                    String bordaSuperior = "╔═══════════════════════════════════╗";
                    String bordaInferior = "╚═══════════════════════════════════╝";
                    String divisoria = "╠═══════════════════════════════════╣";
                    String divisoriaInterna = "║-----------------------------------║";

                    System.out.println(bordaSuperior);
                    System.out.println("║        Detalhes do Pedido         ║");
                    System.out.println(divisoria);

                    System.out.printf("║ ID Pedido:      %-17d ║%n", idPedido);
                    System.out.printf("║ ID Cliente:     %-17d ║%n", cliente.getId());
                    System.out.printf("║ Cliente:        %-17s ║%n", cliente.getNome());
                    System.out.printf("║ ID Item Pedido: %-17d ║%n", idItemPedido);

                    System.out.println(divisoria);
                    for (ItemPedido itemPedido : itemPedidos) {
                        System.out.printf("║ Produto ID:     %-17d ║%n", itemPedido.getProduto().getId());
                        System.out.printf("║ Nome:           %-17s ║%n", itemPedido.getProduto().getNome());
                        System.out.printf("║ Quantidade:     %-17d ║%n", itemPedido.getQuantidade());
                        System.out.println(divisoriaInterna); // Divisão interna entre itens
                    }

                    System.out.printf("║ Valor Total:    %-17.2f ║%n", total);
                    System.out.println(bordaInferior);


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