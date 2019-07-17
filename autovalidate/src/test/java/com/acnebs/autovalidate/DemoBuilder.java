package com.acnebs.autovalidate;


import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.*;

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
