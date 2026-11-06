package br.com.financiamento.shared.web;

import io.quarkus.smallrye.openapi.OpenApiFilter;
import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.media.Schema;

import java.util.List;
import java.util.Map;

/**
 * Completa o contrato OpenAPI com o formato único de erro (ADR 0011, ADR 0012): registra o esquema e acrescenta a
 * toda operação as respostas 400, 401, 403, 404 e 422, sem precisar anotar endpoint por endpoint.
 */
@OpenApiFilter(OpenApiFilter.RunStage.BUILD)
public class RespostasDeErroNoContrato implements OASFilter {

    static final String ESQUEMA = "Erro";

    static final Map<String, String> RESPOSTAS = Map.of(
            "400", "Atributo fora de forma ou formato (códigos ATR-): corrija os campos indicados em falhas[].campo.",
            "401", "Sem token, token inválido, expirado ou de outro emissor (SEG-0001).",
            "403", "O papel não permite, ou você não participa da proposta ou do contrato (SEG-0002 a SEG-0004).",
            "404", "O que se pede não existe (código do catálogo do módulo).",
            "422", "A regra de negócio não deixa: veja falhas[].codigo e falhas[].regra (regra do modelo).");

    @Override
    public void filterOpenAPI(OpenAPI openAPI) {
        if (openAPI.getComponents() == null) openAPI.setComponents(OASFactory.createComponents());
        openAPI.getComponents().addSchema(ESQUEMA, esquemaDoErro());
        if (openAPI.getPaths() == null || openAPI.getPaths().getPathItems() == null) return;
        openAPI.getPaths().getPathItems().values().forEach(item -> item.getOperations().values().forEach(operacao -> {
            if (operacao.getResponses() == null) operacao.setResponses(OASFactory.createAPIResponses());
            RESPOSTAS.forEach((status, descricao) -> {
                var existente = operacao.getResponses().getAPIResponse(status);
                // Respostas do próprio endpoint com corpo prevalecem; as automáticas sem corpo (401, 403) são completadas.
                if (existente != null && existente.getContent() != null) return;
                operacao.getResponses().addAPIResponse(status, OASFactory.createAPIResponse().description(descricao)
                        .content(OASFactory.createContent().addMediaType("application/json",
                                OASFactory.createMediaType().schema(OASFactory.createSchema().ref(ESQUEMA)))));
            });
        }));
    }

    private static Schema esquemaDoErro() {
        var texto = OASFactory.createSchema().addType(Schema.SchemaType.STRING);
        var falha = OASFactory.createSchema().addType(Schema.SchemaType.OBJECT)
                .description("Uma falha, com o código do catálogo de erros (docs/api/catalogo-de-erros.md).")
                .addProperty("codigo", texto.description("Código estável do catálogo, ex.: ORI-0002, ATR-0001."))
                .addProperty("codigoCorporativo", OASFactory.createSchema().addType(Schema.SchemaType.STRING)
                        .description("Código no catálogo corporativo, quando mapeado."))
                .addProperty("campo", OASFactory.createSchema().addType(Schema.SchemaType.STRING)
                        .description("O campo com problema, nas falhas de atributo (ex.: participantes[0].nome)."))
                .addProperty("regra", OASFactory.createSchema().addType(Schema.SchemaType.STRING)
                        .description("A regra do modelo de negócio (ex.: RN-0009, CV-0001), quando há."))
                .addProperty("mensagem", OASFactory.createSchema().addType(Schema.SchemaType.STRING)
                        .description("Mensagem em linguagem de negócio."));
        return OASFactory.createSchema().addType(Schema.SchemaType.OBJECT)
                .description("Formato único de erro (ADR 0011).")
                .addProperty("tipo", OASFactory.createSchema().addType(Schema.SchemaType.STRING)
                        .enumeration(List.of("ATRIBUTO", "DOMINIO")))
                .addProperty("falhas", OASFactory.createSchema().addType(Schema.SchemaType.ARRAY).items(falha));
    }
}
