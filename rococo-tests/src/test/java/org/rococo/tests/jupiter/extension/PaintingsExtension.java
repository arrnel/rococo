package org.rococo.tests.jupiter.extension;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.*;
import org.rococo.tests.jupiter.annotation.Paintings;
import org.rococo.tests.mapper.PaintingMapper;
import org.rococo.tests.model.PaintingDTO;
import org.rococo.tests.util.DataGenerator;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
public class PaintingsExtension extends BaseExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(PaintingsExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {

        var isMethodAnnotated = context.getRequiredTestMethod().isAnnotationPresent(Paintings.class);
        var methodAnno = context.getRequiredTestMethod().getAnnotation(Paintings.class);

        Stream.of(context.getRequiredTestMethod().getParameters())
                .filter(parameter -> isMethodAnnotated || parameter.isAnnotationPresent(Paintings.class))
                .filter(parameter -> isParameterListOfType(parameter, PaintingDTO.class))
                .forEach(parameter -> {
                    var anno = parameter.isAnnotationPresent(Paintings.class)
                            ? parameter.getAnnotation(Paintings.class)
                            : methodAnno;
                    putInPaintingsMap(parameter.getName(), createPaintings(anno));
                });

    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext
    ) throws ParameterResolutionException {

        var isRequiredType = isParameterListOfType(parameterContext.getParameter(), PaintingDTO.class);
        var isMethodAnnotated = extensionContext.getRequiredTestMethod().isAnnotationPresent(Paintings.class);
        var isCurrentParameterAnnotated = parameterContext.getParameter().isAnnotationPresent(Paintings.class);

        return isRequiredType && (isMethodAnnotated || isCurrentParameterAnnotated);

    }

    @Override
    public List<PaintingDTO> resolveParameter(ParameterContext parameterContext,
                                              ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        return getPaintingsMap().get(parameterContext.getParameter().getName());
    }

    @Nonnull
    private List<PaintingDTO> createPaintings(Paintings anno) {
        var randomPaintings = rococoService.addPaintings(anno.count());
        var annoPaintings = rococoService.addPaintings(Stream.of(anno.value())
                .map(paintingAnno -> PaintingMapper.updateFromAnno(DataGenerator.generatePainting(), paintingAnno))
                .toList());

        var paintings = new ArrayList<PaintingDTO>();
        paintings.addAll(randomPaintings);
        paintings.addAll(annoPaintings);

        return paintings;
    }

    public static void putInPaintingsMap(String paramName, List<PaintingDTO> painting) {
        getPaintingsMap().put(paramName, painting);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    private static Map<String, List<PaintingDTO>> getPaintingsMap() {
        ExtensionContext extensionContext = TestMethodContextExtension.context();
        return (Map<String, List<PaintingDTO>>) extensionContext.getStore(NAMESPACE)
                .getOrComputeIfAbsent(extensionContext.getUniqueId(), map -> new HashMap<>());
    }

}
