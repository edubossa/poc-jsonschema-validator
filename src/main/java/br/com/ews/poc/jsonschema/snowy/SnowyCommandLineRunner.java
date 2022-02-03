package br.com.ews.poc.jsonschema.snowy;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.qindesign.json.schema.Error;
import com.qindesign.json.schema.*;
import com.qindesign.json.schema.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.StringReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class SnowyCommandLineRunner implements CommandLineRunner {

    private static final String SCHEMA = "{ \"id\": \"https://example.com/person.schema.json\", \"schema\": \"https://json-schema.org/draft-06/schema\", \"title\": \"Person\", \"type\": \"object\", \"properties\": { \"firstName\": { \"type\": \"string\", \"description\": \"The person's first name.\" }, \"lastName\": { \"type\": \"string\", \"description\": \"The person's last name.\" }, \"age\": { \"description\": \"Age in years which must be equal to or greater than zero.\", \"type\": \"integer\", \"minimum\": 18 } } }";
    private static final String PAYLOAD = "{ \"firstName\": \"John\", \"lastName\": \"Doe\", \"age\": 17 }";

    private static final Specification spec = Specification.DRAFT_2019_09;

    @Override
    public void run(String... args) throws Exception {
        URI schemaID = new URI(new File("schema.json").toURI());
        JsonElement schema = JSON.parse(new StringReader(SCHEMA));
        JsonElement instance = JSON.parse(new StringReader(PAYLOAD));

        Options opts = new Options();
        opts.set(Option.FORMAT, true);
        opts.set(Option.CONTENT, true);
        opts.set(Option.DEFAULT_SPECIFICATION, spec);

        Map<JSONPath, Map<String, Map<JSONPath, Annotation<?>>>> annotations = new HashMap<>();
        Map<JSONPath, Map<JSONPath, Error<?>>> errors = new HashMap<>();

        Validator validator = new Validator(schema, schemaID, null, null, opts);
        boolean result = validator.validate(instance, annotations, errors);

        // Basic output
        log.info("SnowyCommandLineRunner.run Basic output:");
        JSON.print(System.out, basicOutput(result, errors), "    ");
    }

    private static JsonObject basicOutput(boolean result,
                                          Map<JSONPath, Map<JSONPath, Error<?>>> errors) {
        JsonObject root = new JsonObject();
        root.addProperty("valid", result);
        JsonArray errorArr = new JsonArray();
        root.add("errors", errorArr);
        errors.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> {
                    e.getValue().values().stream()
                            .filter(err -> !err.isPruned() && !err.result)
                            .sorted(Comparator.comparing(err -> err.loc.keyword))
                            .forEach(err -> {
                                JsonObject error = new JsonObject();
                                error.addProperty("keywordLocation", err.loc.keyword.toString());
                                error.addProperty("absoluteKeywordLocation", err.loc.absKeyword.toString());
                                error.addProperty("instanceLocation", err.loc.instance.toString());

                                if (err.value != null) {
                                    error.addProperty("error", err.value.toString());
                                }
                                errorArr.add(error);
                            });
                });
        return root;
    }

}
