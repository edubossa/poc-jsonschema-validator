package br.com.ews.poc.jsonschema.networknt;

import com.networknt.schema.ValidationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
public class NetworkntCommandLineRunner implements CommandLineRunner {

    private static final String SCHEMA = "{ \"$id\": \"https://example.com/person.schema.json\", \"$schema\": \"https://json-schema.org/draft-04/schema#\", \"title\": \"Person\", \"type\": \"object\", \"properties\": { \"firstName\": { \"type\": \"string\", \"description\": \"The person's first name.\" }, \"lastName\": { \"type\": \"string\", \"description\": \"The person's last name.\" }, \"age\": { \"description\": \"Age in years which must be equal to or greater than zero.\", \"type\": \"integer\", \"minimum\": 18 } } }";
    private static final String PAYLOAD = "{ \"firstName\": \"John\", \"lastName\": \"Doe\", \"age\": 17 }";

    @Override
    public void run(String... args) throws Exception {
        log.info("NetworkntCommandLineRunner.run");
        Set<ValidationMessage> errors = JsonSchemaValidator.validate(SCHEMA, PAYLOAD);
        errors.forEach(error -> {
            log.error(error.getMessage());
        });
    }

}
