package org.rococo.tests.jupiter.extension;

import org.junit.jupiter.api.extension.*;
import org.rococo.tests.jupiter.annotation.Painting;
import org.rococo.tests.mapper.PaintingMapper;
import org.rococo.tests.model.PaintingDTO;
import org.rococo.tests.util.DataGenerator;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class PaintingExtension extends BaseExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(PaintingExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {

        var isMethodAnnotated = context.getRequiredTestMethod().isAnnotationPresent(Painting.class);
        var methodAnno = context.getRequiredTestMethod().getAnnotation(Painting.class);

        Stream.of(context.getRequiredTestMethod().getParameters())
                .filter(parameter -> isMethodAnnotated || parameter.isAnnotationPresent(Painting.class))
                .filter(parameter -> parameter.getType().equals(PaintingDTO.class))
                .forEach(parameter -> {
                    var anno = parameter.isAnnotationPresent(Painting.class)
                            ? parameter.getAnnotation(Painting.class)
                            : methodAnno;
                    putInPaintingMap(parameter.getName(), createPainting(anno));
                });

    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext
    ) throws ParameterResolutionException {

        var isRequiredType = parameterContext.getParameter().getType().isAssignableFrom(PaintingDTO.class);
        var isMethodAnnotated = extensionContext.getRequiredTestMethod().isAnnotationPresent(Painting.class);
        var isCurrentParameterAnnotated = parameterContext.getParameter().isAnnotationPresent(Painting.class);

        return isRequiredType && (isMethodAnnotated || isCurrentParameterAnnotated);

    }

    @Override
    public PaintingDTO resolveParameter(ParameterContext parameterContext,
                                        ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        return getPaintingsMap().get(parameterContext.getParameter().getName());
    }

    @Nonnull
    private PaintingDTO createPainting(Painting anno) {
        return rococoService.addPainting(
                PaintingMapper.updateFromAnno(
                        DataGenerator.generatePainting(),
                        anno));
    }

    public static void putInPaintingMap(String paramName, PaintingDTO painting) {
        getPaintingsMap().put(paramName, painting);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    private static Map<String, PaintingDTO> getPaintingsMap() {
        ExtensionContext extensionContext = TestMethodContextExtension.context();
        return (Map<String, PaintingDTO>) extensionContext.getStore(NAMESPACE)
                .getOrComputeIfAbsent(extensionContext.getUniqueId(), map -> new HashMap<>());
    }

}
