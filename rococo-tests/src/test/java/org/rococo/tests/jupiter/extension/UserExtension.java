package org.rococo.tests.jupiter.extension;

import org.junit.jupiter.api.extension.*;
import org.rococo.tests.jupiter.annotation.User;
import org.rococo.tests.mapper.UserMapper;
import org.rococo.tests.model.UserDTO;
import org.rococo.tests.util.DataGenerator;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
public class UserExtension extends BaseExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {

        var isMethodAnnotated = context.getRequiredTestMethod().isAnnotationPresent(User.class);
        var methodAnno = context.getRequiredTestMethod().getAnnotation(User.class);

        Stream.of(context.getRequiredTestMethod().getParameters())
                .filter(parameter -> isMethodAnnotated || parameter.isAnnotationPresent(User.class))
                .filter(parameter -> parameter.getType().equals(UserDTO.class))
                .forEach(parameter -> {
                    var anno = parameter.isAnnotationPresent(User.class)
                            ? parameter.getAnnotation(User.class)
                            : methodAnno;
                    putInUserMap(parameter.getName(), createUser(anno));
                });

    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext
    ) throws ParameterResolutionException {

        var isRequiredType = parameterContext.getParameter().getType().isAssignableFrom(UserDTO.class);
        var isMethodAnnotated = extensionContext.getRequiredTestMethod().isAnnotationPresent(User.class);
        var isCurrentParameterAnnotated = parameterContext.getParameter().isAnnotationPresent(User.class);

        return isRequiredType && (isMethodAnnotated || isCurrentParameterAnnotated);

    }

    @Nonnull
    @Override
    public UserDTO resolveParameter(ParameterContext parameterContext,
                                    ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        return getUserMap().get(parameterContext.getParameter().getName());
    }

    @Nonnull
    private UserDTO createUser(User anno) {
        return rococoService.addUser(
                UserMapper.updateFromAnno(
                        DataGenerator.generateUser(),
                        anno));
    }

    public static void putInUserMap(String paramName, UserDTO user) {
        getUserMap().put(paramName, user);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    private static Map<String, UserDTO> getUserMap() {
        ExtensionContext extensionContext = TestMethodContextExtension.context();
        return (Map<String, UserDTO>) extensionContext.getStore(NAMESPACE)
                .getOrComputeIfAbsent(extensionContext.getUniqueId(), map -> new HashMap<>());
    }

    public static Optional<UserDTO> findUser(String username) {
        return getUserMap().values().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

}
