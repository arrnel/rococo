package org.rococo.tests.service.gateway;

import io.qameta.allure.Step;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.rococo.tests.client.gateway.MuseumsApiClient;
import org.rococo.tests.ex.TokenIsEmptyException;
import org.rococo.tests.model.MuseumDTO;
import org.rococo.tests.model.Token;
import org.rococo.tests.service.MuseumService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@ParametersAreNonnullByDefault
public class MuseumServiceGateway implements MuseumService {

    private final MuseumsApiClient museumClient = new MuseumsApiClient();

    @Getter
    @Setter
    private Token token;

    @Nonnull
    @Override
    @Step("Add new museum: [{museumDTO.title}]")
    public MuseumDTO add(MuseumDTO museumDTO) {
        log.info("Add new museum: {}", museumDTO);
        checkToken();
        return museumClient.add(token.token(), museumDTO);
    }

    @Nonnull
    @Override
    @Step("Find museum by id: [{id}]")
    public Optional<MuseumDTO> findById(UUID id) {
        log.info("Find museum by id: {}", id);
        return museumClient.findById(id);
    }

    @Nonnull
    @Override
    @Step("Find museum by title: [{title}]")
    public Optional<MuseumDTO> findByTitle(String title) {
        log.info("Find museum by title: {}", title);
        return findMuseums(null).stream()
                .filter(museum -> museum.getTitle().equals(title))
                .findFirst();
    }

    @NotNull
    @Override
    @Step("Find all museums with partial title: [{partialTitle}]")
    public List<MuseumDTO> findAllByPartialTitle(String partialTitle) {
        log.info("Find all museums by partial title: {}", partialTitle);
        return findMuseums(partialTitle);
    }

    @Nonnull
    @Override
    @Step("Find all museums")
    public List<MuseumDTO> findAll() {
        log.info("Find all museums");
        return findMuseums(null);
    }

    @Nonnull
    @Override
    @Step("Update museum: [{museumDTO.title}]")
    public MuseumDTO update(MuseumDTO museumDTO) {
        log.info("Update museum: {}", museumDTO);
        checkToken();
        return museumClient.update(token.token(), museumDTO);
    }

    @Override
    @Step("Delete museum by id: [{id}]")
    public void delete(UUID id) {
        log.info("Delete museum: {}", id);
        checkToken();
        museumClient.delete(token.token(), id);
    }

    @Override
    @Step("Delete all museums")
    public void clearAll() {
        log.info("Clear all museums");
        checkToken();
        findMuseums(null)
                .forEach(museum -> museumClient.delete(token.token(), museum.getId()));
    }

    private void checkToken() {
        if (token == null || token.token() == null || token.token().isEmpty())
            throw new TokenIsEmptyException();
    }

    private List<MuseumDTO> findMuseums(@Nullable String title) {
        List<MuseumDTO> allMuseums = new ArrayList<>();
        Pageable pageable = PageRequest.of(0, 10);

        while (true) {
            Page<MuseumDTO> page = museumClient.findAll(title, pageable.getPageNumber(), pageable.getPageSize());
            allMuseums.addAll(page.getContent());
            if (!page.hasNext()) break;
            pageable = page.nextPageable();
        }

        return allMuseums;
    }


}
