namespace Comlink;

model GuildEvent {
    definitionId: string;
    completedStars: string;
    endTime: string;
}

model GuildMissionId {
    campaignId: string,
    campaignMapId: string,
    campaignNodeId: string,
    campaignNodeDifficulty: int32;
    campaignMissionId: string,
}

model GuildRaidConfig {
    raidId: string,
    campaignMissionIdentifier: GuildMissionId;
    autoLaunch: boolean;
    autoLaunchImmediately: boolean;
    autoLaunchTime: string;
    joinPeriodDuration: string;
    autoSimEnabled: boolean;
    immediate: boolean;
    scheduledUtcOffsetSeconds: string;
}

model GuildProfile {
    messageCriteriaKey: unknown[];
    raidLaunchConfig: unknown[];
    guildEventTracker: GuildEvent[];
    id: string;
    name: string;
    externalMessageKey: string;
    logoBackground: string;
    enrollmentStatus: int32;
    trophy: int32;
    memberCount: int32;
    memberMax: int32;
    level: int32;
    rank: int32;
    levelRequirement: int32;
    raidWin: int32;
    leaderboardScore: string;
    bannerColorId: string;
    bannerLogoId: string;
    guildGalacticPower: string;
    chatChannelId: string;
    guildType: string;
    guildGalacticPowerForRequirement: string;
    singleLaunchConfig?: GuildRaidConfig,
    autoLaunchConfig?: GuildRaidConfig
}

model Guild {
    member: GuildMember[];
    profile: GuildProfile;
    lastRaidPointsSummary: GuildLastRaidSummary[];
    inviteStatus: unknown[];
    raidStatus: unknown[];
    raidResult: unknown[];
    territoryBattleStatus: unknown[];
    guildEvents: unknown[];
    territoryBattleResult: unknown[];
    territoryWarStatus: unknown[];
    roomAvailable: unknown[];
    stat: unknown[];
    recentRaidResult: GuildRaidResult[];
    recentTerritoryWarResult: GuildWarResult[];
    inventory?: unknown;
    progress?: unknown;
    nextChallengesRefresh: string;
}

model GuildMember {
    memberContribution: GuildMemberContribution[];
    seasonStatus: GuildMemberSeasonStatus[];
    playerId: string;
    playerName: string;
    playerLevel: int32;
    memberLevel: int32;
    guildXp: int32;
    lastActivityTime: string;
    squadPower: int32;
    guildJoinTime: string;
    galacticPower: string;
    playerTitle: string;
    playerPortrait: string;
    lifetimeSeasonScore: string;
    leagueId: string;
    shipGalacticPower: string;
    characterGalacticPower: string;
    nucleusId: string;
}

model GuildMemberContribution {
    type: int32;
    currentValue: string;
    lifetimeValue: string;
}

model GuildMemberSeasonStatus {
    seasonId: string;
    eventInstanceId: string;
    league: string;
    wins: int32;
    losses: int32;
    seasonPoints: int32;
    division: int32;
    joinTime: string;
    endTime: string;
    remove: boolean;
    rank: int32;
}

model GuildLastRaidSummary {
    identifier: GuildMissionId;
    totalPoints: string;
}

model GuildRaidResult {
    raidMember: GuildRaidResultMember[];
    encounterProgress: unknown[];
    raidId: string;
    identifier: GuildMissionId;
    duration: string;
    progress?: unknown;
    endTime: string;
    outcome: int32;
    guildRewardScore: string;
    cachedRewards: unknown;
}

model GuildRaidResultMember {
    playerId: string;
    memberProgress: string;
    memberRank: int32;
    memberAttempt: int32;
}

model GuildWarResult {
    territoryWarId: string;
    score: string;
    power: int64;
    opponentScore: string;
    endTimeSeconds: string;
}