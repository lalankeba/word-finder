package com.laan.wordfinder.util;

import java.util.*;

public class Trie {

    private final Node root;

    public Trie() {
        root = new Node();
    }

    public void insert(String word) {
        Node current = root;
        for (char ch : word.toCharArray()) {
            current.children.putIfAbsent(ch, new Node());
            current = current.children.get(ch);
        }
        current.isWord = true;
        current.frequency++;
    }

    public Map<String, Integer> getTopFrequentWords(int k) {
        PriorityQueue<Map.Entry<String, Integer>> maxHeap =
                new PriorityQueue<>(Comparator.comparingInt(Map.Entry::getValue));

        collectWordsAndFrequencies(root, "", maxHeap, k);

        List<Map.Entry<String, Integer>> resultList = new ArrayList<>(maxHeap);
        resultList.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        Map<String, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : resultList) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    private void collectWordsAndFrequencies(Node node, String currentWord,
                                            PriorityQueue<Map.Entry<String, Integer>> maxHeap, int k) {
        if (node.isWord) {
            maxHeap.offer(new AbstractMap.SimpleEntry<>(currentWord, node.frequency));
            if (maxHeap.size() > k) {
                maxHeap.poll(); // Remove the least frequent word if the heap size exceeds k
            }
        }

        for (Map.Entry<Character, Node> childEntry : node.children.entrySet()) {
            collectWordsAndFrequencies(childEntry.getValue(), currentWord + childEntry.getKey(), maxHeap, k);
        }
    }

    private static class Node {

        private final Map<Character, Node> children;
        private Boolean isWord;
        private Integer frequency;

        public Node() {
            children = new HashMap<>();
            isWord = false;
            frequency = 0;
        }

    }
}
