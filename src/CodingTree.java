import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This is a class that implements Huffman's coding algorithm.
 * 
 * @author Qing Bai
 * @version 29 April 2015
 */
public class CodingTree {
	
	/**
	 * This is a map of characters from the message to binary codes created 
	 * by the Huffman tree.
	 */
	public Map<Character, String> codes;
	
	/**
	 * This is the message encoded using the Huffman codes.
	 */
	public List<Byte> bits;
	
	/**
	 * This is the root of this coding tree.
	 */
	private final Node myRoot;
	
	/**
	 * This creates a coding tree with a given string.
	 * 
	 * @param theMessage is the given message
	 */
	public CodingTree(final String theMessage) {
		myRoot = buildTree(countChars(theMessage));
		codes = new HashMap<Character, String>();
		buildCodes(myRoot, "");
		bits = new ArrayList<Byte>();
		encode(theMessage);
	}
	
	/**
	 * This takes the encoded file and the codes used to encode the file to output 
	 * the original text.
	 * 
	 * @param theBits is the encoded text 
	 * @param theCodes is the codes
	 * @return the original text
	 */
	public String decode(final String theBits, final Map<Character, String> theCodes) {
		if (theBits == null || theCodes == null || theBits.isEmpty() || theCodes.isEmpty()) {
			return null;
		}
		
		final Node root = rebuildTree(theCodes);
		final StringBuilder result = new StringBuilder();
		Node currentNode = root;
		char bit;
		
		for (int i = 0; i < theBits.length(); i++) {
			bit = theBits.charAt(i);
			
			if (bit == '0' && isLeaf(currentNode.myLeftChild)) {
				result.append(currentNode.myLeftChild.myChar);
				currentNode = root;
			} else if (bit == '0' && currentNode.myLeftChild != null) {
				currentNode = currentNode.myLeftChild;
			} else if (bit == '1' && isLeaf(currentNode.myRightChild)) {
				result.append(currentNode.myRightChild.myChar);
				currentNode = root;
			} else if (bit == '1' && currentNode.myRightChild != null) {
				currentNode = currentNode.myRightChild;
			} 
		}
		
		return result.toString();
	}
		
	/**
	 * This reads an encoded file and turns it into a string of binary digits.
	 * 
	 * @param thePath is path to the file
	 * @return a string of binary digits
	 */
	public String readBinaryFile(final String thePath) {
		final StringBuilder result = new StringBuilder();

		try {
			final Path path = Paths.get(thePath);
			final byte[] encodedBytes = Files.readAllBytes(path);
			String binaryString;

			for (int i = 0; i < encodedBytes.length; i++) {
				binaryString = Integer.toBinaryString(encodedBytes[i] & 0xFF);
				result.append(String.format("%8s", binaryString).replace(' ', '0'));
			}
			
		} catch (IOException exception) {
			System.out.println("INPUT ERROR");
		}

		return result.toString();
	}
	
	/**
	 * This rebuilds a map of character to its code from a file that contains
	 * encoding codes.
	 * 
	 * @param thePath is the path to the file
	 * @return a map of character to its code
	 */
	public Map<Character, String> rebuildCodesMap(final String thePath) {
		final Map<Character, String> result = new HashMap<Character, String>();
		
		try {
			final Path path = Paths.get(thePath);
			final String input = new String(Files.readAllBytes(path));
			char currentChar, mapKey;
			final StringBuilder mapValue = new StringBuilder();
			
			for (int i = 0; i < input.length(); i++) {
				currentChar = input.charAt(i);
				// avoids problem that can be caused by a character '='
				if (currentChar == '=' && input.charAt(i + 1) != '=') {
					mapKey = input.charAt(i - 1);
					// reads code for a character
					currentChar = input.charAt(++i);
					while (currentChar == '0' || currentChar == '1') {
						mapValue.append(currentChar);
						currentChar = input.charAt(++i);
					}
					
					result.put(mapKey, mapValue.toString());
					mapValue.delete(0, mapValue.length());
				}
			}
		} catch (IOException exception) {
			System.out.println("INPUT ERROR");
		}
		
		return result;
	}
	
	/**
	 * This rebuilds a huffman tree using codes created by huffman tree.
	 * 
	 * @param theCodes is the codes
	 * @return root of rebuilt tree
	 */
	private Node rebuildTree(final Map<Character, String> theCodes) {
		if (theCodes == null || theCodes.isEmpty()) {
			return null;
		}
		// in this rebuilt tree, all frequencies are set to 0
		final Node root = new Node('\0', 0);
		final Iterator<Character> iterator = theCodes.keySet().iterator();
		char character;
		String code;
		Node currentNode;
		
		while (iterator.hasNext()) {
			character = iterator.next();
			code = theCodes.get(character);
			currentNode = root;
			
			for (int i = 0; i < code.length(); i++) {
				if (code.charAt(i) == '0') {
					if (currentNode.myLeftChild == null) {
						currentNode.myLeftChild = new Node('\0', 0);
					}
					
					currentNode = currentNode.myLeftChild;
				} else { // digit is 1, turn right
					if (currentNode.myRightChild == null) {
						currentNode.myRightChild = new Node('\0', 0);
					}
					
					currentNode = currentNode.myRightChild;
				}
			}
			
			currentNode.myChar = character;
		}
		
		return root;
	}	
	
