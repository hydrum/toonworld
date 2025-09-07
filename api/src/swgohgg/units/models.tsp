namespace Swgohgg;

model Unit {
  name: string;
  @encodedName("application/json", "base_id") baseId: string;
  url: string;
  image: string;
  power: int32;
  description: string;
  @encodedName("application/json", "combat_type") combatType: int32;
  @encodedName("application/json", "gear_levels") gearLevels: GearLevel[];
  alignment: int32;
  categories: string[];
  @encodedName("application/json", "ability_classes") abilityClasses: string[];
  role: string;
  @encodedName("application/json", "ship_base_id") shipBaseId?: string;
  @encodedName("application/json", "ship_slot") shipSlot?: int32;

  @encodedName("application/json", "activate_shard_count")
  activateShardCount: int32;

  @encodedName("application/json", "is_capital_ship") isCapitalShip: boolean;

  @encodedName("application/json", "is_galactic_legend")
  isGalacticLegend: boolean;

  @encodedName("application/json", "made_available_on") madeAvialableOn: string;
  @encodedName("application/json", "crew_base_ids") crewBaseIds: string[];

  @encodedName("application/json", "omicron_ability_ids")
  omicronAbilityIds: string[];

  @encodedName("application/json", "zeta_ability_ids") zetaAbilityIds: string[];
}

model GearLevel {
  tier: int32;
  gear: string[];
}
