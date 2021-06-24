
package cz.habarta.typescript.generator;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("unused")
public class OptionalTest {

    @Test
    public void test() {
        final String output = new TypeScriptGenerator(TestUtils.settings()).generateTypeScript(Input.from(Person.class));
        Assert.assertEquals(
                "interface Person {\n" +
                "    age?: number;\n" +
                "    email?: string;\n" +
                "    name: string;\n" +
                "}",
                output.trim());
    }

    @Test
    public void testJackson2OptionalSupport() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new Jdk8Module())
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);

        final Person personWithEmail = new Person("afh", Optional.of("af@h.cz"));
        final Person personWithEmptyEmail = new Person("afh", Optional.<String>empty());
        final Person personWithoutEmail = new Person("afh", null);

        final String jsonWithEmail = "{'name':'afh','email':'af@h.cz'}".replace('\'', '\"');
        final String jsonWithNullEmail = "{'name':'afh','email':null}".replace('\'', '\"');
        final String jsonWithoutEmail = "{'name':'afh'}".replace('\'', '\"');

        Assert.assertEquals(jsonWithEmail, objectMapper.writeValueAsString(personWithEmail));
        Assert.assertEquals(jsonWithNullEmail, objectMapper.writeValueAsString(personWithEmptyEmail));
        Assert.assertEquals(jsonWithoutEmail, objectMapper.writeValueAsString(personWithoutEmail));

        Assert.assertEquals(personWithEmail, objectMapper.readValue(jsonWithEmail, Person.class));
        Assert.assertEquals(personWithEmptyEmail, objectMapper.readValue(jsonWithNullEmail, Person.class));
        Assert.assertEquals(personWithoutEmail, objectMapper.readValue(jsonWithoutEmail, Person.class));
    }

    @Test
    public void testDeclarationQuestionMark() {
        testDeclaration(OptionalPropertiesDeclaration.questionMark,
                "interface Person {\n" +
                "    age?: number;\n" +
                "    email?: string;\n" +
                "    name: string;\n" +
                "}"
        );
    }

    @Test
    public void testDeclarationNullableType() {
        testDeclaration(OptionalPropertiesDeclaration.nullableType,
                "interface Person {\n" +
                "    age: number | null;\n" +
                "    email: string | null;\n" +
                "    name: string;\n" +
                "}"
        );
    }

    @Test
    public void testDeclarationQuestionMarkAndNullableType() {
        testDeclaration(OptionalPropertiesDeclaration.questionMarkAndNullableType,
                "interface Person {\n" +
                "    age?: number | null;\n" +
                "    email?: string | null;\n" +
                "    name: string;\n" +
                "}"
        );
    }

    @Test
    public void testDeclarationNullableAndUndefinableType() {
        testDeclaration(OptionalPropertiesDeclaration.nullableAndUndefinableType,
                "interface Person {\n" +
                        "    age: number | null | undefined;\n" +
                        "    email: string | null | undefined;\n" +
                        "    name: string;\n" +
                        "}"
        );
    }

    @Test
    public void testDeclarationUndefinableType() {
        testDeclaration(OptionalPropertiesDeclaration.undefinableType,
                "interface Person {\n" +
                        "    age: number | undefined;\n" +
                        "    email: string | undefined;\n" +
                        "    name: string;\n" +
                        "}"
        );
    }

    private static void testDeclaration(OptionalPropertiesDeclaration declaration, String expected) {
        final Settings settings = TestUtils.settings();
        settings.optionalPropertiesDeclaration = declaration;
        final String output = new TypeScriptGenerator(settings).generateTypeScript(Input.from(Person.class));
        Assert.assertEquals(expected.trim(), output.trim());
    }

    private static class Person {
        public String name;
        public Optional<String> email;
        public OptionalInt age;

        public Person() {
        }

        public Person(String name, Optional<String> email) {
            this.name = name;
            this.email = email;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 53 * hash + Objects.hashCode(this.name);
            hash = 53 * hash + Objects.hashCode(this.email);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Person other = (Person) obj;
            if (!Objects.equals(this.name, other.name)) {
                return false;
            }
            if (!Objects.equals(this.email, other.email)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "Person{" + "name=" + name + ", email=" + email + '}';
        }
    }

}
