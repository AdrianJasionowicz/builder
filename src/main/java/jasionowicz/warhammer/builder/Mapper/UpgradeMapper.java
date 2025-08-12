package jasionowicz.warhammer.builder.Mapper;

import jasionowicz.warhammer.builder.Upgrade.UpgradeDTO;
import jasionowicz.warhammer.builder.Upgrade.Upgrade;
import jasionowicz.warhammer.builder.Upgrade.Upgrade;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UpgradeMapper {


    UpgradeDTO unitToUnitDTO(Upgrade upgrade);
    Upgrade dtoToUpgrade(UpgradeDTO upgradeDTO);
}
