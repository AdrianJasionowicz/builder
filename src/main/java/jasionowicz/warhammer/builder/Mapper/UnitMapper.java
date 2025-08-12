package jasionowicz.warhammer.builder.Mapper;


import jasionowicz.warhammer.builder.Unit.Unit;
import jasionowicz.warhammer.builder.Unit.UnitDTO;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface UnitMapper {
    UnitDTO unitToUnitDTO(Unit unit);
    Unit dtoToUnit(UnitDTO unitDTO);
}
