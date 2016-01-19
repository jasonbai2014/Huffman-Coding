import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

/**
 * This is an utility class used to start this program.
 * 
 * @author Qing Bai
 * @version 29 April 2015
 */
public class Main {
	
    /**
     * The start point of this program.
     * 
     * @param theArgs command line arguments, ignored in this program
     */
	public static void main(final String[] theArgs) {
		try {
			final File novel = new File("WarAndPeace.txt");
			final FileReader novelReader = new FileReader(novel);
			final StringBuilder novelString = new StringBuilder();
			int inputChar;
			
			// this reads the novel character by character until it hits the end
			while ((inputChar = novelReader.read()) != -1) {
				novelString.append((char) inputChar);
			}
			
			final long startTime = System.currentTimeMillis();
			final CodingTree codingTree = new CodingTree(novelString.toString());
			final PrintStream outputCodes = new PrintStream(new File("codes.txt"));
			outputCodes.println(codingTree.codes);	
			outputMessage(codingTree.bits, new File("compressed.txt"));
			final long endTime = System.currentTimeMillis();
			
			final long compressedSize = new File("compressed.txt").length();
			final long originalSize = novel.length();
			
			System.out.printf("Running time: %d milliseconds\n", endTime - startTime);
			System.out.printf("Compressed size: %d bytes\n", compressedSize);
			System.out.printf("Original size: %d bytes\n", originalSize);
			System.out.printf("compression ratio (Bytes): %.1f%%\n", compressedSize * 100.0 / originalSize);
         testCodingTree();
			outputCodes.close();
			novelReader.close();
		} catch (FileNotFoundException exception) {
			System.out.println("FILE NOT FOUND");
		} catch (IOException exception) {
			System.out.println("INPUT ERROR");
		}
	}
	
	/**
	 * This outputs the encoded message.
	 * 
	 * @param theEncodedMessage is the encoded message
	 * @param theFile is the output file
	 */
	public static void outputMessage(final List<Byte> theEncodedMessage, final File theFile) {
		try {
			final PrintStream output = new PrintStream(theFile);
			final byte[] result = new byte[theEncodedMessage.size()];
			
			for (int i = 0; i < result.length; i++) {
				result[i] = theEncodedMessage.get(i);
			}
			
			output.write(result, 0, result.length);
			output.close();
		} catch (FileNotFoundException exception) {
			System.out.println("OUTPUT FILE NOT FOUND");
		}		
	}
	
	/**
	 * This tests the methods from the MyPriorityQueue class.
	 */
	public static void testMyPriorityQueue() {
		final MyPriorityQueue<Integer> myQueue = new MyPriorityQueue<Integer>();
		// should be empty
		System.out.println(myQueue);
		System.out.printf("size is: %d\n", myQueue.size());
		System.out.printf("is empty: %s\n", myQueue.isEmpty());
		
		// should has a size of 6 and not be empty
		myQueue.add(18);
		myQueue.add(12);
		myQueue.add(3);
		myQueue.add(66);
		myQueue.add(24);
		myQueue.add(98);
		System.out.println(myQueue);
		System.out.printf("size is: %d\n", myQueue.size());
		System.out.printf("is empty: %s\n", myQueue.isEmpty());
		
		// should poll out all numbers in an increasing order
		while (!myQueue.isEmpty()) {
			if (myQueue.size() != 1) {
				System.out.printf("%d, ", myQueue.poll());
			} else {
				System.out.println(myQueue.poll());
			}
		}
	}
	
	/**
	 * This tests the coding tree class using a literature different from the one
	 * used in the main.
	 */
	public static void testCodingTree() {
		try {
			final File novel = new File("WarAndPeace.txt");
			final FileReader novelReader = new FileReader(novel);
			final StringBuilder novelString = new StringBuilder();
			int inputChar;
			// this turns the novel into one string
			while ((inputChar = novelReader.read()) != -1) {
				novelString.append((char) inputChar);
			}
			
			final String originalNovel = novelString.toString();
			// this encodes the novel
			final CodingTree codingTree = new CodingTree(originalNovel);
			PrintStream output = new PrintStream(new File("test_codes.txt"));
			output.println(codingTree.codes);	
			outputMessage(codingTree.bits, new File("test_compressed.txt"));
			
			// this decodes the novel
			final String decodedNovel = codingTree.decode(codingTree.readBinaryFile("test_compressed.txt"), 
					codingTree.rebuildCodesMap("test_codes.txt"));
			output = new PrintStream(new File("test_decoded.txt"));
			output.println(decodedNovel);
			
			// this checks likeness of the original and decoded novel
			int diffCharNum = 0;
			for (int i = 0; i < originalNovel.length(); i++) {
				if (originalNovel.charAt(i) != decodedNovel.charAt(i)) {
					diffCharNum++;
				}
			}
			// if everything works properly in the CodingTree class, the number should be 0
			System.out.printf("number of different characters: %d\n", diffCharNum);
			
			novelReader.close();
			output.close();
		} catch (IOException e) {
			System.out.println("CANNOT FOUND TEST FILE");
		}
	}
}
