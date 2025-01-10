import "@typespec/http";
import "@typespec/rest";
import "@typespec/openapi";

import "./units";
import "./abilities";

using TypeSpec.Http;
using TypeSpec.OpenAPI;

@service
@info({
    title: "Swgoh.gg API",
    version: "1.0.0"
})
namespace Swgohgg;