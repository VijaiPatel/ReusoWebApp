package com.brunel.group19.Reuso.service;

import static me.xdrop.fuzzywuzzy.FuzzySearch.extractTop;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.brunel.group19.Reuso.model.Tag;
import com.brunel.group19.Reuso.repository.TagRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.xdrop.fuzzywuzzy.ToStringFunction;
import me.xdrop.fuzzywuzzy.model.BoundExtractedResult;

@Service
public class TagService {

    @Autowired
    TagRepository tagRepository;

    private String sanitise(String str) {
        return str.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
    }

    /**
     * Returns a set of tags whereby the input set of tags are ensured to match tags
     * which are already present in the database. Any tags which are unique, and are
     * not already present, are persisted into the SQL database. All tags are
     * reproduced to contain only alphanumeric characters (no whitespace).
     * 
     * @param tags The set of tags to check against the database
     * @return A set of unique tags, all present in the database
     */
    public Set<Tag> standardise(Set<Tag> tags) {
        for (Tag tag : tags) {
            tag.setName(sanitise(tag.getName()));
            Tag check = tagRepository.findByName(tag.getName());
            if (check == null)
                tag = tagRepository.save(tag);
        }
        return tags;
    }

    /**
     * Converts a set of tags into a set of string.
     * 
     * @param tags Set of tags to convert to strings
     * @return Set of tag strings
     */
    public Set<String> toStringSet(Set<Tag> tags) {
        Set<String> newStrings = new HashSet<>();
        for (Tag tag : tags)
            newStrings.add(tag.getName());
        return newStrings;
    }

    /**
     * Converts a set of strings into a set of distict tags (no duplicates). This
     * method also implements the standardise method from the same class.
     * 
     * @param tags List of tags to convert to a set
     * @return Set of tag objects
     */
    public Set<Tag> toSet(Set<String> tags) {
        Set<Tag> newTagSet = new HashSet<>();
        for (String tag : tags) {
            newTagSet.add(new Tag(tag));
        }
        return standardise(newTagSet);
    }

    private ToStringFunction<Tag> func = new ToStringFunction<Tag>() {
        public String apply(Tag t) {
            return t.getName();
        }
    };

    /**
     * Performs a fuzzy search of all tags currently in the database.
     * 
     * @param query string
     * @return Set of up to 5 strings
     */
    public Set<String> fuzzSearch(String query) {
        List<BoundExtractedResult<Tag>> resList = extractTop(
            query, 
            tagRepository.findAll(), 
            func,
            5, 70);
        
        Set<String> resStr = new HashSet<>();
        resList.forEach(t -> {
            resStr.add(t.getString());
        });
        return resStr;
    }

    public Set<String> fuzzSearch(String[] query) {
        Set<String> newSet = new HashSet<>();
        for(String q : query) {
            newSet.addAll(fuzzSearch(q));
        }
        return newSet;
    }
}