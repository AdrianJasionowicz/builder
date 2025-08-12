package jasionowicz.warhammer.builder.SelectedStats;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SelectedStatsDTO {
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
}
