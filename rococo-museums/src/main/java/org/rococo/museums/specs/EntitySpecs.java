package org.rococo.museums.specs;

public interface EntitySpecs<D, S> {
    D findByCriteria(S source);
}
