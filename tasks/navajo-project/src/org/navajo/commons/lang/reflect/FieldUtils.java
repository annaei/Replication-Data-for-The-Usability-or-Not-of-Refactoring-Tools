/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.navajo.commons.lang.reflect;

import org.navajo.commons.lang.ClassUtils;
import org.navajo.commons.lang.StringUtils;
import org.navajo.commons.lang.Validate;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Utilities for working with {@link Field}s by reflection. Adapted and refactored from the dormant [reflect] Commons
 * sandbox component.
 * <p>
 * The ability is provided to break the scoping restrictions coded by the programmer. This can allow fields to be
 * changed that shouldn't be. This facility should be used with care.
 * 
 * @since 2.5
 */
public class FieldUtils {

	/**
	 * {@link FieldUtils} instances should NOT be constructed in standard programming.
	 * <p>
	 * This constructor is {@code public} to permit tools that require a JavaBean instance to operate.
	 * </p>
	 */
	public FieldUtils() {
		super();
	}

	/**
	 * Gets an accessible {@link Field} by name respecting scope. Superclasses/interfaces will be considered.
	 * 
	 * @param cls
	 *            the {@link Class} to reflect, must not be {@code null}
	 * @param fieldName
	 *            the field name to obtain
	 * @return the Field object
	 * @throws IllegalArgumentException
	 *             if the class is {@code null}, or the field name is blank or empty
	 */
	public static Field getField(final Class<?> cls, final String fieldName) {
		final Field field = getField(cls, fieldName, false);
		return field;
	}

	/**
	 * Gets an accessible {@link Field} by name, breaking scope if requested. Superclasses/interfaces will be
	 * considered.
	 * 
	 * @param cls
	 *            the {@link Class} to reflect, must not be {@code null}
	 * @param fieldName
	 *            the field name to obtain
	 * @param forceAccess
	 *            whether to break scope restrictions using the
	 *            {@link java.lang.reflect.AccessibleObject#setAccessible(boolean)} method. {@code false} will only
	 *            match {@code public} fields.
	 * @return the Field object
	 * @throws IllegalArgumentException
	 *             if the class is {@code null}, or the field name is blank or empty or is matched at multiple places
	 *             in the inheritance hierarchy
	 */
	public static Field getField(final Class<?> cls, final String fieldName, final boolean forceAccess) {
		Validate.isTrue(cls != null, "The class must not be null");
		Validate.isTrue(StringUtils.isNotBlank(fieldName), "The field name must not be blank/empty");
		// check up the superclass hierarchy
		for (Class<?> acls = cls; acls != null; acls = acls.getSuperclass()) {
			try {
				final Field field = acls.getDeclaredField(fieldName);
				// getDeclaredField checks for non-public scopes as well
				// and it returns accurate results
				if (!Modifier.isPublic(field.getModifiers())) {
					if (forceAccess) {
						field.setAccessible(true);
					} else {
						continue;
					}
				}
				return field;
			} catch (final NoSuchFieldException ex) { // NOPMD
				// ignore
			}
		}
		// check the public interface case. This must be manually searched for
		// incase there is a public supersuperclass field hidden by a private/package
		// superclass field.
		Field match = null;
		for (final Class<?> class1 : ClassUtils.getAllInterfaces(cls)) {
			try {
				final Field test = class1.getField(fieldName);
				Validate.isTrue(match == null, "Reference to field %s is ambiguous relative to %s"
						+ "; a matching field exists on two or more implemented interfaces.", fieldName, cls);
				match = test;
			} catch (final NoSuchFieldException ex) { // NOPMD
				// ignore
			}
		}
		return match;
	}

	/**
	 * Gets an accessible {@link Field} by name respecting scope. Only the specified class will be considered.
	 * 
	 * @param cls
	 *            the {@link Class} to reflect, must not be {@code null}
	 * @param fieldName
	 *            the field name to obtain
	 * @return the Field object
	 * @throws IllegalArgumentException
	 *             if the class is {@code null}, or the field name is blank or empty
	 */
	public static Field getDeclaredField(final Class<?> cls, final String fieldName) {
		return getDeclaredField(cls, fieldName, false);
	}

	/**
	 * Gets an accessible {@link Field} by name, breaking scope if requested. Only the specified class will be
	 * considered.
	 * 
	 * @param cls
	 *            the {@link Class} to reflect, must not be {@code null}
	 * @param fieldName
	 *            the field name to obtain
	 * @param forceAccess
	 *            whether to break scope restrictions using the
	 *            {@link java.lang.reflect.AccessibleObject#setAccessible(boolean)} method. {@code false} will only
	 *            match {@code public} fields.
	 * @return the Field object
	 * @throws IllegalArgumentException
	 *             if the class is {@code null}, or the field name is blank or empty
	 */
	public static Field getDeclaredField(final Class<?> cls, final String fieldName, final boolean forceAccess) {
		Validate.isTrue(cls != null, "The class must not be null");
		Validate.isTrue(StringUtils.isNotBlank(fieldName), "The field name must not be blank/empty");
		try {
			// only consider the specified class by using getDeclaredField()
			final Field field = cls.getDeclaredField(fieldName);
			if (!MemberUtils.isAccessible(field)) {
				if(forceAccess)
					field.setAccessible(true);
				else
					return null;
			}
			return field;
		} catch (final NoSuchFieldException e) { // NOPMD
			// ignore
		}
		return null;
	}

