/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isokcet.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.glassfish.grizzly.CompletionHandler;
import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.Context;
import org.glassfish.grizzly.GrizzlyFuture;
import org.glassfish.grizzly.ProcessorResult;
import org.glassfish.grizzly.ReadResult;
import org.glassfish.grizzly.WriteResult;
import org.glassfish.grizzly.filterchain.AbstractFilterChain;
import org.glassfish.grizzly.filterchain.Filter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.FilterChainEvent;

/**
 * <p>
 * 
 * </p>
 *
 * @see
 * @author yangjun2
 * @email yangjun1120@gmail.com
 *
 */
public class NullProcessor extends AbstractFilterChain {

    @Override
    public ProcessorResult execute(FilterChainContext context) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <M> GrizzlyFuture<WriteResult> flush(Connection connection, CompletionHandler<WriteResult> completionHandler)
            throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GrizzlyFuture<FilterChainContext> fireEventUpstream(Connection connection, FilterChainEvent event,
            CompletionHandler<FilterChainContext> completionHandler) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GrizzlyFuture<FilterChainContext> fireEventDownstream(Connection connection, FilterChainEvent event,
            CompletionHandler<FilterChainContext> completionHandler) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ReadResult read(FilterChainContext context) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void fail(FilterChainContext context, Throwable failure) {
        // TODO Auto-generated method stub

    }

    @Override
    public ProcessorResult process(Context context) throws IOException {
        return ProcessorResult.createTerminate();
    }

    @Override
    public GrizzlyFuture read(Connection connection, CompletionHandler completionHandler) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GrizzlyFuture write(Connection connection, Object dstAddress, Object message,
            CompletionHandler completionHandler) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int size() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean contains(Object o) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Iterator<Filter> iterator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object[] toArray() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean add(Filter e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean remove(Object o) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Filter> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean addAll(int index, Collection<? extends Filter> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub

    }

    @Override
    public Filter get(int index) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Filter set(int index, Filter element) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void add(int index, Filter element) {
        // TODO Auto-generated method stub

    }

    @Override
    public Filter remove(int index) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int indexOf(Object o) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int lastIndexOf(Object o) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ListIterator<Filter> listIterator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ListIterator<Filter> listIterator(int index) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Filter> subList(int fromIndex, int toIndex) {
        // TODO Auto-generated method stub
        return null;
    }
}
