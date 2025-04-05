package org.rococo.files.specs;

public interface EntitySpecs<D, S> {
    D findByCriteria(S source);
}
