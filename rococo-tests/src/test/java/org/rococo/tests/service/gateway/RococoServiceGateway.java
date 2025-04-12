package org.rococo.tests.service.gateway;

import io.qameta.allure.Step;
import org.rococo.tests.enums.CountryCode;
import org.rococo.tests.ex.CountryNotFoundException;
import org.rococo.tests.model.*;
import org.rococo.tests.service.*;
import org.rococo.tests.util.DataGenerator;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@ParametersAreNonnullByDefault
public class RococoServiceGateway implements RococoService {

    private final ArtistService artistService;
    private final CountryService countryService;
    private final MuseumService museumService;
    private final PaintingService paintingService;
    private final UserService userService;

    public RococoServiceGateway(Token token) {
        artistService = new ArtistServiceGateway(token);
        countryService = new CountryServiceGateway();
        museumService = new MuseumServiceGateway(token);
        paintingService = new PaintingServiceGateway(token);
        userService = new UserServiceGateway(token);
    }

    public void setToken(Token token) {
        ((ArtistServiceGateway) artistService).setToken(token);
        ((MuseumServiceGateway) museumService).setToken(token);
        ((PaintingServiceGateway) paintingService).setToken(token);
        ((UserServiceGateway) userService).setToken(token);
    }

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
        return artists.stream()
                .map(artist -> artistService.findByName(artist.getName())
                        .orElseGet(() -> artistService.add(artist)))
                .toList();
    }

    @Nonnull
    @Override
    @Step("Add [{count}] random artists")
    public List<ArtistDTO> addArtists(int count) {
        if (count < 0) throw new IllegalArgumentException("Artists count cannot be negative");
        if (count == 0) return Collections.emptyList();
        return IntStream.range(0, count)
                .mapToObj(i -> {
                    var artist = DataGenerator.generateArtist();
                    return artistService.findByName(artist.getName())
                            .orElseGet(() -> artistService.add(artist));
                })
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
        museum.getLocation().setCountry(country);
        museumService.add(museum);
        museum.getLocation().setCountry(country);
        return museumService.add(museum);
    }

    @Nonnull
    @Override
    @Step("Add museums")
    public List<MuseumDTO> addMuseums(List<MuseumDTO> museums) {
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
        return IntStream.range(0, count)
                .mapToObj(i -> addMuseum(DataGenerator.generateMuseum()))
                .toList();
    }

    @Nonnull
    @Override
    public PaintingDTO addPainting(PaintingDTO painting) {
        var countryCode = painting.getMuseum().getLocation().getCountry().getCode();
        if (countryCode == null || countryCode == CountryCode.EMPTY)
            throw new IllegalArgumentException("Museum country code cannot be null or equals EMPTY");
        var artist = artistService.findByName(painting.getArtist().getName())
                .orElseGet(() -> artistService.add(painting.getArtist()));
        var country = countryService.findByCode(countryCode)
                .orElseThrow(() -> new CountryNotFoundException(countryCode));
        var museum = museumService.findByTitle(painting.getMuseum().getTitle())
                .orElseGet(() -> museumService.add(painting.getMuseum().setCountry(country)));
        painting.setArtist(artist)
                .setMuseum(museum);
        return paintingService.add(painting);
    }

    @Nonnull
    @Override
    @Step("Add paintings")
    public List<PaintingDTO> addPaintings(List<PaintingDTO> paintings) {
        return paintings.stream()
                .map(this::addPainting)
                .toList();
    }

    @Nonnull
    @Override
    @Step("Add [{count}] random paintings")
    public List<PaintingDTO> addPaintings(int count) {
        if (count < 0) throw new IllegalArgumentException("Paintings count cannot be negative");
        if (count == 0) return Collections.emptyList();
        return IntStream.range(0, count)
                .mapToObj(i -> addPainting(DataGenerator.generatePainting()))
                .toList();
    }

    @Nonnull
    @Override
    @Step("Add user")
    public UserDTO addUser(UserDTO user) {
        return userService.create(user);
    }

    @Nonnull
    @Override
    @Step("Add users")
    public List<UserDTO> addUsers(List<UserDTO> users) {
        return users.stream()
                .map(userService::create)
                .toList();
    }

    @Nonnull
    @Override
    @Step("Add [{count}] random users")
    public List<UserDTO> addUsers(int count) {
        if (count < 0) throw new IllegalArgumentException("Users count cannot be negative");
        if (count == 0) return Collections.emptyList();

        return IntStream.range(0, count)
                .mapToObj(i -> userService.create(DataGenerator.generateUser()))
                .toList();
    }

}
