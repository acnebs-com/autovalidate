package com.acnebs.autovalidate.restexampl.common.rest;


import com.fasterxml.jackson.databind.*;
import org.springframework.hateoas.*;
import org.springframework.http.HttpEntity;

import java.util.Optional;
import java.util.function.Function;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

public abstract class ControllerBase {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private final Function<Object, ? extends ResourceSupport> halResourceFactory;
    private final Function<ResourceSupport, String> idFactory;
    private final Class<?> resClass;

    protected ControllerBase(
            final Function<Object, ResourceSupport> halResourceFactory,
            final Function<ResourceSupport, String> idFactory,
            final Class<?> resClass
    ) {
        this.halResourceFactory = halResourceFactory;
        this.idFactory = idFactory;
        this.resClass = resClass;
    }

    protected <T extends ResourceSupport> Optional<T> toHalResource(final Object object) {
        return Optional.ofNullable(object)
                .map(r -> objectMapper.convertValue(r, resClass))
                .map(halResourceFactory::apply)
                .map(hres -> {
                    hres.add(linkToWithSelfRel(idFactory.apply(hres)));
                    return (T) hres;
                });
    }

    private Link linkToWithSelfRel(final String number) {
        return linkTo(getLinkTarget(number)).withSelfRel();
    }

    private Link linkToWithRel(final String number, final String rel) {
        return linkTo(getLinkTarget(number)).withRel(rel);
    }

    protected abstract <T extends ResourceSupport> HttpEntity<T> getLinkTarget(final String number);

}
