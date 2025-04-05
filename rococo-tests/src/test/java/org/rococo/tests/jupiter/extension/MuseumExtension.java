package org.rococo.tests.jupiter.extension;

import org.junit.jupiter.api.extension.*;
import org.rococo.tests.jupiter.annotation.Museum;
import org.rococo.tests.mapper.MuseumMapper;
import org.rococo.tests.model.MuseumDTO;
import org.rococo.tests.util.DataGenerator;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class MuseumExtension extends BaseExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(MuseumExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {

        var isMethodAnnotated = context.getRequiredTestMethod().isAnnotationPresent(Museum.class);
        var methodAnno = context.getRequiredTestMethod().getAnnotation(Museum.class);

        Stream.of(context.getRequiredTestMethod().getParameters())
                .filter(parameter -> isMethodAnnotated || parameter.isAnnotationPresent(Museum.class))
                .filter(parameter -> parameter.getType().equals(MuseumDTO.class))
                .forEach(parameter -> {
                    var anno = parameter.isAnnotationPresent(Museum.class)
                            ? parameter.getAnnotation(Museum.class)
                            : methodAnno;
                    putInMuseumMap(parameter.getName(), createMuseum(anno));
                });

    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext
    ) throws ParameterResolutionException {

        var isRequiredType = parameterContext.getParameter().getType().isAssignableFrom(MuseumDTO.class);
        var isMethodAnnotated = extensionContext.getRequiredTestMethod().isAnnotationPresent(Museum.class);
        var isCurrentParameterAnnotated = parameterContext.getParameter().isAnnotationPresent(Museum.class);

        return isRequiredType && (isMethodAnnotated || isCurrentParameterAnnotated);

    }

    @Override
    public MuseumDTO resolveParameter(ParameterContext parameterContext,
                                      ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        return getMuseumsMap().get(parameterContext.getParameter().getName());
    }

    @Nonnull
    private MuseumDTO createMuseum(Museum anno) {
        return rococoService.addMuseum(
                MuseumMapper.updateFromAnno(
                        DataGenerator.generateMuseum(),
                        anno));
    }

    public static void putInMuseumMap(String paramName, MuseumDTO museum) {
        getMuseumsMap().put(paramName, museum);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    private static Map<String, MuseumDTO> getMuseumsMap() {
        ExtensionContext extensionContext = TestMethodContextExtension.context();
        return (Map<String, MuseumDTO>) extensionContext.getStore(NAMESPACE)
                .getOrComputeIfAbsent(extensionContext.getUniqueId(), map -> new HashMap<>());
    }

}
