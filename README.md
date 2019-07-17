# auto-validate
Library for creating self-validating objects in Java

# So, what's the deal?
Consider this simple Java bean:
```java
class DemoBean {
    @Size(min = 1, max = 256)
    private String firstName;

    String getFirstName() {
        return firstName;
    }

    void setFirstName(final String firstName) {
        this.firstName = firstName;
    }


    @NotNull
    @Size(min = 1, max = 256)
    private String lastName;


    String getLastName() {
        return lastName;
    }

    void setLastName(final String lastName) {
        this.lastName = lastName;
    }


    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")
    private String email;

    String getEmail() {
        return email;
    }

    void setEmail(final String email) {
        this.email = email;
    }
}      
```

or rather the way more concise [Lombok-ified](https://objectcomputing.com/resources/publications/sett/january-2010-reducing-boilerplate-code-with-project-lombok) equivalent:  
```java
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class DemoBean {

    @Size(min = 1, max = 256)
    String firstName;

    @NotNull
    @Size(min = 1, max = 256)
    String lastName;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")
    String email;
}
```


This class clearly declares validation rules for each of its attributes.
But what does that mean, actually?
Let's write a test and find out:
```java
DemoBean demo = new DemoBean();         // invalid!
demo.setFirstName("Joey");              // invalid!
demo.setLastName("Ramone");             // invalid!
demo.setEmail("joey_at_ramones.com");   // invalid!
```
At any given time the object has been invalid.
Let's create the first valid version:
```java
demo.setEmail("joey@ramones.com");  // de-facto valid...
```
At this point the object is de-facto valid but this fact has not been checked yet.
For this to be done some code OUTSIDE of the object needs to be executed, e.g.:
```java
ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
Validator validator = validatorFactory.getValidator();
Set<ConstraintViolation<DemoBean>> violations = validator.validate(demo);
assertEquals("Object should be valid.", 0, violations.size());
```

This means the poor object has no control over its own validity.

But it gets worse: Now that the object is in a valid state at last, let's break it again:
```java
demo.setEmail("This is not a valid email address!");
violations = validator.validate(demo);
assertEquals("Object should have 1 error because the email address is wrong.", 1, violations.size());
```
Oops, a simple, maybe accidental, call of a setter broke the object's validity and no-one ever noticed,
neither the code that asserted the object's validity a second ago nor the object itself.
In order to find out we had to call the validate method again.


# Some remedy: Builders
We can solve the second problem by using the Builder pattern.
With Lombok, this is actually [super-easy, barely an inconvenience](https://twitter.com/hashtag/SuperEasyBarelyAnInconvenience?src=hash):
```java
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder(toBuilder=true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class DemoBuilder {

    @Size(min = 1, max = 256)
    String firstName;

    @NotNull
    @Size(min = 1, max = 256)
    String lastName;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")
    String email;
}
```

Now there are no more setters and each field is final.
This baby is immutable now.
In order to change a value we would need to "clone" it first.
We can use the toBuilder method that Lombok generated for us (@Builder(toBuilder=true)).
```java
final DemoBuilder demo2 = demo.toBuilder()
                .email("Some other broken email address")
                .build();                           
assertNotSame(demo, demo2);

violations = validate(demo); 
assertEquals("Original object should still be valid.", 0, violations.size());     

violations = validate(demo2); 
assertEquals("Object 2 should have 1 error because the email address is wrong.", 1, violations.size());
```


However, the first problem remains:
```java
DemoBuilder demo = DemoBuilder.builder()  // not done yet
        .firstName("Joey")                // not done yet
        .lastName("Ramone")               // not done yet
        .email("joey_at_ramones.com")     // not done yet
        .build();                         // INVALID 
```        
At this point the object is INVALID because the email address is broken.
However, the object doesn't know that it's broken.
Any code that picks it up does not know it's trying to work with invalid data.
We still depend on a mechanism outside of the object to call the validate method.


# Enter AutoValidate
Here's a solution to both problems:
1. An object that is guaranteed to be either valid or non-existent (by throwing an exception).
2. ... and immutable.
```java
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder(toBuilder=true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@AutoValidate
class DemoAutovalidateClass {

    @Size(min = 1, max = 256)
    String firstName;

    @NotNull
    @Size(min = 1, max = 256)
    String lastName;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")
    String email;
}
```

Um, how is this different?
Well, the magic lies in the @AutoValidate annotation.

Here's how you can use (and test) it:
```java
@Test
public void test_demo() {
    final DemoAutovalidateClass demo = DemoAutovalidateClass.builder()    // not done yet
            .firstName("Joey")                                      // not done yet
            .lastName("Ramone")                                     // not done yet
            .email("joey@ramones.com")                              // not done yet
            .build();
    // If we are still "alive" at this point then the object MUST be valid.

    // Fortunately, making it INVALID again is not possible...
    // Let's "clone" it instead (using the toBuilder method provided by Lombok):
    try {
        DemoAutovalidateClass demo2 = demo.toBuilder()
                .email("joey_at_ramones.com")
                .build();
        fail("This should have failed because the object is not valid");
        // also: the variable demo2 was never assigned any value. 
        // The data was broken, so it never existed,
    }
    catch (ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        assertEquals("Object should have 1 error because the email address is wrong.", 1, violations.size());
    }

    // At no point in the process has the object been available in an invalid state.
    // This means, if it exists it must be valid and can be trusted anywhere it is used.
}
```


# How it works
IN THE BACKGROUND, there's an AspectJ Pointcut for every constructor of a class annotated with @AutoValidate.
(Alternatively, you can annotate a specific constructor with the same annotation.)
For this Pointcut there's an AOP Advice that calls javax.validation for you right after the constructor has finished
but before any other code can use the object.
If the object is invalid then the Advice throws an javax.validation.ConstraintViolationException.

All YOU have to do:
1. Use the @AutoValidate annotation on each class you want to use this feature for, either on the level of the class or the constructor.
2. Enable AspectJ, e.g. -javaagent:{path_to_aspectjweaver}






