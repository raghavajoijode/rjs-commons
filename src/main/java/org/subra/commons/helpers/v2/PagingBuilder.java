package org.subra.commons.helpers.v2;


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
    public static final int DEF_SHOWN_NUMBERS = 5;

    private static final String RECORD_RANGE_FORM = "%s - %s";
    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final int TWO = 2;

    private PagingBuilder() {
        throw new IllegalStateException(this.getClass().getSimpleName());
    }

    public static Paging build(final int page, final int size, final int records) {
        return build(page, size, records, DEF_SHOWN_NUMBERS, DEF_ELLIPSES);
    }

    public static Paging build(final int currentPageNumber, final int pageSize, final int totalRecordsSize, final int pagesShown, final String ellipses) {
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
        paging.setList(buildList(currentPageNumber, totalPages, pageSize, pagesShown, ellipses));
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

    private static List<PageItem> buildList(final int currentPageNumber, final int totalPages, final int pageSize, final int pagesShown, final String ellipses) {
        List<PageItem> pagingList = new LinkedList<>();
        addPageItem(pagingList, ONE, null, pageSize, currentPageNumber);
        if (totalPages <= ONE) {
            return pagingList;
        }
        int standardEdgeSize = Math.floorDiv(pagesShown, TWO);
        int adjustedLeadingSize = adjustEdgeSize(totalPages - currentPageNumber, standardEdgeSize) - ONE;
        int adjustedTrailingSize = adjustEdgeSize(currentPageNumber - ONE, standardEdgeSize) - ONE;
        int startDigit = Math.max(currentPageNumber - adjustedLeadingSize, TWO);
        int endDigit = Math.min(currentPageNumber + adjustedTrailingSize, totalPages - ONE);
        if (totalPages > pagesShown && TWO < startDigit) {
            addPageItem(pagingList, 0, ellipses, pageSize, currentPageNumber);
        }
        for (int curPage = startDigit; curPage <= endDigit; curPage++) {
            addPageItem(pagingList, curPage, null, pageSize, currentPageNumber);
        }
        if (totalPages > endDigit + ONE) {
            addPageItem(pagingList, 0, ellipses, pageSize, currentPageNumber);
        }
        addPageItem(pagingList, totalPages, null, pageSize, currentPageNumber);
        return pagingList;
    }

    private static void addPageItem(final List<PageItem> list, final int pageNumber, final String ellipses, final int pageSize, final int currerntPageNumber) {
        PageItem pageItem = new PageItem();
        final boolean isNotEllipse = StringUtils.isBlank(ellipses);
        pageItem.setDisplayNumber(isNotEllipse ? String.valueOf(pageNumber) : ellipses);
        pageItem.setOffset(isNotEllipse ? (pageNumber - 1) * pageSize : 0);
        pageItem.setCurrentPage(currerntPageNumber == pageNumber);
        pageItem.setEllipse(!isNotEllipse);
        list.add(pageItem);
    }

    private static int adjustEdgeSize(final int distanceFromEdge, final int edgeSize) {
        return distanceFromEdge < edgeSize ? edgeSize + (edgeSize - distanceFromEdge) : edgeSize;
    }

    private static boolean lastPage(final int page, final int pages, final int records) {
        return page == pages || records == 0;
    }

}
