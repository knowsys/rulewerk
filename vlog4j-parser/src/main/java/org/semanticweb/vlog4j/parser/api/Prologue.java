package org.semanticweb.vlog4j.parser.api;

import org.semanticweb.vlog4j.parser.implementation.PrologueException;

public interface Prologue {

    String getBase() throws PrologueException;

    void setBase(String base) throws PrologueException;

    String getPrefix(String prefix) throws PrologueException;

    void setPrefix(String prefix, String iri) throws PrologueException;

    String resolvePName(String prefixedName) throws PrologueException;

}
