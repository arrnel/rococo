package org.rococo.paintings.specs;

public interface EntitySpecs<D, S> {
    D findByCriteria(S source);
}
