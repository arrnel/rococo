package org.rococo.tests.jupiter.extension;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.*;
import org.rococo.tests.jupiter.annotation.Artists;
import org.rococo.tests.mapper.ArtistMapper;
import org.rococo.tests.model.ArtistDTO;
import org.rococo.tests.util.DataGenerator;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
public class ArtistsExtension extends BaseExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ArtistsExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {

        var isMethodAnnotated = context.getRequiredTestMethod().isAnnotationPresent(Artists.class);
        var methodAnno = context.getRequiredTestMethod().getAnnotation(Artists.class);

        Stream.of(context.getRequiredTestMethod().getParameters())
                .filter(parameter -> isMethodAnnotated || parameter.isAnnotationPresent(Artists.class))
                .filter(parameter -> isParameterListOfType(parameter, ArtistDTO.class))
                .forEach(parameter -> {
                    var anno = parameter.isAnnotationPresent(Artists.class)
                            ? parameter.getAnnotation(Artists.class)
                            : methodAnno;
                    putInArtistsMap(parameter.getName(), createArtists(anno));
                });

    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext
    ) throws ParameterResolutionException {

        var isRequiredType = isParameterListOfType(parameterContext.getParameter(), ArtistDTO.class);
        var isMethodAnnotated = extensionContext.getRequiredTestMethod().isAnnotationPresent(Artists.class);
        var isParameterAnnotated = parameterContext.getParameter().isAnnotationPresent(Artists.class);

        return isRequiredType && (isMethodAnnotated || isParameterAnnotated);

    }

    @Override
    public List<ArtistDTO> resolveParameter(ParameterContext parameterContext,
                                            ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        return getArtistsMap().get(parameterContext.getParameter().getName());
    }

    @Nonnull
    private List<ArtistDTO> createArtists(Artists anno) {
        var randomArtists = rococoService.addArtists(anno.count());
        var annoArtists = rococoService.addArtists(Stream.of(anno.value())
                .map(artistAnno -> ArtistMapper.updateFromAnno(DataGenerator.generateArtist(), artistAnno))
                .toList());

        var artists = new ArrayList<ArtistDTO>();
        artists.addAll(annoArtists);
        artists.addAll(randomArtists);

        return artists;
    }

    public static void putInArtistsMap(String paramName, List<ArtistDTO> artist) {
        getArtistsMap().put(paramName, artist);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    private static Map<String, List<ArtistDTO>> getArtistsMap() {
        ExtensionContext extensionContext = TestMethodContextExtension.context();
        return (Map<String, List<ArtistDTO>>) extensionContext.getStore(NAMESPACE)
                .getOrComputeIfAbsent(extensionContext.getUniqueId(), map -> new HashMap<>());
    }

}
