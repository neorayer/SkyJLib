/*
 * Created by dnoakes on 25-Nov-2002 20:30:39 using IntelliJ IDEA.
 */
package com.skymiracle.image.metadata;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Base class for all Metadata directory types with supporting methods for
 * setting and getting tag values.
 */
public abstract class Directory implements Serializable {
	/**
	 * Map of values hashed by type identifiers.
	 */
	public final HashMap<Integer, Object> _tagMap;

	/**
	 * The descriptor used to interperet tag values.
	 */
	protected TagDescriptor _descriptor;

	/**
	 * A convenient list holding tag values in the order in which they were
	 * stored. This is used for creation of an iterator, and for counting the
	 * number of defined tags.
	 */
	protected final List<Tag> _definedTagList;

	private List<String> _errorList;

	// ABSTRACT METHODS

	/**
	 * Provides the name of the directory, for display purposes. E.g.
	 * <code>Exif</code>
	 * 
	 * @return the name of the directory
	 */
	public abstract String getName();

	/**
	 * Provides the map of tag names, hashed by tag type identifier.
	 * 
	 * @return the map of tag names
	 */
	protected abstract HashMap<Integer,Object> getTagNameMap();

	// CONSTRUCTORS

	/**
	 * Creates a new Directory.
	 */
	public Directory() {
		this._tagMap = new HashMap<Integer,Object>();
		this._definedTagList = new ArrayList<Tag>();
	}

	// VARIOUS METHODS

	/**
	 * Indicates whether the specified tag type has been set.
	 * 
	 * @param tagType
	 *            the tag type to check for
	 * @return true if a value exists for the specified tag type, false if not
	 */
	public boolean containsTag(int tagType) {
		return this._tagMap.containsKey(new Integer(tagType));
	}

	/**
	 * Returns an Iterator of Tag instances that have been set in this
	 * Directory.
	 * 
	 * @return an Iterator of Tag instances
	 */
	public Iterator<Tag> getTagIterator() {
		return this._definedTagList.iterator();
	}

	/**
	 * Returns the number of tags set in this Directory.
	 * 
	 * @return the number of tags set in this Directory
	 */
	public int getTagCount() {
		return this._definedTagList.size();
	}

	/**
	 * Sets the descriptor used to interperet tag values.
	 * 
	 * @param descriptor
	 *            the descriptor used to interperet tag values
	 */
	public void setDescriptor(TagDescriptor descriptor) {
		if (descriptor == null) {
			throw new NullPointerException("cannot set a null descriptor");
		}
		this._descriptor = descriptor;
	}

	public void addError(String message) {
		if (this._errorList == null) {
			this._errorList = new ArrayList<String>();
		}
		this._errorList.add(message);
	}

	public boolean hasErrors() {
		return (this._errorList != null && this._errorList.size() > 0);
	}

	public Iterator<String> getErrors() {
		return this._errorList.iterator();
	}

	public int getErrorCount() {
		return this._errorList.size();
	}

	// TAG SETTERS

	/**
	 * Sets an int value for the specified tag.
	 * 
	 * @param tagType
	 *            the tag's value as an int
	 * @param value
	 *            the value for the specified tag as an int
	 */
	public void setInt(int tagType, int value) {
		setObject(tagType, new Integer(value));
	}

	/**
	 * Sets a double value for the specified tag.
	 * 
	 * @param tagType
	 *            the tag's value as an int
	 * @param value
	 *            the value for the specified tag as a double
	 */
	public void setDouble(int tagType, double value) {
		setObject(tagType, new Double(value));
	}

	/**
	 * Sets a float value for the specified tag.
	 * 
	 * @param tagType
	 *            the tag's value as an int
	 * @param value
	 *            the value for the specified tag as a float
	 */
	public void setFloat(int tagType, float value) {
		setObject(tagType, new Float(value));
	}

	/**
	 * Sets an int value for the specified tag.
	 * 
	 * @param tagType
	 *            the tag's value as an int
	 * @param value
	 *            the value for the specified tag as a String
	 */
	public void setString(int tagType, String value) {
		setObject(tagType, value);
	}

