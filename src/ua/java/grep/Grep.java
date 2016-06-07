// grep analogue utility 
// Enter the path to your file and words(or regular expressions) that you want to find in this file

package ua.java.grep;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Grep {

	public String[] read(String filePath) {
		try (BufferedReader reader = new BufferedReader(
				new FileReader(filePath))) {
			String s;
			StringBuilder builder = new StringBuilder();
			while ((s = reader.readLine()) != null) {
				builder.append(s + "\n");
			}
			return builder.toString().split("\n");
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (IOException e) {
			System.out.println("I/O exception");
		}
		throw new NullPointerException();
	}

	public String find(String text, String key, boolean ignoreCase) {
		if (!ignoreCase) {
			if (text.contains(key))
				return text;
		} else {
			if (text.toLowerCase().contains(key.toLowerCase()))
				return text;
		}
		return null;
	}

	public String find(String text, Pattern pattern) {
		Matcher matcher = pattern.matcher(text);
		if (matcher.find())
			return text;
		return null;
	}

	private static void usage() {
		System.err
				.println("Usage: Grep [-i option] [-e reg_exp | -w key_word] [-f file_path]");
		System.err
				.println("There can be zero or one option. One or more key words or regular expressions. File path must be only one");
		System.err
				.println("       -h[elp]  = Print a usage message briefly summarizing the command-line options, then exit.");
		System.err.println("       -i[gnore-case] = Ignore case distinctions.");
		System.err
				.println("       -e[pattern] = Enter the regular expresion with which the search will perfomed in the file.");
		System.err
				.println("       -w[ord] = Enter the regular expresion with which the search will perfomed in the file.");
		System.err
				.println("       -f[ile path] = Enter the path to the file in which the search will be carried out.");
		System.exit(1);
	}

	public static void main(String[] args) throws IOException {

		String path = null;
		String[] patterns = null;
		String[] keys = null;
		boolean ignoreCase = false;

		for (int i = 0, j = 0; i < args.length; i++) {
			if (args[i].equals("-h")) {
				usage();
			} else if (args[i].equals("-e")) {
				if (patterns == null)
					patterns = new String[args.length];
			} else if (args[i].equals("-w")) {
				if (keys == null)
					keys = new String[args.length];
				keys[j++] = args[i + 1];
			} else if (args[i].equals("-i")) {
				ignoreCase = true;
			} else if (args[i].equals("-f")) {
				path = args[i + 1];
			}
		}

		if (path != null && (keys != null ^ patterns != null)) {
			Set<String> result = new LinkedHashSet<String>();
			Grep grep = new Grep();
			String[] text = grep.read(path);
			String s;
			if (keys != null) {
				for (String str : text) {
					for (String key : keys) {
						if ((s = grep.find(str, key, ignoreCase)) != null)
							result.add(str);
						break;
					}
				}
			}
			if (patterns != null) {
				List<Pattern> lp = new ArrayList<Pattern>();
				if (ignoreCase) {
					for (String pattern : patterns) {
						lp.add(Pattern.compile(pattern,
								Pattern.CASE_INSENSITIVE));
					}
				} else {
					for (String pattern : patterns)
						lp.add(Pattern.compile(pattern));

				}
				for (String str : text) {
					for (Pattern pattern : lp) {
						if ((s = grep.find(str, pattern)) != null)
							result.add(s);
						break;
					}
				}
			}
			for (String res : result) {
				System.out.println(res);
			}
		} else
			usage();
	}
}
