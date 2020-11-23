package edu.iup.cosc310.wordcount;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import edu.iup.cosc310.util.HashtableDictionary;

/**
 * Word Counter Program that uses the HashtableDictionary to find unique words in a page
 * of a document
 * 
 * @author Amma Darkwah
 *
 */
public class WordCounter {

	private static HashtableDictionary<String, Set<Integer>> dictionary = new HashtableDictionary<>();

	/**
	 * Program Entry Point
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		loadFile();

		Iterator<String> iter = dictionary.keys();
		
		HashMap<Integer,TreeSet<String>> pages = new HashMap<>();
		
		//iterate over the keys 
		while (iter.hasNext()) {
			String key = iter.next();

			TreeSet<Integer> temp =(TreeSet<Integer>) dictionary.get(key);
			
			for(Integer page : temp) {
				if(pages.get(page) != null) {
					TreeSet<String> ls = pages.get(page);
					ls.add(key);
					pages.put(page, ls);
				}else {
					TreeSet<String> ls = new TreeSet<String>();
					ls.add(key);
					pages.put(page, ls);
				}
			}
		}
		
		
		//print each page and  number of keywords
		for(Integer key : pages.keySet()) {
			System.out.println("--------------------------------------------------");
			System.out.println("Page " + key);
			System.out.println("--------------------------------------------------");
			TreeSet<String> keywords = pages.get(key);
			for(String word : keywords) {
				System.out.println(word);
			}
			
			System.out.println();
			System.out.println();
		}
	}

	/**
	 * Load all words from the File
	 */
	private static void loadFile() {
		try {
			int page_num = 1;
			int line_counters = 0;
			
			Scanner scan = new Scanner(new File("COSC 310 Syllabus.txt"));
			while (scan.hasNextLine()) {
				String[] line = scan.nextLine().split("\\W+");
				
				if(line_counters == 50) { 
					line_counters = 0;
					page_num++;
				}
				
				line_counters++;
				unpackList(line,page_num);
				
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Unpack a list of words and put them into the dictionary
	 * 
	 * @param list
	 */
	private static void unpackList(String[] list, int pageNum) {
		int len = list.length;

		for (int i = 0; i < len; i++) {
			
			TreeSet<Integer> temp = (TreeSet<Integer>) dictionary.get(list[i].toLowerCase());
			if(temp != null) {
				
				temp.add(pageNum);
				dictionary.put(list[i].toLowerCase(), temp);

			}else {
				TreeSet<Integer> pages = new TreeSet<Integer>();
				pages.add(pageNum);
				dictionary.put(list[i].toLowerCase(), pages);

			}
				       
		}
	}
}
