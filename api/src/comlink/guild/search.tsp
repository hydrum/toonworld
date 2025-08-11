import "@typespec/http";
import "@typespec/openapi";
import "./models.tsp";
import "../common.tsp";

using TypeSpec.Http;
using TypeSpec.OpenAPI;

namespace Comlink;

model FindGuildRequestPayload {
  name: string;
  count?: int32;
  @doc("byName = 4, byCriteria = 5") filterType: int32;
  searchCriteria?: FindGuildRequestCriteria;
}

model FindGuildRequestCriteria {
  minMemberCount: int32;
  maxMemberCount: int32;
  minGuildGalacticPower: int32;
  maxGuildGalacticPower: int32;
  recentTbParticipatedIn: string[];
  includeInviteOnly: boolean;
}

@tag("Guild")
@route("/getGuilds")
@post
@operationId("findGuild")
op findGuild(
  @body request: ComlinkRequest<FindGuildRequestPayload>,
): FindGuildResult[];

model FindGuildResult {
  guilds: GuildProfile[];
}
