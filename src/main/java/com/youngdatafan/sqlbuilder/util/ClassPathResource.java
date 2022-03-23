package com.youngdatafan.sqlbuilder.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

/**
 * 类路径资源加载.
 *
 * @author gavin
 * @since 2021-08-17 2:37 下午
 */
public class ClassPathResource {

    private final String path;

    private ClassLoader classLoader;

    private Class<?> clazz;

    /**
     * Create a new {@code ClassPathResource} for {@code ClassLoader} usage.
     * A leading slash will be removed, as the ClassLoader resource access
     * methods will not accept it.
     *
     * <p>The thread context class loader will be used for
     * loading the resource.
     *
     * @param path the absolute path within the class path
     * @see java.lang.ClassLoader#getResourceAsStream(String)
     */
    public ClassPathResource(String path) {
        this(path, (ClassLoader) null);
    }

    /**
     * Create a new {@code ClassPathResource} for {@code ClassLoader} usage.
     * A leading slash will be removed, as the ClassLoader resource access
     * methods will not accept it.
     *
     * @param path        the absolute path within the classpath
     * @param classLoader the class loader to load the resource with,
     *                    or {@code null} for the thread context class loader
     * @see ClassLoader#getResourceAsStream(String)
     */
    public ClassPathResource(String path, ClassLoader classLoader) {
        notNull(path, "Path must not be null");
        String pathToUse = path;
        if (pathToUse.startsWith("/")) {
            pathToUse = pathToUse.substring(1);
        }
        this.path = pathToUse;
        this.classLoader = classLoader != null ? classLoader : getDefaultClassLoader();
    }

    /**
     * Create a new {@code ClassPathResource} for {@code Class} usage.
     * The path can be relative to the given class, or absolute within
     * the classpath via a leading slash.
     *
     * @param path  relative or absolute path within the class path
     * @param clazz the class to load resources with
     * @see java.lang.Class#getResourceAsStream
     */
    public ClassPathResource(String path, Class<?> clazz) {
        notNull(path, "Path must not be null");
        this.path = path;
        this.clazz = clazz;
    }

    /**
     * Create a new {@code ClassPathResource} with optional {@code ClassLoader}
     * and {@code Class}. Only for internal usage.
     *
     * @param path        relative or absolute path within the classpath
     * @param classLoader the class loader to load the resource with, if any
     * @param clazz       the class to load resources with, if any
     * @deprecated as of 4.3.13, in favor of selective use of
     * {@link #ClassPathResource(String, ClassLoader)} vs {@link #ClassPathResource(String, Class)}
     */
    @Deprecated
    protected ClassPathResource(String path, ClassLoader classLoader, Class<?> clazz) {
        this.path = path;
        this.classLoader = classLoader;
        this.clazz = clazz;
    }

