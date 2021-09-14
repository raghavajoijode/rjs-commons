package org.subra.commons.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subra.commons.exceptions.RJSCustomException;
import org.subra.commons.exceptions.RJSRuntimeException;
import org.subra.commons.utils.RJSCollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * @author Raghava Joijode
 */
public class CommonHelper {

    public static final String COMMA = ",";
    public static final String HYPHEN = "-";
    public static final String UNDER_SCORE = "_";
    public static final String SLASH = "/";
    public static final String COLON = ":";
    public static final String SEMI_COLON = ";";
    public static final String AT = "@";
    public static final String AMP = "&";
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonHelper.class);
    private static final String EXCEPTION = "exception";
    private static final String EXCEPTION_SUFFIX = HYPHEN + EXCEPTION;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private CommonHelper() {
        throw new UnsupportedOperationException();
    }

    public static ObjectMapper getObjectMapper() {
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        return OBJECT_MAPPER;
    }

    public static <T> T convertToClass(Object objValue, Class<T> clazz) throws IOException {
        T classValue = null;
        try {
            classValue = getObjectMapper().readValue((objValue instanceof String) ? (String) objValue : writeValueAsString(objValue), clazz);
        } catch (IllegalArgumentException ie) {
            LOGGER.error("Error occurred converting obj to class", ie);
        }
        return classValue;
    }

    public static String writeValueAsString(Object objValue) throws JsonProcessingException {
        return CommonHelper.getObjectMapper().writeValueAsString(objValue);
    }

    public static <T> T readValueFromResource(final String resourceFileName, final Class<T> clazz) throws IOException {
        return getObjectMapper().readValue(CommonHelper.class.getResourceAsStream(resourceFileName.startsWith("/") ? resourceFileName : String.format("/%s", resourceFileName.trim())), clazz);
    }

    public static <T> T getCacheData(HttpServletRequest request, String cacheKey, Supplier<T> supplier) {
        return getOptionalCachedData(request, cacheKey, supplier).orElse(null);
    }

    public static <T> T getCachedDataOrThrow(final HttpServletRequest request, final String cacheKey, final Supplier<T> supplier) throws RJSCustomException {
        return getOptionalCacheDataOrThrow(request, cacheKey, supplier).orElse(null);
    }

    public static <T> Optional<T> getOptionalCachedData(final HttpServletRequest request, final String cacheKey, final Supplier<T> supplier) {
        try {
            return getOptionalCacheDataOrThrow(request, cacheKey, supplier);
        } catch (final Exception e) {
            throw new RJSRuntimeException("Caught unexpected exception", e);
        }
    }

    private static <T> Optional<T> getOptionalCacheDataOrThrow(final HttpServletRequest request, final String cacheKey, final Supplier<T> supplier) throws RJSCustomException {
        final Optional<T> cachedData = (Optional<T>) request.getAttribute(cacheKey);
        if (cachedData.isPresent())
            return cachedData;

        final RJSCustomException exception = (RJSCustomException) request.getAttribute(cacheKey + EXCEPTION_SUFFIX);
        if (exception != null)
            throw exception;

        try {
            final Optional<T> data = Optional.ofNullable(supplier.get());
            request.setAttribute(cacheKey, data);
            return data;
        } catch (Exception e) {
            request.setAttribute(cacheKey + EXCEPTION_SUFFIX, new RJSCustomException(e));
            throw new RJSCustomException(e);
        }
    }

    /*
     * Creates new instance of a given class
     *
     * throws RJSRuntimeException if unable to create class instance.
     */
    public static <T> T createClassInstance(final Class<T> clazz) throws RJSCustomException {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            throw new RJSCustomException(e);
        }
    }

    /*
     * @param title - String which needs to be converted to name
     * @return name in standard form from title
     */
    public static String createNameFromTitle(final String title) {
        return StringUtils.replace(StringUtils.lowerCase(title), StringUtils.SPACE, HYPHEN);
    }

    /*
     * @param name - String which needs to be converted to title
     * @return title in standard form from name
     */
    public static String decodeTitleFromName(final String name) {
        return StringUtils.replace(StringUtils.capitalize(name), HYPHEN, StringUtils.SPACE);
    }

    public static List<String> getLookUpKeys(final String str) {
        return RJSCollectionUtils.getStreamFromArray(substringsBetween(str, "${", "}")).map(i -> i.contains(":-") ? substringBefore(i, ":-") : i).collect(Collectors.toList());
    }

    public static String encode(final String value, final String charset) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, defaultIfBlank(charset, "UTF-8"));
    }

}