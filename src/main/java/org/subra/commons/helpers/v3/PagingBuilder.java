package org.subra.commons.helpers.v3;


import org.apache.commons.lang3.StringUtils;
import org.subra.commons.dtos.paging.v2.Paging;
import org.subra.commons.dtos.paging.v2.Paging.PageItem;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Raghava Joijode
 */
public final class PagingBuilder {

    public static final String DEF_ELLIPSES = "...";

    private static final String RECORD_RANGE_FORM = "%s - %s";
    private static final int ZERO = 0;
    private static final int ONE = 1;

    private PagingBuilder() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public static Paging build(final int page, final int size, final int records) {
        return build(page, size, records, 2, 2, DEF_ELLIPSES);
    }

    public static Paging build(int currentPageNumber, final int pageSize, final int totalRecordsSize, final int blocksBeforeEllipse, final int blocksAfterEllipse, final String ellipses) {
        int totalPages = Math.max(totalPages(totalRecordsSize, pageSize), ONE);
        Paging paging = new Paging();
        paging.setCurrentPageNumber(currentPageNumber);
        paging.setPageSize(pageSize);
        paging.setTotalPages(totalPages);
        paging.setTotalRecordsSize(totalRecordsSize);
        paging.setPrevPageOffset(currentPageNumber > 1 ? (currentPageNumber - 2) * pageSize : 0);
        paging.setNextPageOffset(currentPageNumber * pageSize);
        paging.setFirstPage(currentPageNumber == ONE);
        paging.setLastPage(lastPage(currentPageNumber, totalPages, totalRecordsSize));
        paging.setRangeStart(rangeStart(currentPageNumber, pageSize));
        paging.setRangeEnd(rangeEnd(currentPageNumber, pageSize, totalRecordsSize));
        paging.setRecordRange(recordRange(currentPageNumber, pageSize, totalRecordsSize));
        paging.setList(buildList(currentPageNumber, totalPages, pageSize, blocksBeforeEllipse, blocksAfterEllipse, ellipses));
        return paging;
    }

    private static int totalPages(final int records, final int size) {
        return records / size + (records % size == ZERO ? ZERO : ONE);
    }

    private static int rangeStart(final int page, final int size) {
        return (page - ONE) * size + ONE;
    }

    private static int rangeEnd(final int page, final int size, final int records) {
        return Math.min(page * size, records);
    }

    private static String recordRange(final int page, final int size, final int records) {
        return records == 0 ? String.valueOf(ZERO) : String.format(RECORD_RANGE_FORM, rangeStart(page, size), rangeEnd(page, size, records));
    }

    private static List<PageItem> buildList(final int currentPageNumber, final int totalPages, final int pageSize, final int blocksBeforeEllipse, final int blocksAfterEllipse, final String ellipses) {
        List<PageItem> pagingList = new LinkedList<>();
        int totalBlocks = blocksBeforeEllipse + 1 + blocksAfterEllipse;
        if(totalPages <= totalBlocks) {
            addPageItems(currentPageNumber, totalPages, pageSize, pagingList, 1);
        } else {
            if(currentPageNumber < (totalPages - blocksAfterEllipse + 1)){
                int endIndex = Math.max(currentPageNumber, blocksBeforeEllipse);
                addPageItems(currentPageNumber, endIndex, pageSize, pagingList, endIndex -blocksBeforeEllipse + 1);
            } else {
                addPageItems(currentPageNumber, totalPages - blocksAfterEllipse, pageSize, pagingList, totalPages - blocksAfterEllipse + 1 -blocksBeforeEllipse);
            }
            addPageItem(pagingList, 0, ellipses, pageSize, currentPageNumber);
            addPageItems(currentPageNumber, totalPages, pageSize, pagingList, totalPages - blocksAfterEllipse + 1);
        }
        return pagingList;
    }

    private static void addPageItems(int currentPageNumber, int totalPages, int pageSize, List<PageItem> pagingList, int i2) {
        for (int i = i2; i <= totalPages; i++) {
            addPageItem(pagingList, i, null, pageSize, currentPageNumber);
        }
    }

    private static void addPageItem(final List<PageItem> list, final int pageNumber, final String ellipses, final int pageSize, final int currentPageNumber) {
        PageItem pageItem = new PageItem();
        final boolean isNotEllipse = StringUtils.isBlank(ellipses);
        pageItem.setDisplayNumber(isNotEllipse ? String.valueOf(pageNumber) : ellipses);
        pageItem.setOffset(isNotEllipse ? (pageNumber - 1) * pageSize : 0);
        pageItem.setCurrentPage(currentPageNumber == pageNumber);
        pageItem.setEllipse(!isNotEllipse);
        list.add(pageItem);
    }

    private static boolean lastPage(final int page, final int pages, final int records) {
        return page == pages || records == 0;
    }

}
