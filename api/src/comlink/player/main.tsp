import "@typespec/http";
import "@typespec/openapi";
import "./models.tsp";
import "../common.tsp";

using TypeSpec.Http;
using TypeSpec.OpenAPI;

namespace Comlink;

model PlayerPayload {
  allyCode?: string;
  playerId?: string;
}

@tag("Player")
@route("/player")
@post
@operationId("findPlayer")
op findPlayer(@body request: ComlinkRequest<PlayerPayload>): Player;
