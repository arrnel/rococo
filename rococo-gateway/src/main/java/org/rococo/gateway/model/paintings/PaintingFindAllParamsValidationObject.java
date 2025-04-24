package org.rococo.gateway.model.paintings;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import org.rococo.gateway.validation.Columns;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@Getter
@Builder
public class PaintingFindAllParamsValidationObject {

    @Size(min = 3, max = 255, message = "{errors.validation.filter.title.size}")
    String title;

    UUID artistId;

    @Columns({"id", "title", "createdDate"})
    Pageable pageable;

}
