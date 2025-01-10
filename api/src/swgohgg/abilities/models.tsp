namespace Swgohgg;

model Ability {
    @encodedName("application/json", "base_id") baseId: string;
    @encodedName("application/json", "ability_id") abilityId: string;
    name: string;
    image: string;
    url: string;
    @encodedName("application/json", "tier_max") tierMax: int32;
    @encodedName("application/json", "is_zeta") isZeta: boolean;
    @encodedName("application/json", "is_omega") isOmega: boolean;
    @encodedName("application/json", "is_omicron") isOmicron: boolean;
    @encodedName("application/json", "is_ultimate") isUltimate: boolean;
    description: string;
    @encodedName("application/json", "combat_type") combatType: int32;
    @encodedName("application/json", "omicron_mode") omicronMode: int32;
    type: int32;
    @encodedName("application/json", "character_base_id") characterBaseId?: string;
    @encodedName("application/json", "ship_base_id") shipBaseId?: string;
    @encodedName("application/json", "omicron_battle_types") omicronBattleTypes: string[];
}
