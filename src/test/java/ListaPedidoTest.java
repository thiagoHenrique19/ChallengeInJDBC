import org.DesafioJDBC.cliente.model.PedidoService;
import org.DesafioJDBC.produto.model.Pedido;
import org.DesafioJDBC.produto.view.CriaConexao;

public class ListaPedidoTest {
    public static void main(String[] args) {
      try{
          PedidoService pedidosService = new PedidoService(CriaConexao.getConexao());

          for (Pedido pedido : pedidosService.getPedidos()){
              System.out.println(pedido);
          }
      }catch (Exception e){
         e.printStackTrace();
      }
    }
}
