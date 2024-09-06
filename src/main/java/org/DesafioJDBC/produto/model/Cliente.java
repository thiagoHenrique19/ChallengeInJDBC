package org.DesafioJDBC.produto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
//um padrao de projetos criacao para que consiga montar um objeto passo a passo
public class Cliente {
    private int id;
    private String nome;
    private String cpf;
}

