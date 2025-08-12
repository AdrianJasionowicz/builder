package jasionowicz.warhammer.builder.Mapper;

import jasionowicz.warhammer.builder.SelectedUpgrade.SelectedUpgrade;
import jasionowicz.warhammer.builder.SelectedUpgrade.SelectedUpgradeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SelectedUpgradeMapper {

    @Mapping(target = "pointsCost", source = "upgrade.pointsCost")
    @Mapping(target = "name",       source = "upgrade.name")
    SelectedUpgradeDTO toDto(SelectedUpgrade entity);

    List<SelectedUpgradeDTO> toDtoList(List<SelectedUpgrade> entities);
}