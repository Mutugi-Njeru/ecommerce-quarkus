package org.jacpower.ruleEngine.interfaces;

public interface IRule <I>{
    boolean matches(I module);
    Object apply(I request);
}