	/**
	 * Sets an int value for the specified tag.
	 * 
	 * @param tagType
	 *            the tag's value as an int
	 * @param value
	 *            the value for the specified tag as a boolean
	 */
	public void setBoolean(int tagType, boolean value) {
		setObject(tagType, new Boolean(value));
	}

	/**
	 * Sets a long value for the specified tag.
	 * 
	 * @param tagType
	 *            the tag's value as an int
	 * @param value
	 *            the value for the specified tag as a long
	 */
	public void setLong(int tagType, long value) {
		setObject(tagType, new Long(value));
	}

	/**
	 * Sets a java.util.Date value for the specified tag.
	 * 
	 * @param tagType
	 *            the tag's value as an int
	 * @param value
	 *            the value for the specified tag as a java.util.Date
	 */
	public void setDate(int tagType, java.util.Date value) {
		setObject(tagType, value);
	}

	/**
	 * Sets a Rational value for the specified tag.
	 * 
	 * @param tagType
	 *            the tag's value as an int
	 * @param rational
	 *            rational number
	 */
	public void setRational(int tagType, Rational rational) {
		setObject(tagType, rational);
	}

	/**
	 * Sets a Rational array for the specified tag.
	 * 
	 * @param tagType
	 *            the tag identifier
	 * @param rationals
	 *            the Rational array to store
	 */
	public void setRationalArray(int tagType, Rational[] rationals) {
		setObjectArray(tagType, rationals);
	}

	/**
	 * Sets an int array for the specified tag.
	 * 
	 * @param tagType
	 *            the tag identifier
	 * @param ints
	 *            the int array to store
	 */
	public void setIntArray(int tagType, int[] ints) {
		setObjectArray(tagType, ints);
	}

	/**
	 * Sets a byte array for the specified tag.
	 * 
	 * @param tagType
	 *            the tag identifier
	 * @param bytes
	 *            the byte array to store
	 */
	public void setByteArray(int tagType, byte[] bytes) {
		setObjectArray(tagType, bytes);
	}

	/**
	 * Sets a String array for the specified tag.
	 * 
	 * @param tagType
	 *            the tag identifier
	 * @param strings
	 *            the String array to store
	 */
	public void setStringArray(int tagType, String[] strings) {
		setObjectArray(tagType, strings);
	}

	/**
	 * Private helper method, containing common functionality for all 'add'
	 * methods.
	 * 
	 * @param tagType
	 *            the tag's value as an int
	 * @param value
	 *            the value for the specified tag
	 * @throws NullPointerException
	 *             if value is <code>null</code>
	 */
	public void setObject(int tagType, Object value) {
		if (value == null) {
			throw new NullPointerException("cannot set a null object");
		}

		Integer key = new Integer(tagType);
		if (!this._tagMap.containsKey(key)) {
			this._definedTagList.add(new Tag(tagType, this));
		}
		this._tagMap.put(key, value);
	}

	/**
	 * Private helper method, containing common functionality for all
	 * 'add...Array' methods.
	 * 
	 * @param tagType
	 *            the tag's value as an int
	 * @param array
	 *            the array of values for the specified tag
	 */
	public void setObjectArray(int tagType, Object array) {
		// for now, we don't do anything special -- this method might be a
		// candidate for removal once the dust settles
		setObject(tagType, array);
	}

	// TAG GETTERS

	/**
	 * Returns the specified tag's value as an int, if possible.
	 */
	public int getInt(int tagType) throws MetadataException {
		Object o = getObject(tagType);
		if (o == null) {
			throw new MetadataException("Tag " + getTagName(tagType)
					+ " has not been set -- check using containsTag() first");
		} else if (o instanceof String) {
			try {
				return Integer.parseInt((String) o);
			} catch (NumberFormatException nfe) {
				// convert the char array to an int
				String s = (String) o;
				int val = 0;
				for (int i = s.length() - 1; i >= 0; i--) {
					val += s.charAt(i) << (i * 8);
				}
				return val;
			}
		} else if (o instanceof Number) {
			return ((Number) o).intValue();
		}
		throw new MetadataException("Requested tag cannot be cast to int");
	}

