package br.com.ews.poc.jsonschema.networknt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import lombok.SneakyThrows;

import java.util.Set;

public class JsonSchemaValidator {

    private static final ObjectMapper mapper = new ObjectMapper();

    @SneakyThrows
    private static JsonNode toJsonNodeFromString(String s) {
        return mapper.readTree(s);
    }

    @SneakyThrows
    private static JsonSchema toJsonSchemaFromString(JsonNode jsonNode) {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
        return factory.getSchema(jsonNode);
    }

    public static Set<ValidationMessage> validate(String schema, String payload) {
        final JsonNode schemaNode = toJsonNodeFromString(schema);

        final JsonSchema jsonSchema = toJsonSchemaFromString(schemaNode);
        jsonSchema.initializeValidators();

        final JsonNode node = toJsonNodeFromString(payload);
        return jsonSchema.validate(node);
    }

}
