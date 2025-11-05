package br.unipar.projetointegrador.frotisapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Item_treino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Muitos Itens de Treino (@ManyToOne) pertencem a UM Treino.
     */
    @ManyToOne
    @JoinColumn(name = "treino_id") // Define o nome da coluna da chave estrangeira
    private Treino treino;

    /**
     * Muitos Itens de Treino (@ManyToOne) se referem a UM Exercicio.
     */
    @ManyToOne
    @JoinColumn(name = "exercicio_id") // Define o nome da coluna da chave estrangeira
    private Exercicio exercicio;

    // --- Atributos extras que justificam a criação desta classe ---
    private int series;
    private String repeticoes; // Usar String permite valores como "10-12" ou "Até a falha"
    private int carga;
    private int tempoDescansoSegundos; // Ex: 60


}
