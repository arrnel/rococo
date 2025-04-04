package org.rococo.artists.specs;

public interface EntitySpecs<D, S> {
    D findByCriteria(S source);
}
