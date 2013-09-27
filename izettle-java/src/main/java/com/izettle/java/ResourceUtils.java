package com.izettle.java;

import static java.lang.String.format;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public abstract class ResourceUtils {

	private ResourceUtils() {
	}

	public static byte[] getResourceAsBytes(String resourceName)
			throws IOException {
		ResourceLoader resourceLoader = new ResourceLoader();
		return resourceLoader.getBytesFromResource(resourceName);
	}

	public static InputStream getResourceAsStream(String resourceName) throws IOException {
		ResourceLoader resourceLoader = new ResourceLoader();
		return resourceLoader.getInputStream(resourceName);
	}

	private static class ResourceLoader {

		private byte[] getBytesFromResource(String resourceName) throws IOException {
			InputStream inputStream = getInputStream(resourceName);
			byte[] outputBytes;
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			try {
				byte[] buffer = new byte[1024];
				int read;
				while ((read = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, read);
				}
				outputStream.flush();
				outputBytes = outputStream.toByteArray();
			} finally {
				if (inputStream != null) {
					inputStream.close();
				}
			}
			return outputBytes;
		}

		public InputStream getInputStream(String resourceName) throws IOException {
			final ClassLoader[] classLoaders = new ClassLoader[]{
				getClass().getClassLoader(),
				ResourceUtils.class.getClassLoader(),
				Thread.currentThread().getContextClassLoader(),
				ClassLoader.getSystemClassLoader()
			};
			resourceName = removePreSlash(resourceName);
			URL resource = null;
			for (ClassLoader classLoader : classLoaders) {
				resource = classLoader.getResource(resourceName);

				if (resource != null) {
					break;
				}
				resource = classLoader.getResource("/" + resourceName);
				if (resource != null) {
					break;
				}
			}
			if (resource == null) {
				throw new IOException("Could not find resource: " + resourceName);
			}
			return resource.openStream();
		}

		private static String removePreSlash(String resourceName) {
			if (resourceName.charAt(0) == '/') {
				return resourceName.substring(1);
			}
			return resourceName;
		}
	}

	/**
	 * Returns a {@code URL} pointing to {@code resourceName} if the resource is
	 * found in the class path. {@code Resources.class.getClassLoader()} is used
	 * to locate the resource.
	 * 
	 * @throws NullPointerException if resource is not found
	 */
	public static URL getResource(String resourceName) {
		URL url = ResourceUtils.class.getClassLoader().getResource(resourceName);
		requireNonNull(url, format("resource %s not found.", resourceName));
		return url;
	}

	/**
	 * Returns a {@code URL} pointing to {@code resourceName} that is relative to
	 * {@code contextClass}, if the resource is found in the class path. 
	 * 
	 * @throws NullPointerException if resource is not found
	 */
	public static URL getResource(Class<?> contextClass, String resourceName) {
		URL url = contextClass.getResource(resourceName);

		requireNonNull(url, format("resource %s relative to %s not found.", resourceName, contextClass.getName()));
		return url;
	}

	/**
	 * Checks that the specified object reference is not {@code null} and
	 * throws a customized {@link NullPointerException} if it is. This method
	 * is designed primarily for doing parameter validation in methods and
	 * constructors with multiple parameters, as demonstrated below:
	 * <blockquote><pre>
	 * public Foo(Bar bar, Baz baz) {
	 *     this.bar = Objects.requireNonNull(bar, "bar must not be null");
	 *     this.baz = Objects.requireNonNull(baz, "baz must not be null");
	 * }
	 * </pre></blockquote>
	 *
	 * @param obj     the object reference to check for nullity
	 * @param message detail message to be used in the event that a {@code
	 *                NullPointerException} is thrown
	 * @param <T> the type of the reference
	 * @return {@code obj} if not {@code null}
	 * @throws NullPointerException if {@code obj} is {@code null}
	 */
	private static <T> T requireNonNull(T obj, String message) {
		if (obj == null)
			throw new NullPointerException(message);
		return obj;
	}
}
