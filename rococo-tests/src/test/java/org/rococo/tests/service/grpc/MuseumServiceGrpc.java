package org.rococo.tests.service.grpc;

import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.rococo.tests.client.grpc.MuseumsGrpcClient;
import org.rococo.tests.model.MuseumDTO;
import org.rococo.tests.service.MuseumService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@ParametersAreNonnullByDefault
public class MuseumServiceGrpc implements MuseumService {

    @Nonnull
    @Override
    @Step("Add new museum: [{museum.title}]")
    public MuseumDTO add(MuseumDTO museum) {
        log.info("Add new museum: {}", museum);
        return withClient(museumsClient ->
                museumsClient.add(museum));
    }

    @Nonnull
    @Override
    @Step("Find museum by id: [{id}]")
    public Optional<MuseumDTO> findById(UUID id) {
        log.info("Find museum with id: {}", id);
        return withClient(museumsClient ->
                museumsClient.findById(id));
    }

    @Nonnull
    @Override
    @Step("Find museum by name: [{name}]")
    public Optional<MuseumDTO> findByTitle(String title) {
        log.info("Find museum with title: {}", title);
        return withClient(museumsClient ->
                museumsClient.findByTitle(title));
    }

    @Nonnull
    @Override
    @Step("Find all museums by partial title: [{partialTitle}]")
    public List<MuseumDTO> findAllByPartialTitle(String partialTitle) {
        log.info("Find all museums by partial name: {}", partialTitle);
        return withClient(museumsClient ->
                findAllMuseums(museumsClient, partialTitle));
    }

    @Nonnull
    @Override
    @Step("Find all museums")
    public List<MuseumDTO> findAll() {
        log.info("Find all museums");
        return withClient(museumsClient ->
                findAllMuseums(museumsClient, null));
    }

    @Nonnull
    @Override
    @Step("Update museum with id: [{museum.id}]")
    public MuseumDTO update(MuseumDTO museum) {
        log.info("Update museum: {}", museum);
        return withClient(museumsClient ->
                museumsClient.update(museum));
    }

    @Override
    @Step("Delete museum with id: [{id}]")
    public void delete(UUID id) {
        log.info("Delete museum with id: {}", id);
        withClient(museumsClient -> {
            museumsClient.delete(id);
            return null;
        });
    }

    @Override
    @Step("Clear table \"rococo-museums\" and remove all files with entity_type MUSEUM from \"rococo-files\"")
    public void clearAll() {
        log.info("Truncate table \"rococo-museums\" and remove all files with entity_type MUSEUM from \"rococo-files\"");
        withClient(museumsClient -> {
            findAllMuseums(museumsClient, null).stream()
                    .map(MuseumDTO::getId)
                    .forEach(museumsClient::delete);
            return null;
        });
    }

    @Nonnull
    private List<MuseumDTO> findAllMuseums(MuseumsGrpcClient museumsClient, @Nullable String name) {
        // DON'T remove sort. Help to get all museums in parallel test execution
        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(
                        Sort.Order.asc("createdDate"),
                        Sort.Order.asc("id")
                ));
        List<MuseumDTO> allMuseums = new ArrayList<>();
        while (true) {
            Page<MuseumDTO> page = museumsClient.findAll(name, pageable);
            allMuseums.addAll(page.getContent());
            if (!page.hasNext()) break;
            pageable = pageable.next();
        }
        return allMuseums;
    }

    private <T> T withClient(Function<MuseumsGrpcClient, T> operation) {
        try (MuseumsGrpcClient client = new MuseumsGrpcClient()) {
            return operation.apply(client);
        }
    }

}
