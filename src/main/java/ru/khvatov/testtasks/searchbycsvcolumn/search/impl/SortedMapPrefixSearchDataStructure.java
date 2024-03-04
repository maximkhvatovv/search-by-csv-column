package ru.khvatov.testtasks.searchbycsvcolumn.search.impl;

import ru.khvatov.testtasks.searchbycsvcolumn.search.PrefixSearchDataStructure;
import ru.khvatov.testtasks.searchbycsvcolumn.sort.WordsSorter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public class SortedMapPrefixSearchDataStructure implements PrefixSearchDataStructure {
    private final SortedMap<String, List<Integer>> wordToIds;
    private final Function<String, String> generalizeFunc;

    public SortedMapPrefixSearchDataStructure() {
        generalizeFunc = String::toLowerCase;
        wordToIds = new TreeMap<>();
    }

    @Override
    public void add(final String word, final Integer rowId) {
        final String generalizedWord = generalizeFunc.apply(word);

        if (wordToIds.containsKey(generalizedWord)) {
            wordToIds.get(generalizedWord).add(rowId);
        } else {
            final List<Integer> ids = new ArrayList<>();
            ids.add(rowId);
            wordToIds.put(generalizedWord, ids);
        }
    }

    @Override
    public List<Integer> findRowIdsByPrefix(final String prefix, final WordsSorter wordsSorter) {
        final String generalizedPrefix = generalizeFunc.apply(prefix);
        final List<String> result = new ArrayList<>(
                wordToIds.subMap(generalizedPrefix, generalizedPrefix + Character.MAX_VALUE).keySet()
        );
        return wordsSorter.sort(result)
                .stream()
                .map(wordToIds::get)
                .flatMap(Collection::stream)
                .collect(toList());
    }

    @Override
    public String toString() {
        return "SortedMapPrefixSearchDataStructure{" +
                "wordToIds=" + wordToIds +
                '}';
    }
}
