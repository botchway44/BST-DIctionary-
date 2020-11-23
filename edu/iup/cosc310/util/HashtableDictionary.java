package edu.iup.cosc310.util;

import java.util.ArrayList;
import java.util.Iterator;
/**
 * An implementation of a Hashtable using linear probbing
 * 
 * @author Amma Darkwah
 *
 * @param <K> Data type for the keys
 * @param <V> Data type for the values
 */
public class HashtableDictionary<K, V> implements Dictionary<K, V> {

	/**
	 * An Input object stores the value and the key of the
	 * object given by the user
	 */
	private class InputObject<K, V>  {
		public K key;
		public V value;

		/**
		 *  Constructs an Input object
		 * @param key
		 * @param value
		 */
		public InputObject(K key, V value) {
			this.key = key;
			this.value = value;
		}
		
		
		//compares two input objects
		public boolean compareTo(InputObject<K,V> object) {
			
			int res = ((Comparable)this.key).compareTo(object.key);
			if(res == 0) return true;
			
			return false;
		}
	}

	/**
	 * Sets the initial size of the Hashtable
	 */
	private  int SIZE = 11;
	
	/** Keeps track of the number of keys in the hashtable*/
	private int numKeys = 0;
	
	
	private InputObject<K, V>[] hashTable = new InputObject[SIZE];


	/**
	 * Creates a hashed value of the key and rounds it by the 
	 * mod of the input
	 * @param key
	 * @return the generated hash key
	 */
	private int hashKey(K key) {

		
		// find hashed value
		int hashed = key.hashCode() % this.SIZE;

		// if hascode is negative, convert to positive
		if (hashed < 0)
			hashed = -1 * hashed;
		
		return hashed;
	}
	
	/**
	 * Put a key together with its associated value into the hashtable. If the key
	 * already exists then the new value replaces the current value associated with
	 * the key. Values can be retrieved using the get method.
	 * 
	 * @param key   the key
	 * @param value the new value
	 * @return the original value if the key already exists in the dictionary,
	 *         otherwise null.
	 */
	@Override
	public V put(K key, V value) {

		if(this.isLoadFactorReached()) this.rehashTable();
		
		InputObject<K, V> node = new InputObject<K,V>(key, value);

		// find hashed value
		int hashed = this.hashKey(key);
		
		// find available slot at or from hashed location
		this.insertNextAvailableSlot(hashed, node);


		return node.value;
	}

	/**
	 * Finds the next available slot to perform insertion
	 * @param index - the index position to be inserted
	 * @param input - the value to be inserted
	 */
	private void insertNextAvailableSlot(int index, InputObject<K, V> input) {
		if (this.hashTable[index] == null) {
			
			this.hashTable[index] = input;

			this.numKeys++;
		}
		
		else if(this.hashTable[index].compareTo(input)) {
			this.hashTable[index] = input;
		}
		else {
			int nextIndex = (index + 1) % this.SIZE;

			insertNextAvailableSlot(nextIndex, input);
		}
	}



	/**
	 * Get the current value associated with a given key.
	 * 
	 * @param key the key
	 * @return the current value associated with the key in the dictionary if found,
	 *         otherwise null.
	 */
	@Override
	public V get(K key) {
		int hashed = this.hashKey(key);
		InputObject<K,V> val = getValue(key, hashed);
		
		if(val == null) return null;
		
		return val.value;
	}

	/**
	 * Gets the Position or Value of Input Object in the hashTable
	 * @param key
	 * @param startingPosition
	 * @return the Object if found or null
	 */
	private InputObject<K,V> getValue(K key,int startingPosition ){
		
		int counter = startingPosition;
		while(this.hashTable[counter] != null) {
			int res = ((Comparable)this.hashTable[counter].key).compareTo(key);
			
			if(res == 0) return this.hashTable[counter];
			
			counter= (counter +1) % this.SIZE;
		}
		
		return null;
	}

	
	/**
	 * An Iterator Class For the Hashtable
	 * @param <K>
	 * @param <V>
	 */
	class DictionaryIterator<K, V> implements Iterator<K> {

		private ArrayList<K> keys;
		private int count = 0;

		public DictionaryIterator(InputObject<K, V>[] inputs, int numOfKeys) {

			this.keys = new ArrayList<>();
			this.getAllKeys(inputs);
			this.count = 0;

		}

		/**
		 * Checks if the iterator has a next value
		 * 
		 * @return boolean
		 */
		@Override
		public boolean hasNext() {

			if (count < keys.size())
				return true;
			
			
			return false;
		}

		/**
		 * Returns the Next Key
		 * 
		 * @return K
		 */
		@Override
		public K next() {

			return (K) this.keys.get(count++);
		}


		
		/**
		 * Gets all the keys into a List for the iterator
		 * 
		 * @param current
		 */
		public void getAllKeys(InputObject<K, V>[] inputs) {
			int checkIndex = 0;
			
			for(int i=0; i< inputs.length; i++) {
				if(inputs[i] != null) {

					this.keys.add(inputs[i].key);
				} 
				
			}
			
		}
	}

	/**
	 * Create an Iterator to iterate over the keys of the dictionary.
	 * 
	 * @return an Iterator to iterator over the keys.
	 */
	@Override
	public Iterator<K> keys() {
		return new DictionaryIterator<K, V>(this.hashTable, this.numKeys);
	}

	
	/**
	 * Test if the dictionary is empty
	 * 
	 * @return true if the dictionary is empty, otherwise false
	 */
	@Override
	public boolean isEmpty() {

		return this.numKeys <=0;
	}

	/**
	 * Get the number of keys in the hash table
	 * 
	 * @return the number of keys in the dictionary
	 */
	@Override
	public int noKeys() {
		return this.numKeys;
	}



	@Override
	public V remove(K key) {

		int hashed = this.hashKey(key);
		
		//find the value associated
		InputObject<K,V> val = findPositionAndRemove(key, hashed);
		
		if(val != null) return val.value;
		
	return null;
	}

	private InputObject<K,V> findPositionAndRemove(K key,int startingPosition ){
		
		int counter = startingPosition;
		while(this.hashTable[counter] != null) {
			int res = ((Comparable)this.hashTable[counter].key).compareTo(key);
			
			if(res == 0) {
				//hold the value temporarily
				InputObject<K,V> temp =  this.hashTable[counter];
				
				//set the position in the hashtable to null
				this.hashTable[counter] = null;
				
				//reduce the size of the hashtable
				this.numKeys--;
				
				//return the temp
				return temp;
			}
			
			counter= (counter +1) % this.SIZE;
		}
		
		return null;
	}

	
	/**
	 * Check if load factor of 0.5 is reached
	 * @return
	 */
	private boolean isLoadFactorReached() {
		float lf =(float) this.numKeys / this.SIZE;
		if(lf > 0.5) return true;
		return false;
	}
	
	//rehash table when load factor is 50%
	public void rehashTable(){
		
		 int newSize = this.SIZE * 2;
		 InputObject<K, V>[] newTable = new InputObject[newSize];
		 
		 Iterator<K> iter = this.keys();
			
		 while(iter.hasNext()) {
			K key = iter.next();
			V value = this.get(key);
			
			int hashed = key.hashCode() % newSize;
			
			if(hashed < 0) hashed =  -1 * hashed;
			
			while(true) {
				if(newTable[hashed] == null) {
					//insert here
					newTable[hashed] = new InputObject(key,value);
					break;
				}
				hashed = (hashed + 1) % newSize;
			}
		 }
		
		 //set new value to hashtable
		 this.hashTable = newTable;
		 this.SIZE = newSize;
		 
	}
	
}
