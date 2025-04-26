package org.rococo.gateway.model.museums;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import org.rococo.gateway.validation.Columns;
import org.springframework.data.domain.Pageable;

@Getter
@Builder
public class MuseumFindAllParamsValidationObject {

    @Size(min = 3, max = 255, message = "{errors.validation.filter.title.size}")
    String title;

    @Columns({"id", "title", "createdDate"})
    Pageable pageable;

}
