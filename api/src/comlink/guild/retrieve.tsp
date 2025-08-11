import "@typespec/http";
import "@typespec/openapi";
import "./models.tsp";
import "../common.tsp";

using TypeSpec.Http;
using TypeSpec.OpenAPI;

namespace Comlink;

model RetrieveGuildPayload {
  guildId: string;
  includeRecentGuildActivityInfo: boolean;
}

@tag("Guild")
@route("/guild")
@post
@operationId("getGuild")
op retrieveGuild(
  @body request: ComlinkRequest<RetrieveGuildPayload>,
): RetrieveGuildResult;

model RetrieveGuildResult {
  guild: Guild;
}
