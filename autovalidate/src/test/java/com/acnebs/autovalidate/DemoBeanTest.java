package com.acnebs.autovalidate;

import org.junit.Test;

import javax.validation.*;

import java.util.Set;

import static org.junit.Assert.*;


public class DemoBeanTest {
    @Test
    public void test_new() {
        assertNotNull(new DemoBean());
    }
    
    @Test
    public void test_demo() throws Exception {
        // DemoBean defines validation rules for its validity.
        // Let's try it.
        DemoBean demo = new DemoBean();     // invalid!
        demo.setFirstName("Joey");          // invalid!
        demo.setLastName("Ramone");         // invalid!
        demo.setEmail("joey@ramones.com");  // de-facto valid...
        // at this point the object is de-facto valid but this fact has not been checked yet

        final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        final Validator validator = validatorFactory.getValidator();

        Set<ConstraintViolation<DemoBean>> violations = validator.validate(demo);
        assertEquals("Object should be valid.", 0, violations.size());

        demo.setEmail("joey_at_ramones.com");
        violations = validator.validate(demo);
        assertEquals("Object should have 1 error because the email address is wrong.", 1, violations.size());
        // but wait a second: wasn't that thing valid earlier? how could someone make it invalid again?
    }
}
