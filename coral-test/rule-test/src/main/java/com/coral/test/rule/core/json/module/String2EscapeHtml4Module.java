package com.coral.test.rule.core.json.module;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * string xss过滤
 *
 * @author huss
 */
@Slf4j
public class String2EscapeHtml4Module extends SimpleModule {

    public String2EscapeHtml4Module() {
        super(String2EscapeHtml4Module.class.getName(), PackageVersion.VERSION);
        super.addDeserializer(String.class, new String2EscapeHtml4Deserializer());
    }

    static class String2EscapeHtml4Deserializer extends JsonDeserializer<String> {

        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

            String value = p.getValueAsString();
//            log.debug(">>>>> String2EscapeHtml4Deserializer. name：{}, value：{}", p.currentName(), value);
//            if (value != null) {
//                return StringEscapeUtils.escapeHtml4(value);
//            }
            return value;

        }
    }
}
