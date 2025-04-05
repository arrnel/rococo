package org.rococo.tests.jupiter.extension;

import org.junit.jupiter.api.extension.*;
import org.rococo.tests.enums.CountryCode;
import org.rococo.tests.ex.CountryNotFoundException;
import org.rococo.tests.jupiter.annotation.Country;
import org.rococo.tests.model.CountryDTO;
import org.rococo.tests.service.CountryService;
import org.rococo.tests.service.db.CountryServiceDb;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class CountryExtension extends BaseExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CountryExtension.class);

    private final CountryService countryService = new CountryServiceDb();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {

        var isMethodAnnotated = context.getRequiredTestMethod().isAnnotationPresent(Country.class);
        var methodAnno = context.getRequiredTestMethod().getAnnotation(Country.class);

        Stream.of(context.getRequiredTestMethod().getParameters())
                .filter(parameter -> isMethodAnnotated || parameter.isAnnotationPresent(Country.class))
                .filter(parameter -> parameter.getType().equals(CountryDTO.class))
                .forEach(parameter -> {
                    var anno = parameter.isAnnotationPresent(Country.class)
                            ? parameter.getAnnotation(Country.class)
                            : methodAnno;
                    putInCountryMap(parameter.getName(), findCountry(anno));
                });

    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext
    ) throws ParameterResolutionException {

        var isRequiredType = parameterContext.getParameter().getType().isAssignableFrom(CountryDTO.class);
        var isMethodAnnotated = extensionContext.getRequiredTestMethod().isAnnotationPresent(Country.class);
        var isCurrentParameterAnnotated = parameterContext.getParameter().isAnnotationPresent(Country.class);

        return isRequiredType && (isMethodAnnotated || isCurrentParameterAnnotated);

    }

    @Override
    public CountryDTO resolveParameter(ParameterContext parameterContext,
                                       ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        return getCountriesMap().get(parameterContext.getParameter().getName());
    }

    @Nonnull
    private CountryDTO findCountry(Country anno) {
        var countryCode = anno.code() == CountryCode.EMPTY
                ? CountryCode.random()
                : anno.code();
        return countryService.findByCode(countryCode)
                .orElseThrow(() -> new CountryNotFoundException(countryCode));
    }

    public static void putInCountryMap(String paramName, CountryDTO country) {
        getCountriesMap().put(paramName, country);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    private static Map<String, CountryDTO> getCountriesMap() {
        ExtensionContext extensionContext = TestMethodContextExtension.context();
        return (Map<String, CountryDTO>) extensionContext.getStore(NAMESPACE)
                .getOrComputeIfAbsent(extensionContext.getUniqueId(), map -> new HashMap<>());
    }

}
