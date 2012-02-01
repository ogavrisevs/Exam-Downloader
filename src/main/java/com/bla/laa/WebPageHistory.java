package com.bla.laa;

import com.bla.laa.Common.MyCustException;

import java.util.HashSet;
import java.util.Set;

public class WebPageHistory {

    private final Set<String> visitedPages = new HashSet<String>();

    public boolean addPage(String pageHash) throws MyCustException {
        if (!this.visitedPages.add(pageHash))
            throw new MyCustException("Page all ready visited !");

        return this.visitedPages.add(pageHash);
    }

}
