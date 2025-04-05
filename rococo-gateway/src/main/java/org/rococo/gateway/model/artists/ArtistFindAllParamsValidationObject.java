package org.rococo.gateway.model.artists;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import org.rococo.gateway.validation.Columns;
import org.springframework.data.domain.Pageable;

@Getter
@Builder
public class ArtistFindAllParamsValidationObject {

    @Size(min = 3, max = 255, message = "{errors.validation.filter.name.size}")
    private String name;

    @Columns({"id", "name"})
    private Pageable pageable;

}