	/**
	 * Reads an accessible {@link Field}.
	 * 
	 * @param field
	 *            the field to use
	 * @param target
	 *            the object to call on, may be {@code null} for {@code static} fields
	 * @return the field value
	 * @throws IllegalArgumentException
	 *             if the field is {@code null}
	 * @throws IllegalAccessException
	 *             if the field is not accessible
	 */
	public static Object readField(final Field field, final Object target) throws IllegalAccessException {
		return readField(field, target, false);
	}

	/**
	 * Reads a {@link Field}.
	 * 
	 * @param field
	 *            the field to use
	 * @param target
	 *            the object to call on, may be {@code null} for {@code static} fields
	 * @param forceAccess
	 *            whether to break scope restrictions using the
	 *            {@link java.lang.reflect.AccessibleObject#setAccessible(boolean)} method.
	 * @return the field value
	 * @throws IllegalArgumentException
	 *             if the field is {@code null}
	 * @throws IllegalAccessException
	 *             if the field is not made accessible
	 */
	public static Object readField(final Field field, final Object target, final boolean forceAccess) throws IllegalAccessException {
		Validate.isTrue(field != null, "The field must not be null");
		if (forceAccess && !field.isAccessible()) {
			field.setAccessible(true);
		}
		return field.get(target);
	}

	/**
	 * Reads the named {@code public} {@link Field}. Superclasses will be considered.
	 * 
	 * @param target
	 *            the object to reflect, must not be {@code null}
	 * @param fieldName
	 *            the field name to obtain
	 * @return the value of the field
	 * @throws IllegalArgumentException
	 *             if the class is {@code null}, or the field name is blank or empty or could not be found
	 * @throws IllegalAccessException
	 *             if the named field is not {@code public}
	 */
	public static Object readField(final Object target, final String fieldName) throws IllegalAccessException {
		return readField(target, fieldName, false);
	}

	/**
	 * Reads the named {@link Field}. Superclasses will be considered.
	 * 
	 * @param target
	 *            the object to reflect, must not be {@code null}
	 * @param fieldName
	 *            the field name to obtain
	 * @param forceAccess
	 *            whether to break scope restrictions using the
	 *            {@link java.lang.reflect.AccessibleObject#setAccessible(boolean)} method. {@code false} will only
	 *            match {@code public} fields.
	 * @return the field value
	 * @throws IllegalArgumentException
	 *             if {@code target} is {@code null}, or the field name is blank or empty or could not be found
	 * @throws IllegalAccessException
	 *             if the named field is not made accessible
	 */
	public static Object readField(final Object target, final String fieldName, final boolean forceAccess) throws IllegalAccessException {
		Validate.isTrue(target != null, "target object must not be null");
		final Class<?> cls = target.getClass();
		final Field field = getField(cls, fieldName, forceAccess);
		Validate.isTrue(field != null, "Cannot locate field %s on %s", fieldName, cls);
		// already forced access above, don't repeat it here:
		return readField(field, target, false);
	}

	/**
	 * Reads the named {@code public} {@link Field}. Only the class of the specified object will be considered.
	 * 
	 * @param target
	 *            the object to reflect, must not be {@code null}
	 * @param fieldName
	 *            the field name to obtain
	 * @return the value of the field
	 * @throws IllegalArgumentException
	 *             if {@code target} is {@code null}, or the field name is blank or empty or could not be found
	 * @throws IllegalAccessException
	 *             if the named field is not {@code public}
	 */
	public static Object readDeclaredField(final Object target, final String fieldName) throws IllegalAccessException {
		return readDeclaredField(target, fieldName, false);
	}

	/**
	 * Gets a {@link Field} value by name. Only the class of the specified object will be considered.
	 * 
	 * @param target
	 *            the object to reflect, must not be {@code null}
	 * @param fieldName
	 *            the field name to obtain
	 * @param forceAccess
	 *            whether to break scope restrictions using the
	 *            {@link java.lang.reflect.AccessibleObject#setAccessible(boolean)} method. {@code false} will only
	 *            match public fields.
	 * @return the Field object
	 * @throws IllegalArgumentException
	 *             if {@code target} is {@code null}, or the field name is blank or empty or could not be found
	 * @throws IllegalAccessException
	 *             if the field is not made accessible
	 */
	public static Object readDeclaredField(final Object target, final String fieldName, final boolean forceAccess) throws IllegalAccessException {
		Validate.isTrue(target != null, "target object must not be null");
		final Class<?> cls = target.getClass();
		final Field field = getDeclaredField(cls, fieldName, forceAccess);
		Validate.isTrue(field != null, "Cannot locate declared field %s.%s", cls, fieldName);
		// already forced access above, don't repeat it here:
		return readField(field, target, false);
	}

