package org.DesafioJDBC.produto.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper=false)
public class ItemPedido {
    int id;
    Produto produto;
    int quantidade;
    int preco;

    @Override
    public String toString() {
        return "\n ID Produto: "+ produto.getId()  + "\n Nome: "+ produto.getNome() + "\n Valor " + produto.getValor() + "\n Quantidade: " + quantidade;
    }
}
