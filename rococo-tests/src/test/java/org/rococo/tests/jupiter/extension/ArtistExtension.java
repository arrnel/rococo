package org.rococo.tests.jupiter.extension;

import org.junit.jupiter.api.extension.*;
import org.rococo.tests.jupiter.annotation.Artist;
import org.rococo.tests.mapper.ArtistMapper;
import org.rococo.tests.model.ArtistDTO;
import org.rococo.tests.util.DataGenerator;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ArtistExtension extends BaseExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ArtistExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {

        var isMethodAnnotated = context.getRequiredTestMethod().isAnnotationPresent(Artist.class);
        var methodAnno = context.getRequiredTestMethod().getAnnotation(Artist.class);

        Stream.of(context.getRequiredTestMethod().getParameters())
                .filter(parameter -> isMethodAnnotated || parameter.isAnnotationPresent(Artist.class))
                .filter(parameter -> parameter.getType().equals(ArtistDTO.class))
                .forEach(parameter -> {
                    var anno = parameter.isAnnotationPresent(Artist.class)
                            ? parameter.getAnnotation(Artist.class)
                            : methodAnno;
                    putInArtistMap(parameter.getName(), createArtist(anno));
                });

    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext
    ) throws ParameterResolutionException {

        var isRequiredType = parameterContext.getParameter().getType().isAssignableFrom(ArtistDTO.class);
        var isMethodAnnotated = extensionContext.getRequiredTestMethod().isAnnotationPresent(Artist.class);
        var isCurrentParameterAnnotated = parameterContext.getParameter().isAnnotationPresent(Artist.class);

        return isRequiredType && (isMethodAnnotated || isCurrentParameterAnnotated);

    }

    @Override
    public ArtistDTO resolveParameter(ParameterContext parameterContext,
                                      ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        return getArtistsMap().get(parameterContext.getParameter().getName());
    }

    @Nonnull
    private ArtistDTO createArtist(Artist anno) {
        return rococoService.addArtist(
                ArtistMapper.updateFromAnno(
                        DataGenerator.generateArtist(),
                        anno));
    }

    public static void putInArtistMap(String paramName, ArtistDTO artist) {
        getArtistsMap().put(paramName, artist);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    private static Map<String, ArtistDTO> getArtistsMap() {
        ExtensionContext extensionContext = TestMethodContextExtension.context();
        return (Map<String, ArtistDTO>) extensionContext.getStore(NAMESPACE)
                .getOrComputeIfAbsent(extensionContext.getUniqueId(), map -> new HashMap<>());
    }

}