	/**
	 * Writes an accessible {@link Field}.
	 * 
	 * @param field
	 *            to write
	 * @param target
	 *            the object to call on, may be {@code null} for {@code static} fields
	 * @param value
	 *            to set
	 * @throws IllegalAccessException
	 *             if the field or target is {@code null}, the field is not accessible or is {@code final}, or
	 *             {@code value} is not assignable
	 */
	public static void writeField(final Field field, final Object target, final Object value) throws IllegalAccessException {
		writeField(field, target, value, false);
	}

	/**
	 * Writes a {@link Field}.
	 * 
	 * @param field
	 *            to write
	 * @param target
	 *            the object to call on, may be {@code null} for {@code static} fields
	 * @param value
	 *            to set
	 * @param forceAccess
	 *            whether to break scope restrictions using the
	 *            {@link java.lang.reflect.AccessibleObject#setAccessible(boolean)} method. {@code false} will only
	 *            match {@code public} fields.
	 * @throws IllegalArgumentException
	 *             if the field is {@code null} or {@code value} is not assignable
	 * @throws IllegalAccessException
	 *             if the field is not made accessible or is {@code final}
	 */
	public static void writeField(final Field field, final Object target, final Object value, final boolean forceAccess)
			throws IllegalAccessException {
		Validate.isTrue(field != null, "The field must not be null");
		if (forceAccess && !field.isAccessible()) {
			field.setAccessible(true);
		} else {
			MemberUtils.setAccessibleWorkaround(field);
		}
		field.set(target, value);
	}

	/**
	 * Writes a {@code public} {@link Field}. Superclasses will be considered.
	 * 
	 * @param target
	 *            the object to reflect, must not be {@code null}
	 * @param fieldName
	 *            the field name to obtain
	 * @param value
	 *            to set
	 * @throws IllegalArgumentException
	 *             if {@code target} is {@code null}, {@code fieldName} is blank or empty or could not be found, or
	 *             {@code value} is not assignable
	 * @throws IllegalAccessException
	 *             if the field is not accessible
	 */
	public static void writeField(final Object target, final String fieldName, final Object value) throws IllegalAccessException {
		writeField(target, fieldName, value, false);
	}

	/**
	 * Writes a {@link Field}. Superclasses will be considered.
	 * 
	 * @param target
	 *            the object to reflect, must not be {@code null}
	 * @param fieldName
	 *            the field name to obtain
	 * @param value
	 *            to set
	 * @param forceAccess
	 *            whether to break scope restrictions using the
	 *            {@link java.lang.reflect.AccessibleObject#setAccessible(boolean)} method. {@code false} will only
	 *            match {@code public} fields.
	 * @throws IllegalArgumentException
	 *             if {@code target} is {@code null}, {@code fieldName} is blank or empty or could not be found, or
	 *             {@code value} is not assignable
	 * @throws IllegalAccessException
	 *             if the field is not made accessible
	 */
	public static void writeField(final Object target, final String fieldName, final Object value, final boolean forceAccess)
			throws IllegalAccessException {
		Validate.isTrue(target != null, "target object must not be null");
		final Class<?> cls = target.getClass();
		final Field field = getField(cls, fieldName, forceAccess);
		Validate.isTrue(field != null, "Cannot locate declared field %s.%s", cls.getName(), fieldName);
		writeField(field, target, value, false);
	}

    /**
     * Writes a public field. Only the specified class will be considered.
     * @param target  the object to reflect, must not be null
     * @param fieldName  the field name to obtain
     * @param value to set
     * @throws IllegalArgumentException if <code>target</code> or <code>fieldName</code> is null
     * @throws IllegalAccessException if the field is not made accessible
     */
    public static void writeDeclaredField(Object target, String fieldName, Object value) throws IllegalAccessException {
        writeDeclaredField(target, fieldName, value, false);
    }

    /**
     * Writes a public field. Only the specified class will be considered.
     * @param target  the object to reflect, must not be null
     * @param fieldName  the field name to obtain
     * @param value to set
     * @param forceAccess  whether to break scope restrictions using the
     *  <code>setAccessible</code> method. <code>False</code> will only
     *  match public fields.
     * @throws IllegalArgumentException if <code>target</code> or <code>fieldName</code> is null
     * @throws IllegalAccessException if the field is not made accessible
     */
    public static void writeDeclaredField(Object target, String fieldName, Object value, boolean forceAccess)
            throws IllegalAccessException {
        if (target == null) {
            throw new IllegalArgumentException("target object must not be null");
        }
        Class<?> cls = target.getClass();
        Field field = getDeclaredField(cls, fieldName, forceAccess);
        if (field == null) {
            throw new IllegalArgumentException("Cannot locate declared field " + cls.getName() + "." + fieldName);
        }
        //already forced access above, don't repeat it here:
        writeField(field, target, value);
    }
}
