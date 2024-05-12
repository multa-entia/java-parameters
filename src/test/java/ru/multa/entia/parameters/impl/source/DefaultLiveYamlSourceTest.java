package ru.multa.entia.parameters.impl.source;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DefaultLiveYamlSourceTest {

    @Test
    void shouldCheckGetting_ifReaderNull() {
        
    }

    @Test
    void shouldCheckGetting_ifReaderReturnFail() {
        
    }

    @Test
    void shouldCheckGetting_ifSyntaxError() {
        
    }

    @Test
    void shouldCheckGetting() {

    }

    // TODO: del
    @Test
    void test() {

        String source =
"""
int_value: 123
float_value: 123.56
boolean_value: true
list_value_0: [val0, val1, val2]
list_value_1:
  - val3
  - val4
  - val5
str_value_0: hello
str_value_1: "world"
str_value_2: |
   Hello
   world
   !!!
object_values_0:
  dan:
    name: Dan
    age: 21
  dora:
    name: Dora
    age: 22
#bad_int: 123float_v: 123.78
    name: Dora
    age: 221
joe:
  name: J
  age: 100
list_value_3: val0, val1, val2
list_value_11:
  val3
  val4
  val5
""";

        Yaml yaml = new Yaml();
        Map<String, Object> object = yaml.load(source);
        for (Map.Entry<String, Object> entry : object.entrySet()) {
            System.out.printf("%s : %s (%s)\n", entry.getKey(), entry.getValue(), entry.getValue().getClass());
        }
    }
}