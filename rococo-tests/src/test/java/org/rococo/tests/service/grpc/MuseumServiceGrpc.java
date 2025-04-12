package org.rococo.tests.service.grpc;

import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.rococo.tests.client.grpc.CountriesGrpcClient;
import org.rococo.tests.client.grpc.FilesGrpcClient;
import org.rococo.tests.client.grpc.MuseumsGrpcClient;
import org.rococo.tests.model.ImageDTO;
import org.rococo.tests.model.MuseumDTO;
import org.rococo.tests.service.MuseumService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;

import static org.rococo.tests.enums.EntityType.MUSEUM;

@Slf4j
@ParametersAreNonnullByDefault
public class MuseumServiceGrpc implements MuseumService {

    private final MuseumsGrpcClient museumClient = new MuseumsGrpcClient();

    @Nonnull
    @Override
    @Step("Add new museum: [{museum.title}]")
    public MuseumDTO add(MuseumDTO museum) {
        log.info("Add new museum: {}", museum);
        return museumClient.add(museum);
    }

    @Nonnull
    @Override
    @Step("Find museum by id: [{id}]")
    public Optional<MuseumDTO> findById(UUID id) {
        log.info("Find museum with id: {}", id);
        return museumClient.findById(id);
    }

    @Nonnull
    @Override
    @Step("Find museum by name: [{name}]")
    public Optional<MuseumDTO> findByTitle(String title) {
        log.info("Find museum with title: {}", title);
        return museumClient.findByTitle(title);
    }

    @Nonnull
    @Override
    @Step("Find all museums by partial title: [{partialTitle}]")
    public List<MuseumDTO> findAllByPartialTitle(String partialTitle) {
        log.info("Find all museums by partial name: {}", partialTitle);
        return findAllMuseums(partialTitle);
    }

    @Nonnull
    @Override
    @Step("Find all museums")
    public List<MuseumDTO> findAll() {
        log.info("Find all museums");
        return findAllMuseums(null);
    }

    @Nonnull
    @Override
    @Step("Update museum with id: [{museum.id}]")
    public MuseumDTO update(MuseumDTO museum) {
        log.info("Update museum: {}", museum);
        return museumClient.update(museum);
    }

    @Override
    @Step("Delete museum with id: [{id}]")
    public void delete(UUID id) {
        log.info("Delete museum with id: {}", id);
        museumClient.delete(id);
    }

    @Override
    @Step("Clear table \"rococo-museums\" and remove all files with entity_type MUSEUM from \"rococo-files\"")
    public void clearAll() {
        log.info("Truncate table \"rococo-museums\" and remove all files with entity_type MUSEUM from \"rococo-files\"");
        findAllMuseums(null).stream()
                .map(MuseumDTO::getId)
                .forEach(this::delete);
    }

    @Nonnull
    private List<MuseumDTO> findAllMuseums(@Nullable String name) {
        List<MuseumDTO> allMuseums = new ArrayList<>();
        Pageable pageable = PageRequest.of(0, 10);

        while (true) {
            Page<MuseumDTO> page = museumClient.findAll(name, pageable);
            allMuseums.addAll(page.getContent());
            if (!page.hasNext()) break;
            pageable = page.nextPageable();
        }

        return allMuseums;
    }

}
