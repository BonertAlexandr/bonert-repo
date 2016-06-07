package ua.java.sort;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

public class FileSorter {

	public static final Comparator<String> COMPARE_BY_CHAR_AMOUNT = new Comparator<String>() {
		@Override
		public int compare(String o1, String o2) {
			int length1 = 0, length2 = 0;
			for (int i = 0; i < o1.length(); i++) {
				if (Character.isLetter(o1.charAt(i)))
					length1++;
			}
			for (int i = 0; i < o2.length(); i++) {
				if (Character.isLetter(o2.charAt(i)))
					length2++;
			}
			if (length1 > length2)
				return 1;
			if (length1 < length2)
				return -1;
			return 0;
		}
	};

	public String[] read(String path) {
		try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
			StringBuilder textStore = new StringBuilder();
			String s = null;
			while ((s = reader.readLine()) != null) {
				textStore.append(s + "\n");
			}
			return textStore.toString().split("\n");
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (IOException e) {
			System.out.println("I/O exception");
		}
		throw new NullPointerException();
	}

	public void sort(String[] strs) {
		Arrays.sort(strs, String.CASE_INSENSITIVE_ORDER);
	}

	public void sort(String[] strs, Comparator<String> c) {
		Arrays.sort(strs, c);
	}

	public void sort(String[] strs, int index) {
		Arrays.sort(strs, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return o1.split(" ")[index].compareToIgnoreCase(o2.split(" ")[index]);
			}
		});
	}

	public void show(String[] strs) {
		for (String str : strs)
			System.out.println(str);
	}

	private static void usage() {
		System.err.println("Usage: FileSorter [-i index | -l] [-p path]");
		System.err
				.println("       -i[ndex]  = Sort the text by the index of word in line.");
		System.err
				.println("       -l[ength] = Sort the text by characters amount in line.");
		System.err
				.println("       -f[ile path] = Enter the file path wich will be sorted.");
		System.exit(1);
	}

	public static class Checker {
		public static boolean isInteger(String str) {
			if (str == null)
				return false;
			int length = str.length();
			if (length == 0)
				return false;
			int i = 0;
			if (str.charAt(0) == '-') {
				if (length == 1)
					return false;
				i = 1;
			}
			for (; i < length; i++) {
				char c = str.charAt(i);
				if ((c < '0') || (c > '9')) {
					return false;
				}
			}
			return true;
		}
	}

	public static void main(String[] args) {
		String path = null;
		boolean charCnt = false, wordIndex = false;
		int k = 0;

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-p")) {
				path = args[i + 1];
			} else if (args[i].equals("-l")) {
				charCnt = true;
			} else if (args[i].equals("-i")) {
				if (FileSorter.Checker.isInteger(args[i + 1])) {
					wordIndex = true;
					k = Integer.parseInt(args[i + 1]);
				}
			}
		}

		if (path != null) {
			FileSorter sorter = new FileSorter();
			String[] text = sorter.read(path);
			if (!charCnt && !wordIndex) {
				sorter.sort(text);
			} else if (charCnt)
				sorter.sort(text, FileSorter.COMPARE_BY_CHAR_AMOUNT);
			else if (wordIndex)
				sorter.sort(text, k);
			sorter.show(text);
		} else
			usage();

	}
}
