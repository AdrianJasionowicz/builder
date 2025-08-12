package jasionowicz.warhammer.builder.Unit;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jasionowicz.warhammer.builder.UnitStats.UnitStats;
import jasionowicz.warhammer.builder.Upgrade.Upgrade;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private double pointsCostPerUnit;
    private double minQuantity;
    private String unitType;
    private String nation;
    @OneToOne
    @JoinColumn(name = "unitStats_id")
    private UnitStats unitStats;
    @OneToMany(mappedBy = "unit", fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Upgrade> upgradesList = new ArrayList<>();

}