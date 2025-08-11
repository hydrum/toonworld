import "@typespec/http";
import "@typespec/rest";
import "@typespec/openapi";

import "./units";
import "./abilities";

using TypeSpec.Http;
using TypeSpec.OpenAPI;

@useAuth(ApiKeyAuth<ApiKeyLocation.header, "x-gg-bot-access">)
@service
@info(#{ title: "Swgoh.gg API", version: "1.0.0" })
namespace Swgohgg;
