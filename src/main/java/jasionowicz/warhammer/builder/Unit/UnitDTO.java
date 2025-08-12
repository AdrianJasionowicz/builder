package jasionowicz.warhammer.builder.Unit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UnitDTO {

    private Integer id;
    private String name;
    private String unitType;
    private String nation;
    private double pointsCostPerUnit;

}
