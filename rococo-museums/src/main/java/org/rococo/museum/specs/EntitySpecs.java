package org.rococo.museum.specs;

public interface EntitySpecs<D, S> {
    D findByCriteria(S source);
}
