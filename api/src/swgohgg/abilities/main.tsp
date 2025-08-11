import "@typespec/http";
import "@typespec/openapi";
import "./models.tsp";

using TypeSpec.Http;
using TypeSpec.OpenAPI;

namespace Swgohgg;

@tag("Unit")
@route("/abilities")
@get
@operationId("getAbilities")
op getAbilities(): Ability[];
