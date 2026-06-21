package br.com.Vestibuline.domain.planner;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "materia") // ou qualquer tabela existente real do seu banco
public class PlannerEntity {

    @Id
    private UUID id;
}