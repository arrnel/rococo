package org.rococo.tests.service.db;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rococo.tests.enums.CountryCode;
import org.rococo.tests.ex.CountryNotFoundException;
import org.rococo.tests.model.ArtistDTO;
import org.rococo.tests.model.MuseumDTO;
import org.rococo.tests.model.PaintingDTO;
import org.rococo.tests.model.UserDTO;
import org.rococo.tests.service.*;
import org.rococo.tests.util.DataGenerator;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor
@ParametersAreNonnullByDefault
public class RococoServiceDb implements RococoService {

    private final ArtistService artistService = new ArtistServiceDb();
    private final CountryService countryService = new CountryServiceDb();
    private final MuseumService museumService = new MuseumServiceDb();
    private final PaintingService paintingService = new PaintingServiceDb();
    private final UserService userService = new UserServiceDb();

    @Nonnull
    @Override
    public ArtistDTO addArtist(ArtistDTO artist) {
        return artistService.findByName(artist.getName())
                .orElseGet(() -> artistService.add(artist));
    }

    @Nonnull
    @Override
    @Step("Add artists")
    public List<ArtistDTO> addArtists(List<ArtistDTO> artists) {
        if (artists.isEmpty()) return Collections.emptyList();
        log.info("Create [{}] artists", artists.size());
        return artists.stream()
                .map(this::addArtist)
                .toList();
    }

    @Nonnull
    @Override
    @Step("Add [{count}] random artists")
    public List<ArtistDTO> addArtists(int count) {
        if (count < 0) throw new IllegalArgumentException("Artists count cannot be negative");
        if (count == 0) return Collections.emptyList();
        log.info("Create [{}] random artists", count);
        return IntStream.range(0, count)
                .mapToObj(i -> addArtist(DataGenerator.generateArtist()))
                .toList();
    }

    @Nonnull
    @Override
    public MuseumDTO addMuseum(MuseumDTO museum) {

        var countryCode = museum.getLocation().getCountry().getCode();
        if (countryCode == null || countryCode == CountryCode.EMPTY)
            throw new IllegalArgumentException("Museum country code cannot be null or equals EMPTY");

        var country = countryService.findByCode(countryCode)
                .orElseThrow(() -> new CountryNotFoundException(countryCode));

        return museumService.add(museum.setCountry(country))
                .setCountry(country);
    }

    @Nonnull
    @Override
    @Step("Add museums")
    public List<MuseumDTO> addMuseums(List<MuseumDTO> museums) {
        if (museums.isEmpty()) return Collections.emptyList();
        log.info("Create [{}] museums", museums.size());
        return museums.stream()
                .map(this::addMuseum)
                .toList();
    }

    @Nonnull
    @Override
    @Step("Add [{count}] random museums")
    public List<MuseumDTO> addMuseums(int count) {
        if (count < 0) throw new IllegalArgumentException("Museums count cannot be negative");
        if (count == 0) return Collections.emptyList();
        log.info("Create [{}] random museums", count);
        return IntStream.range(0, count)
                .mapToObj(i -> addMuseum(DataGenerator.generateMuseum()))
                .toList();
    }

    @Nonnull
    @Override
    public PaintingDTO addPainting(PaintingDTO painting) {

        var artist = artistService.findByName(painting.getArtist().getName())
                .orElseGet(() -> artistService.add(painting.getArtist()));
        var museum = museumService.findByTitle(painting.getMuseum().getTitle())
                .orElseGet(() -> {
                    var countryCode = painting.getMuseum().getLocation().getCountry().getCode();
                    if (countryCode == null || countryCode == CountryCode.EMPTY) {
                        throw new IllegalArgumentException("Museum country code cannot be null or equals EMPTY");
                    }
                    var country = countryService.findByCode(countryCode)
                            .orElseThrow(() -> new CountryNotFoundException(countryCode));
                    return museumService.add(painting.getMuseum().setCountry(country)).setCountry(country);
                });

        painting.setArtist(artist)
                .setMuseum(museum);

        return paintingService.findByTitle(painting.getTitle())
                .orElseGet(() -> paintingService.add(painting))
                .setArtist(artist)
                .setMuseum(museum);

    }

    @Nonnull
    @Override
    public List<PaintingDTO> addPaintings(List<PaintingDTO> paintings) {
        if (paintings.isEmpty()) return Collections.emptyList();
        log.info("Create [{}] paintings", paintings.size());
        return Allure.step("Add [%s] paintings".formatted(paintings.size()),
                () -> paintings.stream()
                        .map(this::addPainting)
                        .toList());
    }

    @Nonnull
    @Override
    public List<PaintingDTO> addPaintings(int count) {
        if (count < 0) throw new IllegalArgumentException("Paintings count cannot be negative");
        if (count == 0) return Collections.emptyList();
        log.info("Create [{}] random paintings", count);
        return Allure.step("Add [%s] random paintings".formatted(count),
                () -> IntStream.range(0, count)
                        .mapToObj(i -> addPainting(DataGenerator.generatePainting()))
                        .toList());
    }

    @Nonnull
    @Override
    @Step("Add user")
    public UserDTO addUser(UserDTO user) {
        return userService.findByUsername(user.getUsername())
                .orElseGet(() -> userService.create(user));
    }

    @Nonnull
    @Override
    @Step("Add users")
    public List<UserDTO> addUsers(List<UserDTO> users) {
        if (users.isEmpty()) return Collections.emptyList();
        log.info("Create [{}] users", users.size());
        return Allure.step("Add [%s] users".formatted(users.size()),
                () -> users.stream()
                        .map(userService::create)
                        .toList());
    }

    @Nonnull
    @Override
    @Step("Add [{count}] random users")
    public List<UserDTO> addUsers(int count) {
        if (count < 0) throw new IllegalArgumentException("Users count cannot be negative");
        if (count == 0) return Collections.emptyList();
        log.info("Create [{}] random users", count);
        return Allure.step("Add [%s] random users".formatted(count),
                () -> IntStream.range(0, count)
                        .mapToObj(i -> userService.create(DataGenerator.generateUser()))
                        .toList());
    }

}
