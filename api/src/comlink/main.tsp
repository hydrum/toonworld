import "@typespec/http";
import "@typespec/rest";
import "@typespec/openapi";

import "./player";
import "./guild";

using TypeSpec.Http;
using TypeSpec.OpenAPI;

@service
@info(#{ title: "Comlink API", version: "2.0.0" })
namespace Comlink;