	/**
	 * Gets the specified tag's value as a String array, if possible. Only
	 * supported where the tag is set as String[], String, int[], byte[] or
	 * Rational[].
	 * 
	 * @param tagType
	 *            the tag identifier
	 * @return the tag's value as an array of Strings
	 * @throws MetadataException
	 *             if the tag has not been set or cannot be represented as a
	 *             String[]
	 */
	public String[] getStringArray(int tagType) throws MetadataException {
		Object o = getObject(tagType);
		if (o == null) {
			throw new MetadataException("Tag " + getTagName(tagType)
					+ " has not been set -- check using containsTag() first");
		} else if (o instanceof String[]) {
			return (String[]) o;
		} else if (o instanceof String) {
			String[] strings = { (String) o };
			return strings;
		} else if (o instanceof int[]) {
			int[] ints = (int[]) o;
			String[] strings = new String[ints.length];
			for (int i = 0; i < strings.length; i++) {
				strings[i] = Integer.toString(ints[i]);
			}
			return strings;
		} else if (o instanceof byte[]) {
			byte[] bytes = (byte[]) o;
			String[] strings = new String[bytes.length];
			for (int i = 0; i < strings.length; i++) {
				strings[i] = Byte.toString(bytes[i]);
			}
			return strings;
		} else if (o instanceof Rational[]) {
			Rational[] rationals = (Rational[]) o;
			String[] strings = new String[rationals.length];
			for (int i = 0; i < strings.length; i++) {
				strings[i] = rationals[i].toSimpleString(false);
			}
			return strings;
		}
		throw new MetadataException(
				"Requested tag cannot be cast to String array ("
						+ o.getClass().toString() + ")");
	}

	/**
	 * Gets the specified tag's value as an int array, if possible. Only
	 * supported where the tag is set as String, int[], byte[] or Rational[].
	 * 
	 * @param tagType
	 *            the tag identifier
	 * @return the tag's value as an int array
	 * @throws MetadataException
	 *             if the tag has not been set, or cannot be converted to an int
	 *             array
	 */
	public int[] getIntArray(int tagType) throws MetadataException {
		Object o = getObject(tagType);
		if (o == null) {
			throw new MetadataException("Tag " + getTagName(tagType)
					+ " has not been set -- check using containsTag() first");
		} else if (o instanceof Rational[]) {
			Rational[] rationals = (Rational[]) o;
			int[] ints = new int[rationals.length];
			for (int i = 0; i < ints.length; i++) {
				ints[i] = rationals[i].intValue();
			}
			return ints;
		} else if (o instanceof int[]) {
			return (int[]) o;
		} else if (o instanceof byte[]) {
			byte[] bytes = (byte[]) o;
			int[] ints = new int[bytes.length];
			for (int i = 0; i < bytes.length; i++) {
				byte b = bytes[i];
				ints[i] = b;
			}
			return ints;
		} else if (o instanceof String) {
			String str = (String) o;
			int[] ints = new int[str.length()];
			for (int i = 0; i < str.length(); i++) {
				ints[i] = str.charAt(i);
			}
			return ints;
		}
		throw new MetadataException(
				"Requested tag cannot be cast to int array ("
						+ o.getClass().toString() + ")");
	}

	/**
	 * Gets the specified tag's value as an byte array, if possible. Only
	 * supported where the tag is set as String, int[], byte[] or Rational[].
	 * 
	 * @param tagType
	 *            the tag identifier
	 * @return the tag's value as a byte array
	 * @throws MetadataException
	 *             if the tag has not been set, or cannot be converted to a byte
	 *             array
	 */
	public byte[] getByteArray(int tagType) throws MetadataException {
		Object o = getObject(tagType);
		if (o == null) {
			throw new MetadataException("Tag " + getTagName(tagType)
					+ " has not been set -- check using containsTag() first");
		} else if (o instanceof Rational[]) {
			Rational[] rationals = (Rational[]) o;
			byte[] bytes = new byte[rationals.length];
			for (int i = 0; i < bytes.length; i++) {
				bytes[i] = rationals[i].byteValue();
			}
			return bytes;
		} else if (o instanceof byte[]) {
			return (byte[]) o;
		} else if (o instanceof int[]) {
			int[] ints = (int[]) o;
			byte[] bytes = new byte[ints.length];
			for (int i = 0; i < ints.length; i++) {
				bytes[i] = (byte) ints[i];
			}
			return bytes;
		} else if (o instanceof String) {
			String str = (String) o;
			byte[] bytes = new byte[str.length()];
			for (int i = 0; i < str.length(); i++) {
				bytes[i] = (byte) str.charAt(i);
			}
			return bytes;
		}
		throw new MetadataException(
				"Requested tag cannot be cast to byte array ("
						+ o.getClass().toString() + ")");
	}

