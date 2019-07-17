package com.acnebs.autovalidate;

import org.junit.Test;

import javax.validation.*;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;


public class DemoBuilderTest {
    @Test
    public void test_demo() {
        // DemoBean defines validation rules for its validity.
        // Let's try it.
        DemoBuilder demo = DemoBuilder.builder()  // not done yet
                .firstName("Joey")                // not done yet
                .lastName("Ramone")               // not done yet
                .email("joey@ramones.com")        // not done yet
                .build();                         // de-facto valid...
        // at this point the object is de-facto valid but this fact has not been checked yet

        Set<ConstraintViolation<DemoBuilder>> violations = validate(demo);
        assertEquals("Object should be valid.", 0, violations.size());

        // Fortunately, making it INVALID again is no possible...
        // let's "clone" it instead:
        final DemoBuilder demo2 = demo.toBuilder()
                .email("joey_at_ramones.com")
                .build();                           // de-facto INVALID...
        assertNotSame(demo, demo2);

        violations = validate(demo2);
        assertEquals("Object 2 should have 1 error because the email address is wrong.", 1, violations.size());


        violations = validate(demo);
        assertEquals("Object 1 should still be valid.", 0, violations.size());

        // This is better, however, the object's data integrity relies on the fact that some process
        // OUTSIDE of the object has taken care of validation.
        // The object itself cannot be trusted as is.
    }

    private Set<ConstraintViolation<DemoBuilder>> validate(final DemoBuilder demo) {
        final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        final Validator validator = validatorFactory.getValidator();

        return validator.validate(demo);
    }
}
