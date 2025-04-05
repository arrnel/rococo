package org.rococo.gateway.model.countries;

import lombok.Builder;
import lombok.Getter;
import org.rococo.gateway.validation.Columns;
import org.springframework.data.domain.Pageable;

@Getter
@Builder
public class CountryFindAllParamsValidationObject {

    @Columns({"id", "name", "code"})
    Pageable pageable;

}
