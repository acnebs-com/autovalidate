package com.acnebs.autovalidate.restexampl.room.domain;

import org.junit.Test;

import javax.validation.ConstraintViolationException;

import static org.junit.Assert.*;

public class RoomTest {

    @Test
    public void test_validation() throws Exception {
        try {
            Room.builder().build();
            fail("It should fail");
        }
        catch (ConstraintViolationException e) {
            assertEquals(3, e.getConstraintViolations().size());
        }
    }



}
