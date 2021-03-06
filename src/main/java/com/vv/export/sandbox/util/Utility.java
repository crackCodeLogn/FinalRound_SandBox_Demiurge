package com.vv.export.sandbox.util;

import com.google.common.collect.ImmutableMap;
import javafx.concurrent.Worker;

/**
 * @author Vivek
 * @since 14-10-2018
 */
public class Utility {

    public final static String DOWNLOAD_DESTINATION_PATH = "download.destination.path";
    public final static String DOWNLOAD_TIMEOUT_UPPER = "download.timeout.upper";
    public final static String DOWNLOAD_FORMATS = "download.formats";
    public final static String HTTP_SITE_EXTENSIONS = "http.site.extensions";
    public final static String HOME_PAGE_URL = "home.page.url";
    public final static String WWW = "www";
    public final static String HTTP = "http";
    public final static String HTTPS = "https";
    public final static String HTTP_URL_PART = "http://";
    public final static String HTTPS_URL_PART = "https://";

    public final static String DEFAULT_DOWNLOAD_DEST_PATH = "/home/v2k/theRange/sandbox_target/tmp";
    public final static String DEFAULT_HOME_PAGE_URL = "https://bing.com";
    public static final String DEFAULT_VALUE_STRING = "";

    public final static String FILEPATH_PROPERTIES = "src//main//resources//config.properties";

    public final static String FORMAT_STRING_EXTENSION = ".%s";
    public final static String FORMAT_STRING_IMG_PATH = "%s%s";
    public final static String FORMAT_STRING_SEARCH_WEB = "%s/search?q=%s";
    public final static String FORMAT_STRING_HTTP_WITH_WWWW = "%s%s.%s";
    public final static String FORMAT_STRING_HTTP_WITHOUT_WWW = "%s%s";

    public final static String BROWSER_TITLE = "Albedo";
    public final static String MENU_TITLE = "MENU";
    public final static String HISTORY_TITLE = "HISTORY";
    public final static String DOWNLOADS_TITLE = "DOWNLOADS";

    public final static String BASE_FILEPATH = "/home/v2k/theRange/IdeaProjects/FinalRound_Sandbox_Demiurge/";

    public final static String RES_COVER_THUMBNAIL = "src//main//resources//cover_thumbnail.jpg";
    public final static String RES_ARROW_BACK = "src//main//resources//RESIZED_ic_arrow_back_black_48dp_2x.png";
    public final static String RES_ARROW_FORWARD = "src//main//resources//RESIZED_ic_arrow_forward_black_48dp_2x.png";
    public final static String RES_REFRESH = "src//main//resources//RESIZED_ic_refresh_black_48dp_2x.png";
    public final static String RES_HOME = "src//main//resources//RESIZED_ic_home_black_48dp_2x.png";
    public final static String RES_CLOSE = "src//main//resources//RESIZED_ic_close_black_48dp_2x.png";
    public final static String RES_MORE_VERT = "src//main//resources//RESIZED_ic_more_vert_black_48dp_2x.png";

    public final static String IDLE_BROWSER = "---";
    private final static String READY = "READY";
    private final static String SCHEDULED = "SCHEDULED";
    private final static String RUNNING = "RUNNING";
    private final static String FAILED = "FAILED";
    private final static String SUCCEEDED = "SUCCEEDED";
    private final static String CANCELLED = "CANCELLED";

    public static final ImmutableMap<Worker.State, String> browserStateToStringMap = ImmutableMap.<Worker.State, String>builder()
            .put(Worker.State.READY, READY)
            .put(Worker.State.SCHEDULED, SCHEDULED)
            .put(Worker.State.RUNNING, RUNNING)
            .put(Worker.State.FAILED, FAILED)
            .put(Worker.State.SUCCEEDED, SUCCEEDED)
            .put(Worker.State.CANCELLED, CANCELLED)
            .build();

    public final static int HEIGHT_OF_TEXT_FIELD_CONTROLLER = 1000;
    public final static int WIDTH_MENU_WINDOW = 250;
    public final static int GO_FORWARD = 1;
    public final static int GO_BACK = -1;
}
