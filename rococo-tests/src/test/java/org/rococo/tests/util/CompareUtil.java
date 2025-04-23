package org.rococo.tests.util;

import lombok.extern.slf4j.Slf4j;
import org.rococo.tests.model.*;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Comparator;

@Slf4j
@ParametersAreNonnullByDefault
public class CompareUtil {

    private CompareUtil() {
    }

    private static Comparator<CountryDTO> getCountryComparator() {
        return Comparator
                .comparing(CountryDTO::getId)
                .thenComparing(CountryDTO::getName)
                .thenComparing(CountryDTO::getCode);
    }

    private static Comparator<LocationDTO> getLocationComparator() {
        return Comparator
                .comparing(LocationDTO::getCity)
                .thenComparing(LocationDTO::getCountry, getCountryComparator());
    }

    private static Comparator<MuseumDTO> getMuseumComparator(boolean comparePhoto) {
        Comparator<MuseumDTO> comparator = Comparator
                .comparing(MuseumDTO::getId)
                .thenComparing(MuseumDTO::getTitle)
                .thenComparing(MuseumDTO::getDescription)
                .thenComparing(MuseumDTO::getLocation, getLocationComparator());

        if (comparePhoto) {
            comparator = comparator.thenComparing(MuseumDTO::getPhoto);
        }

        return comparator;
    }

    private static Comparator<ArtistDTO> getArtistComparator(boolean comparePhoto) {
        Comparator<ArtistDTO> comparator = Comparator
                .comparing(ArtistDTO::getId)
                .thenComparing(ArtistDTO::getName)
                .thenComparing(ArtistDTO::getBiography);

        if (comparePhoto) {
            comparator = comparator.thenComparing(ArtistDTO::getPhoto);
        }

        return comparator;
    }

    private static Comparator<PaintingDTO> getPaintingComparator(boolean comparePhoto) {
        Comparator<PaintingDTO> comparator = Comparator
                .comparing(PaintingDTO::getId)
                .thenComparing(PaintingDTO::getTitle)
                .thenComparing(PaintingDTO::getDescription)
                .thenComparing(PaintingDTO::getArtist, getArtistComparator(false))
                .thenComparing(PaintingDTO::getMuseum, getMuseumComparator(false));

        if (comparePhoto) {
            comparator = comparator.thenComparing(PaintingDTO::getPhoto);
        }

        return comparator;
    }

    private static Comparator<UserDTO> getUserComparator(boolean comparePhoto) {
        Comparator<UserDTO> comparator = Comparator
                .comparing(UserDTO::getId)
                .thenComparing(UserDTO::getUsername)
                .thenComparing(UserDTO::getFirstName)
                .thenComparing(UserDTO::getLastName);

        if (comparePhoto) {
            comparator = comparator.thenComparing(UserDTO::getPhoto);
        }

        return comparator;
    }

    public static boolean containsMuseums(Collection<MuseumDTO> expected, Collection<MuseumDTO> actual, boolean comparePhotos) {
        final var missingElements = expected.stream()
                .filter(exp -> actual.stream()
                        .noneMatch(act ->
                                getMuseumComparator(comparePhotos)
                                        .compare(exp, act) == 0))
                .toList();
        if (!missingElements.isEmpty())
            log.error("Actual museums collection not contains: \n{}\nExpected collection: {}\nActual collection: {}",
                    missingElements, expected, actual);
        return missingElements.isEmpty();
    }

    public static boolean containsArtists(Collection<ArtistDTO> expected, Collection<ArtistDTO> actual, boolean comparePhotos) {
        final var missingElements = expected.stream()
                .filter(exp -> actual.stream()
                        .noneMatch(act ->
                                getArtistComparator(comparePhotos)
                                        .compare(exp, act) == 0))
                .toList();
        if (!missingElements.isEmpty())
            log.error("Actual artists collection not contains: \n{}\nExpected collection: {}\nActual collection: {}",
                    missingElements, expected, actual);
        return missingElements.isEmpty();
    }

    public static boolean containsPaintings(Collection<PaintingDTO> expected, Collection<PaintingDTO> actual, boolean comparePhotos) {
        final var missingElements = expected.stream()
                .filter(exp -> actual.stream()
                        .noneMatch(act ->
                                getPaintingComparator(comparePhotos)
                                        .compare(exp, act) == 0))
                .toList();
        if (!missingElements.isEmpty())
            log.error("Actual paintings collection not contains: \n{}\nExpected collection: {}\nActual collection: {}",
                    missingElements, expected, actual);
        return missingElements.isEmpty();
    }

    public static boolean containsUsers(Collection<UserDTO> expected, Collection<UserDTO> actual, boolean comparePhotos) {
        final var missingElements = expected.stream()
                .filter(exp -> actual.stream()
                        .noneMatch(act ->
                                getUserComparator(comparePhotos)
                                        .compare(exp, act) == 0))
                .toList();
        if (!missingElements.isEmpty())
            log.error("Actual users collection not contains: \n{}\nExpected collection: {}\nActual collection: {}",
                    missingElements, expected, actual);
        return missingElements.isEmpty();
    }

}