	/**
	 * Returns the specified tag's value as a double, if possible.
	 */
	public double getDouble(int tagType) throws MetadataException {
		Object o = getObject(tagType);
		if (o == null) {
			throw new MetadataException("Tag " + getTagName(tagType)
					+ " has not been set -- check using containsTag() first");
		} else if (o instanceof String) {
			try {
				return Double.parseDouble((String) o);
			} catch (NumberFormatException nfe) {
				throw new MetadataException("unable to parse string " + o
						+ " as a double", nfe);
			}
		} else if (o instanceof Number) {
			return ((Number) o).doubleValue();
		}
		throw new MetadataException("Requested tag cannot be cast to double");
	}

	/**
	 * Returns the specified tag's value as a float, if possible.
	 */
	public float getFloat(int tagType) throws MetadataException {
		Object o = getObject(tagType);
		if (o == null) {
			throw new MetadataException("Tag " + getTagName(tagType)
					+ " has not been set -- check using containsTag() first");
		} else if (o instanceof String) {
			try {
				return Float.parseFloat((String) o);
			} catch (NumberFormatException nfe) {
				throw new MetadataException("unable to parse string " + o
						+ " as a float", nfe);
			}
		} else if (o instanceof Number) {
			return ((Number) o).floatValue();
		}
		throw new MetadataException("Requested tag cannot be cast to float");
	}

	/**
	 * Returns the specified tag's value as a long, if possible.
	 */
	public long getLong(int tagType) throws MetadataException {
		Object o = getObject(tagType);
		if (o == null) {
			throw new MetadataException("Tag " + getTagName(tagType)
					+ " has not been set -- check using containsTag() first");
		} else if (o instanceof String) {
			try {
				return Long.parseLong((String) o);
			} catch (NumberFormatException nfe) {
				throw new MetadataException("unable to parse string " + o
						+ " as a long", nfe);
			}
		} else if (o instanceof Number) {
			return ((Number) o).longValue();
		}
		throw new MetadataException("Requested tag cannot be cast to long");
	}

	/**
	 * Returns the specified tag's value as a boolean, if possible.
	 */
	public boolean getBoolean(int tagType) throws MetadataException {
		Object o = getObject(tagType);
		if (o == null) {
			throw new MetadataException("Tag " + getTagName(tagType)
					+ " has not been set -- check using containsTag() first");
		} else if (o instanceof Boolean) {
			return ((Boolean) o).booleanValue();
		} else if (o instanceof String) {
			try {
				return Boolean.getBoolean((String) o);
			} catch (NumberFormatException nfe) {
				throw new MetadataException("unable to parse string " + o
						+ " as a boolean", nfe);
			}
		} else if (o instanceof Number) {
			return (((Number) o).doubleValue() != 0);
		}
		throw new MetadataException("Requested tag cannot be cast to boolean");
	}

	/**
	 * Returns the specified tag's value as a java.util.Date, if possible.
	 */
	public java.util.Date getDate(int tagType) throws MetadataException {
		Object o = getObject(tagType);
		if (o == null) {
			throw new MetadataException("Tag " + getTagName(tagType)
					+ " has not been set -- check using containsTag() first");
		} else if (o instanceof java.util.Date) {
			return (java.util.Date) o;
		} else if (o instanceof String) {
			// add new dateformat strings to make this method even smarter
			// so far, this seems to cover all known date strings
			// (for example, AM and PM strings are not supported...)
			String datePatterns[] = { "yyyy:MM:dd HH:mm:ss",
					"yyyy:MM:dd HH:mm", "yyyy-MM-dd HH:mm:ss",
					"yyyy-MM-dd HH:mm" };
			String dateString = (String) o;
			for (int i = 0; i < datePatterns.length; i++) {
				try {
					DateFormat parser = new java.text.SimpleDateFormat(
							datePatterns[i]);
					return parser.parse(dateString);
				} catch (java.text.ParseException ex) {
					// simply try the next pattern
				}
			}
		}
		throw new MetadataException(
				"Requested tag cannot be cast to java.util.Date");
	}

