import "@typespec/http";
import "@typespec/openapi";

using TypeSpec.Http;
using TypeSpec.OpenAPI;

model Metadata {
    latestLocalizationBundleVersion: string;
    latestGamedataVersion: string;
}

@tag("Metadata")
@route("/metadata")
@post @operationId("getMetadata") op retrieveMetadata(): Metadata;