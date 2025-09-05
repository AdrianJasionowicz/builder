package jasionowicz.warhammer.builder.SelectedStats;

import jakarta.persistence.*;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnit;
import jasionowicz.warhammer.builder.UnitStats.UnitStats;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SelectedStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer m;
    private Integer ws;
    private Integer bs;
    private Integer s;
    private Integer t;
    private Integer w;
    private Integer i;
    private Integer a;
    private Integer ld;
    private Integer basicSave;
    private Integer wardSave;

    @OneToOne(mappedBy = "selectedStats")
    private SelectedUnit selectedUnit;

    public SelectedStats(UnitStats unitStats) {
        this.m = unitStats.getM();
        this.ws = unitStats.getWs();
        this.bs = unitStats.getBs();
        this.s = unitStats.getS();
        this.t = unitStats.getT();
        this.w = unitStats.getW();
        this.i = unitStats.getI();
        this.a = unitStats.getA();
        this.ld = unitStats.getLd();
        this.basicSave = unitStats.getBasicSave();
        this.wardSave = unitStats.getWardSave();
    }
}
