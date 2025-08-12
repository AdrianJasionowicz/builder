package jasionowicz.warhammer.builder.Upgrade;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jasionowicz.warhammer.builder.Unit.Unit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Upgrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private double pointsCost;
    private String upgradeType;
    private String description;
    @ManyToOne
    @JoinColumn(name = "unit_id")
    @JsonBackReference
    private Unit unit;


}