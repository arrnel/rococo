package org.rococo.tests.jupiter.extension;

import org.junit.jupiter.api.extension.*;
import org.rococo.tests.jupiter.annotation.Users;
import org.rococo.tests.mapper.UserMapper;
import org.rococo.tests.model.UserDTO;
import org.rococo.tests.util.DataGenerator;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Stream;

public class UsersExtension extends BaseExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UsersExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {

        var isMethodAnnotated = context.getRequiredTestMethod().isAnnotationPresent(Users.class);
        var methodAnno = context.getRequiredTestMethod().getAnnotation(Users.class);

        Stream.of(context.getRequiredTestMethod().getParameters())
                .filter(parameter -> isMethodAnnotated || parameter.isAnnotationPresent(Users.class))
                .filter(parameter -> isParameterListOfType(parameter, UserDTO.class))
                .forEach(parameter -> {
                    var anno = parameter.isAnnotationPresent(Users.class)
                            ? parameter.getAnnotation(Users.class)
                            : methodAnno;
                    putInUsersMap(parameter.getName(), createUsers(anno));
                });

    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext
    ) throws ParameterResolutionException {

        var isRequiredType = isParameterListOfType(parameterContext.getParameter(), UserDTO.class);
        var isMethodAnnotated = extensionContext.getRequiredTestMethod().isAnnotationPresent(Users.class);
        var isCurrentParameterAnnotated = parameterContext.getParameter().isAnnotationPresent(Users.class);

        return isRequiredType && (isMethodAnnotated || isCurrentParameterAnnotated);

    }

    @Override
    public List<UserDTO> resolveParameter(ParameterContext parameterContext,
                                          ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        return getUsersMap().get(parameterContext.getParameter().getName());
    }

    @Nonnull
    private List<UserDTO> createUsers(Users anno) {
        var randomUsers = rococoService.addUsers(anno.count());
        var annoUsers = rococoService.addUsers(Stream.of(anno.value())
                .map(userAnno -> UserMapper.updateFromAnno(DataGenerator.generateUser(), userAnno))
                .toList());
        var users = new ArrayList<UserDTO>();
        users.addAll(annoUsers);
        users.addAll(randomUsers);
        return users;
    }

    public static void putInUsersMap(String paramName, List<UserDTO> user) {
        getUsersMap().put(paramName, user);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    private static Map<String, List<UserDTO>> getUsersMap() {
        ExtensionContext extensionContext = TestMethodContextExtension.context();
        return (Map<String, List<UserDTO>>) extensionContext.getStore(NAMESPACE)
                .getOrComputeIfAbsent(extensionContext.getUniqueId(), map -> new HashMap<>());
    }

    public static Optional<UserDTO> findUser(String username) {
        return getUsersMap().values().stream()
                .flatMap(List::stream)
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

}