	/**
	 * This encodes a given message using the codes from this coding tree.
	 * 
	 * @param theMessage is the given message
	 */
	private void encode(final String theMessage) {
		if (theMessage == null || theMessage.isEmpty()) {
			return;
		}
		
		final StringBuilder buffer = new StringBuilder();
		short code;
		
		for (int i = 0; i < theMessage.length();) {
			buffer.append(codes.get(theMessage.charAt(i++)));
			
			while (buffer.length() < 8 && i < theMessage.length()) {
				buffer.append(codes.get(theMessage.charAt(i++)));
			}
			
			while (buffer.length() >= 8) {
				code = Short.parseShort(buffer.substring(0, 8), 2);
				bits.add((byte) code);
				buffer.delete(0, 8);
			}
		}
		
		while (buffer.length() != 8) {
			buffer.append('0');
		}
		
		code = Short.parseShort(buffer.substring(0, 8), 2);
		bits.add((byte) code);
	}
	
	/**
	 * This builds a map of character to string that assigns each character
	 * an unique code.
	 * 
	 * @param theRoot is the root of this coding tree
	 * @param theCode is code for a characther
	 */
	private void buildCodes(final Node theRoot, final String theCode) {
		if (theRoot == null) {
			return;
		} else if (isLeaf(theRoot)) {
			codes.put(theRoot.myChar, theCode);
			return;
		}
		
		buildCodes(theRoot.myLeftChild, theCode + "0");
		buildCodes(theRoot.myRightChild, theCode + "1");
	}
	
	/**
	 * This checks whether or not a given node is a leaf of the tree.
	 * 
	 * @param theNode is the node being checked
	 * @return true if the node is a leaf. Otherwise, false.
	 */
	private boolean isLeaf(final Node theNode) {
		return theNode != null && (theNode.myLeftChild == null && theNode.myRightChild == null);
	}
	
	/**
	 * This builds a huffman tree using a given map of character to frequency.
	 * 
	 * @param theFrequency is the map of character to frequency
	 * @return root of the huffman tree
	 */
	private Node buildTree(final Map<Character, Integer> theFrequency) {
		if (theFrequency == null || theFrequency.isEmpty()) {
			return null;
		}
		
		final MyPriorityQueue<Node> roots = new MyPriorityQueue<Node>();
		final Iterator<Character> iterator = theFrequency.keySet().iterator();
		char key;
		Node leftChild, rightChild, root;
		
		while (iterator.hasNext()) {
			key = iterator.next();
			roots.add(new Node(key, theFrequency.get(key)));
		}
		
		while (roots.size() != 1) {
			leftChild = roots.poll();
			rightChild = roots.poll();
			root = new Node('\0', leftChild.myFrequency + rightChild.myFrequency);
			root.myLeftChild = leftChild;
			root.myRightChild = rightChild;
			roots.add(root);
		}
		
		return roots.poll();
	}
	
	
	/**
	 * This counts frequency of each character that presents in the given string.
	 * 
	 * @param theMessage is the given string
	 * @return a map of character to its frequency
	 */
	private Map<Character, Integer> countChars(final String theMessage) {
		if (theMessage == null) {
			return null;
		}
		
		final Map<Character, Integer> result = new HashMap<Character, Integer>();
		char inputChar;
		//this processes the string character by character
		for (int i = 0; i < theMessage.length(); i++) {
			inputChar = theMessage.charAt(i);
			
			if (result.containsKey(inputChar)) {
				result.put(inputChar, result.get(inputChar) + 1);
			} else {
				result.put(inputChar, 1);
			}
		}
		
		return result;
	}
	
	/**
	 * This is a private class used to create nodes in the tree.
	 */
	private class Node implements Comparable<Node> {
		
		/**
		 * This stores a char element in this node.
		 */
		public char myChar;
		
		/**
		 * This stores frequency of the char.
		 */
		public int myFrequency;
		
		/**
		 * This is left child of the node.
		 */
		public Node myLeftChild;
		
		/**
		 * This is right child of the node.
		 */
		public Node myRightChild;
		
		/**
		 * This creates a node with a given char and its frequency.
		 * 
		 * @param theChar is the given char
		 * @param theFrequency is the frequency
		 */
		public Node(final char theChar, final int theFrequency) {
			myChar = theChar;
			myFrequency = theFrequency;
			myLeftChild = null;
			myRightChild = null;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compareTo(final Node theOther) {
			if (theOther == null) {
				throw new IllegalArgumentException();
			}
			
			return myFrequency - theOther.myFrequency;
		}
	}
}