	/**
	 * Returns the specified tag's value as a Rational, if possible.
	 */
	public Rational getRational(int tagType) throws MetadataException {
		Object o = getObject(tagType);
		if (o == null) {
			throw new MetadataException("Tag " + getTagName(tagType)
					+ " has not been set -- check using containsTag() first");
		} else if (o instanceof Rational) {
			return (Rational) o;
		}
		throw new MetadataException("Requested tag cannot be cast to Rational");
	}

	public Rational[] getRationalArray(int tagType) throws MetadataException {
		Object o = getObject(tagType);
		if (o == null) {
			throw new MetadataException("Tag " + getTagName(tagType)
					+ " has not been set -- check using containsTag() first");
		} else if (o instanceof Rational[]) {
			return (Rational[]) o;
		}
		throw new MetadataException(
				"Requested tag cannot be cast to Rational array ("
						+ o.getClass().toString() + ")");
	}

	/**
	 * Returns the specified tag's value as a String. This value is the 'raw'
	 * value. A more presentable decoding of this value may be obtained from the
	 * corresponding Descriptor.
	 * 
	 * @return the String reprensentation of the tag's value, or
	 *         <code>null</code> if the tag hasn't been defined.
	 */
	public String getString(int tagType) {
		Object o = getObject(tagType);
		if (o == null) {
			return null;
		} else if (o instanceof Rational) {
			return ((Rational) o).toSimpleString(true);
		} else if (o.getClass().isArray()) {
			// handle arrays of objects and primitives
			int arrayLength = Array.getLength(o);
			// determine if this is an array of objects i.e. [Lcom.drew.blah
			boolean isObjectArray = o.getClass().toString().startsWith(
					"class [L");
			StringBuffer sbuffer = new StringBuffer();
			for (int i = 0; i < arrayLength; i++) {
				if (i != 0) {
					sbuffer.append(' ');
				}
				if (isObjectArray) {
					sbuffer.append(Array.get(o, i).toString());
				} else {
					sbuffer.append(Array.getInt(o, i));
				}
			}
			return sbuffer.toString();
		} else {
			return o.toString();
		}
	}

	/**
	 * Returns the object hashed for the particular tag type specified, if
	 * available.
	 * 
	 * @param tagType
	 *            the tag type identifier
	 * @return the tag's value as an Object if available, else null
	 */
	public Object getObject(int tagType) {
		return this._tagMap.get(new Integer(tagType));
	}

	// OTHER METHODS

	/**
	 * Returns the name of a specified tag as a String.
	 * 
	 * @param tagType
	 *            the tag type identifier
	 * @return the tag's name as a String
	 */
	public String getTagName(int tagType) {
		Integer key = new Integer(tagType);
		HashMap<Integer,Object> nameMap = getTagNameMap();
		if (!nameMap.containsKey(key)) {
			String hex = Integer.toHexString(tagType);
			while (hex.length() < 4) {
				hex = "0" + hex;
			}
			return "Unknown tag (0x" + hex + ")";
		}
		return (String) nameMap.get(key);
	}

	/**
	 * Provides a description of a tag's value using the descriptor set by
	 * <code>setDescriptor(Descriptor)</code>.
	 * 
	 * @param tagType
	 *            the tag type identifier
	 * @return the tag value's description as a String
	 * @throws MetadataException
	 *             if a descriptor hasn't been set, or if an error occurs during
	 *             calculation of the description within the Descriptor
	 */
	public String getDescription(int tagType) throws MetadataException {
		if (this._descriptor == null) {
			throw new MetadataException(
					"a descriptor must be set using setDescriptor(...) before descriptions can be provided");
		}

		return this._descriptor.getDescription(tagType);
	}
}
