/*
 * Copyright (c) 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.networknt.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.networknt.schema.SpecVersion.VersionFlag;

/**
 * UnevaluatedPropertiesValidatorTest.
 */
public class UnevaluatedPropertiesValidatorTest {
    /**
     * Issue 962.
     */
    @Test
    void annotationsOnlyDroppedAtTheEndOfSchemaProcessing() {
        String schemaData = "{\r\n"
                + "  \"type\": \"object\",\r\n"
                + "  \"required\": [\r\n"
                + "    \"key1\"\r\n"
                + "  ],\r\n"
                + "  \"properties\": {\r\n"
                + "    \"key1\": {\r\n"
                + "      \"type\": \"string\"\r\n"
                + "    },\r\n"
                + "    \"key2\": {\r\n"
                + "      \"type\": \"string\"\r\n"
                + "    },\r\n"
                + "    \"key3\": {\r\n"
                + "      \"type\": \"string\"\r\n"
                + "    }\r\n"
                + "  },\r\n"
                + "  \"unevaluatedProperties\": false\r\n"
                + "}";
        String inputData = "{\r\n"
                + "    \"key2\": \"value2\",\r\n"
                + "    \"key3\": \"value3\",\r\n"
                + "    \"key4\": \"value4\"\r\n"
                + "}";
        JsonSchema schema = JsonSchemaFactory.getInstance(VersionFlag.V202012).getSchema(schemaData);
        Set<ValidationMessage> messages = schema.validate(inputData, InputFormat.JSON);
        assertEquals(2, messages.size());
        List<ValidationMessage> assertions = messages.stream().collect(Collectors.toList());
        assertEquals("required", assertions.get(0).getType());
        assertEquals("key1", assertions.get(0).getProperty());
        assertEquals("unevaluatedProperties", assertions.get(1).getType());
        assertEquals("key4", assertions.get(1).getProperty());
    }
}