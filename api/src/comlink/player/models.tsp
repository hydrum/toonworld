namespace Comlink;

model Player {
  rosterUnit: RosterUnit[];
  playerRating: PlayerRating;
  profileStat: PlayerProfileStat[];
  pvpProfile: PlayerPvpProfile[];
  unlockedPlayerTitle: IdReference[];
  unlockedPlayerPortrait: IdReference[];
  seasonStatus: unknown[];
  datacron: unknown[];
  selectedPlayerTitle?: IdReference;
  selectedPlayerPortrait?: IdReference;
  name: string;
  level: int32;
  allyCode: string;
  playerId: string;
  guildId: string;
  guildName: string;
  guildLogoBackground: string;
  guildBannerColor: string;
  guildBannerLogo: string;
  guildTypeId: string;
  localTimeZoneOffsetMinutes: int32;
  lastActivityTime: string;
  lifetimeSeasonScore: string;
  nucleusId: unknown;
}

model IdReference {
  id: string;
}

model PlayerRating {
  playerSkillRating?: PlayerSkillRating;
  playerRankStatus?: PlayerRankStatus;
}

model PlayerSkillRating {
  skillRating: int32;
}

model PlayerRankStatus {
  leagueId: string;
  divisionId: int32;
}

model PlayerProfileStat {
  nameKey: string;
  versionKey: string;
  value: string;
  index: int32;
}

model RosterUnit {
  skill: UnitSkill[];
  equipment: UnitGear[];
  equippedStatModOld: unknown[];
  equippedStatMod: Mod[];
  purchasedAbilityId: string[];
  id: string;
  definitionId: string;
  currentRarity: int32;
  currentLevel: int32;
  currentXp: int32;
  promotionRecipeReference: string;
  unitStat?: unknown;
  currentTier: int32;
  relic?: RelicTier;
}

model UnitSkill {
  id: string;
  tier: int32;
}

model RelicTier {
  currentTier: int32;
}

model UnitGear {
  equipmentId: string;
  slot: int32;
}

model Mod {
  id: string;
  definitionId: string;
  level: int32;
  tier: int32;
  sellValue: ModCost;
  removeCost: ModCost;
  locked: boolean;
  xp: int32;
  levelCost: ModCost;
  bonusQuantity: int32;
  convertedItem?: unknown;
  rerolledCount: int32;
  primaryStat: ModStat;
  secondaryStat: ModStat[];
}

model ModStat {
  roll: string[];
  unscaledRollValue: string[];
  stat: ModStatValue;
  statRolls: int32;
  statRollerBoundsMin: int32;
  statRollerBoundsMax: int32;
}

model ModStatValue {
  unitStatId: int32;
  statValueDecimal: string;
  unscaledDecimalValue: string;
  uiDisplayOverrideValue: string;
  @encodedName("application/json", "scalar") scalarValue: string;
}

model ModCost {
  currency: int32;
  quantity: string;
  bonusQuantity: int32;
}

model PlayerPvpProfile {
  tab: int32;
  rank: int32;
  squad: unknown;
  eventId: string;
}
