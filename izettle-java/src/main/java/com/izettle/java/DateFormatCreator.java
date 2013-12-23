package com.izettle.java;

import com.izettle.java.enums.TimeZoneId;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public abstract class DateFormatCreator {

	public static final String FORMAT_YYYYMMDD = "yyyyMMdd";

	private DateFormatCreator() {
	}

	public static DateFormat createFormatter(String pattern, TimeZoneId timeZoneId) {
		DateFormat df = new SimpleDateFormat(pattern);
		df.setTimeZone(timeZoneId.getTimeZone());
		return df;
	}

	public static DateFormat createFormatter(String pattern, Locale locale, TimeZoneId timeZoneId) {
		DateFormat df = new SimpleDateFormat(pattern, locale);
		df.setTimeZone(timeZoneId.getTimeZone());
		return df;
	}

	/**
	 * Create a formatter commonly used in the HTTP protocol. Specifically RFC 1123 is used in almost all
	 * time-related HTTP headers. Ex: "Sun, 19 Jun 2011 22:26:58 GMT". Time Zone is included in the format, so none
	 * is needed as argument for this method
	 * @return the newly created formatter
	 */
	public static DateFormat createRFC1123Formatter() {
		return createFormatter("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US, TimeZoneId.GMT);
	}

	/**
	 * @param timeZoneId the time zone id of the spectator
	 * @return date formatter with pattern <b>yyyy-MM-dd</b>(ISO-8601 Date).
	 */
	public static DateFormat createDateFormatter(TimeZoneId timeZoneId) {
		return createFormatter("yyyy-MM-dd", timeZoneId);
	}

	/**
	 * @param locale the locale of the spectator
	 * @param timeZoneId the time zone id of the spectator
	 * @return date formatter with pattern <b>yyyy-MM-dd</b>(ISO-8601 Date).
	 */
	public static DateFormat createDateFormatter(Locale locale, TimeZoneId timeZoneId) {
		return createFormatter("yyyy-MM-dd", locale, timeZoneId);
	}

	/**
	 * @param timeZoneId the time zone id of the spectator
	 * @return date formatter with pattern <b>yyyy-MM-dd HH:mm:ss</b>.
	 */
	public static DateFormat createDateAndTimeFormatter(TimeZoneId timeZoneId) {
		return createFormatter("yyyy-MM-dd HH:mm:ss", timeZoneId);
	}

	/**
	 * @param timeZoneId the time zone id of the spectator
	 * @return date formatter with pattern <b>yyyy-MM-dd HH:mm:ss.SSS</b>.
	 */
	public static DateFormat createDateAndTimeMillisFormatter(TimeZoneId timeZoneId) {
		return createFormatter("yyyy-MM-dd HH:mm:ss.SSS", timeZoneId);
	}

	/**
	 * @param timeZoneId the time zone id of the spectator
	 * @return date formatter with pattern <b>yyyy-MM-dd HH:mm</b>.
	 */
	public static DateFormat createDateTimePickerFormatter(TimeZoneId timeZoneId) {
		return createFormatter("yyyy-MM-dd HH:mm", timeZoneId);
	}

	/**
	 * @param locale the locale of the spectator
	 * @param timeZoneId the time zone id of the spectator
	 * @return date formatter with pattern <b>d MMMM yyyy</b>.
	 */
	public static DateFormat createLongDateFormatter(Locale locale, TimeZoneId timeZoneId) {
		return createFormatter("d MMMM yyyy", locale, timeZoneId);
	}

	/**
	 * Create a DateFormat that parses/formats <b>yyyyMMdd</b> date strings, with respect to the passed in time zone id.
	 * @param timeZoneId - desired timezone.
	 * @return
	 */
	public static DateFormat createYYYYMMDDFormatter(TimeZoneId timeZoneId) {
		return createFormatter(FORMAT_YYYYMMDD, timeZoneId);
	}
}
