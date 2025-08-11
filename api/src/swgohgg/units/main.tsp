import "@typespec/http";
import "@typespec/openapi";
import "./models.tsp";

using TypeSpec.Http;
using TypeSpec.OpenAPI;

namespace Swgohgg;

model UnitResponse {
  data: Unit[];
  message?: unknown;
  @encodedName("application/json", "total_count") totalCount?: unknown;
}

@tag("Unit")
@route("/units")
@get
@operationId("getUnits")
op getUnits(): UnitResponse;
