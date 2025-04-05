package org.rococo.tests.jupiter.extension;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.*;
import org.rococo.tests.jupiter.annotation.Museums;
import org.rococo.tests.mapper.MuseumMapper;
import org.rococo.tests.model.MuseumDTO;
import org.rococo.tests.service.RococoService;
import org.rococo.tests.service.db.RococoServiceDb;
import org.rococo.tests.util.DataGenerator;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
public class MuseumsExtension extends BaseExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(MuseumsExtension.class);

    public RococoService rococoService = new RococoServiceDb();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {

        var isMethodAnnotated = context.getRequiredTestMethod().isAnnotationPresent(Museums.class);
        var methodAnno = context.getRequiredTestMethod().getAnnotation(Museums.class);

        Stream.of(context.getRequiredTestMethod().getParameters())
                .filter(parameter -> isMethodAnnotated || parameter.isAnnotationPresent(Museums.class))
                .filter(parameter -> isParameterListOfType(parameter, MuseumDTO.class))
                .forEach(parameter -> {
                    var anno = parameter.isAnnotationPresent(Museums.class)
                            ? parameter.getAnnotation(Museums.class)
                            : methodAnno;
                    putInMuseumsMap(parameter.getName(), createMuseums(anno));
                });

    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext
    ) throws ParameterResolutionException {

        var isRequiredType = isParameterListOfType(parameterContext.getParameter(), MuseumDTO.class);
        var isMethodAnnotated = extensionContext.getRequiredTestMethod().isAnnotationPresent(Museums.class);
        var isCurrentParameterAnnotated = parameterContext.getParameter().isAnnotationPresent(Museums.class);

        return isRequiredType && (isMethodAnnotated || isCurrentParameterAnnotated);

    }

    @Override
    public List<MuseumDTO> resolveParameter(ParameterContext parameterContext,
                                            ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        return getMuseumsMap().get(parameterContext.getParameter().getName());
    }

    @Nonnull
    private List<MuseumDTO> createMuseums(Museums anno) {
        var randomMuseums = rococoService.addMuseums(anno.count());
        var annoMuseums = rococoService.addMuseums(Stream.of(anno.value())
                .map(museumAnno -> MuseumMapper.updateFromAnno(DataGenerator.generateMuseum(), museumAnno))
                .toList());

        var museums = new ArrayList<MuseumDTO>();
        museums.addAll(annoMuseums);
        museums.addAll(randomMuseums);

        return museums;
    }

    public static void putInMuseumsMap(String paramName, List<MuseumDTO> museum) {
        getMuseumsMap().put(paramName, museum);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    private static Map<String, List<MuseumDTO>> getMuseumsMap() {
        ExtensionContext extensionContext = TestMethodContextExtension.context();
        return (Map<String, List<MuseumDTO>>) extensionContext.getStore(NAMESPACE)
                .getOrComputeIfAbsent(extensionContext.getUniqueId(), map -> new HashMap<>());
    }

}
