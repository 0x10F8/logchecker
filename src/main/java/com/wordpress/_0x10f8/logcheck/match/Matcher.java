package com.wordpress._0x10f8.logcheck.match;

/**
 * 
 * Functional interface to check whether some generic item matches a lambda (or
 * anonymous inner class)
 * 
 * @param <T> Any type you want to match with the function
 */
public interface Matcher<T> {

	/**
	 * Check if the function matches the item given
	 * 
	 * @param item The item to match
	 * @return boolean true if matches
	 */
	boolean matches(final T item);

}