    /**
     * Return the default ClassLoader to use: typically the thread context
     * ClassLoader, if available; the ClassLoader that loaded the ClassUtils
     * class will be used as fallback.
     *
     * <p>Call this method if you intend to use the thread context ClassLoader
     * in a scenario where you clearly prefer a non-null ClassLoader reference:
     * for example, for class path resource loading (but not necessarily for
     * {@code Class.forName}, which accepts a {@code null} ClassLoader
     * reference as well).
     *
     * @return the default ClassLoader (only {@code null} if even the system ClassLoader isn't accessible)
     * @see Thread#getContextClassLoader()
     * @see ClassLoader#getSystemClassLoader()
     */
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = ClassPathResource.class.getClassLoader();
            if (cl == null) {
                // getClassLoader() returning null indicates the bootstrap ClassLoader
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable ex) {
                    // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
                }
            }
        }
        return cl;
    }

    /**
     * Determine if the given objects are equal, returning {@code true} if
     * both are {@code null} or {@code false} if only one is {@code null}.
     *
     * <p>Compares arrays with {@code Arrays.equals}, performing an equality
     * check based on the array elements rather than the array reference.
     *
     * @param o1 first Object to compare
     * @param o2 second Object to compare
     * @return whether the given objects are equal
     * @see Object#equals(Object)
     * @see java.util.Arrays#equals
     */
    public static boolean nullSafeEquals(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        if (o1.equals(o2)) {
            return true;
        }
        if (o1.getClass().isArray() && o2.getClass().isArray()) {
            return arrayEquals(o1, o2);
        }
        return false;
    }

    /**
     * Assert that an object is not {@code null}.
     * <pre class="code">Assert.notNull(clazz, "The class must not be null");</pre>
     *
     * @param object  the object to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the object is {@code null}
     */
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Compare the given arrays with {@code Arrays.equals}, performing an equality
     * check based on the array elements rather than the array reference.
     *
     * @param o1 first array to compare
     * @param o2 second array to compare
     * @return whether the given objects are equal
     * @see #nullSafeEquals(Object, Object)
     * @see java.util.Arrays#equals
     */
    private static boolean arrayEquals(Object o1, Object o2) {
        if (o1 instanceof Object[] && o2 instanceof Object[]) {
            return Arrays.equals((Object[]) o1, (Object[]) o2);
        }
        if (o1 instanceof boolean[] && o2 instanceof boolean[]) {
            return Arrays.equals((boolean[]) o1, (boolean[]) o2);
        }
        if (o1 instanceof byte[] && o2 instanceof byte[]) {
            return Arrays.equals((byte[]) o1, (byte[]) o2);
        }
        if (o1 instanceof char[] && o2 instanceof char[]) {
            return Arrays.equals((char[]) o1, (char[]) o2);
        }
        if (o1 instanceof double[] && o2 instanceof double[]) {
            return Arrays.equals((double[]) o1, (double[]) o2);
        }
        if (o1 instanceof float[] && o2 instanceof float[]) {
            return Arrays.equals((float[]) o1, (float[]) o2);
        }
        if (o1 instanceof int[] && o2 instanceof int[]) {
            return Arrays.equals((int[]) o1, (int[]) o2);
        }
        if (o1 instanceof long[] && o2 instanceof long[]) {
            return Arrays.equals((long[]) o1, (long[]) o2);
        }
        if (o1 instanceof short[] && o2 instanceof short[]) {
            return Arrays.equals((short[]) o1, (short[]) o2);
        }
        return false;
    }

    /**
     * the path for this resource (as resource path within the class path).
     *
     * @return the path for this resource (as resource path within the class path).
     */
    public final String getPath() {
        return this.path;
    }

    /**
     * Return the ClassLoader that this resource will be obtained from.
     *
     * @return Return the ClassLoader that this resource will be obtained from.
     */
    public final ClassLoader getClassLoader() {
        return this.clazz != null ? this.clazz.getClassLoader() : this.classLoader;
    }

    /**
     * Resolves a URL for the underlying class path resource.
     *
     * @return the resolved URL, or {@code null} if not resolvable
     */
    protected URL resolveURL() {
        if (this.clazz != null) {
            return this.clazz.getResource(this.path);
        } else if (this.classLoader != null) {
            return this.classLoader.getResource(this.path);
        } else {
            return ClassLoader.getSystemResource(this.path);
        }
    }

    /**
     * This implementation opens an InputStream for the given class path resource.
     *
     * @return InputStream
     * @throws IOException read error.
     * @see java.lang.ClassLoader#getResourceAsStream(String)
     * @see java.lang.Class#getResourceAsStream(String)
     */
    public InputStream getInputStream() throws IOException {
        InputStream is;
        if (this.clazz != null) {
            is = this.clazz.getResourceAsStream(this.path);
        } else if (this.classLoader != null) {
            is = this.classLoader.getResourceAsStream(this.path);
        } else {
            is = ClassLoader.getSystemResourceAsStream(this.path);
        }
        if (is == null) {
            throw new FileNotFoundException(getDescription() + " cannot be opened because it does not exist");
        }
        return is;
    }

    /**
     * This implementation returns a description that includes the class path location.
     *
     * @return This implementation returns a description that includes the class path location.
     */
    public String getDescription() {
        StringBuilder builder = new StringBuilder("class path resource [");
        String pathToUse = this.path;
        if (this.clazz != null && !pathToUse.startsWith("/")) {
            builder.append(this.clazz.getName());
            builder.append('/');
        }
        if (pathToUse.startsWith("/")) {
            pathToUse = pathToUse.substring(1);
        }
        builder.append(pathToUse);
        builder.append(']');
        return builder.toString();
    }

    /**
     * This implementation compares the underlying class path locations.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ClassPathResource)) {
            return false;
        }
        ClassPathResource otherRes = (ClassPathResource) other;
        return this.path.equals(otherRes.path) && nullSafeEquals(this.classLoader, otherRes.classLoader) && nullSafeEquals(this.clazz, otherRes.clazz);
    }

    /**
     * This implementation returns the hash code of the underlying
     * class path location.
     */
    @Override
    public int hashCode() {
        return this.path.hashCode();
    }

}

