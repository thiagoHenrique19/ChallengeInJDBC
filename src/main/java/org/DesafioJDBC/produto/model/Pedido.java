package org.DesafioJDBC.produto.model;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Builder
public class Pedido {

    int id;
    Cliente cliente;
    List<ItemPedido> itensPedidos;
    float total;

    public double calcularTotal() {
        double valorTotal = 0;
        for (ItemPedido itensPedido : itensPedidos) {
            valorTotal += itensPedido.getProduto().getId();
        }
        return valorTotal;
    }
}

