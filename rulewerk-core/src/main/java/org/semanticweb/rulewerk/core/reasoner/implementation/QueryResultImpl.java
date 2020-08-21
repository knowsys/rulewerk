package org.semanticweb.rulewerk.core.reasoner.implementation;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/*
 * #%L
 * Rulewerk Core Components
 * %%
 * Copyright (C) 2018 - 2020 Rulewerk Developers
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.List;
import java.util.ListIterator;

import org.semanticweb.rulewerk.core.model.api.QueryResult;
import org.semanticweb.rulewerk.core.model.api.Term;

/**
 * Implements {@link QueryResult}s.
 * 
 * @author Irina Dragoste
 *
 */
public final class QueryResultImpl implements QueryResult {

	static class ShallowTermList implements List<Term> {

		final Term[] data;
		
		public ShallowTermList(Term[] data) {
			this.data = data;
		}
		
		UnsupportedOperationException uoe() { return new UnsupportedOperationException(); }
		
		@Override public boolean add(Term e) { throw uoe(); }
        @Override public boolean addAll(Collection<? extends Term> c) { throw uoe(); }
        @Override public void    clear() { throw uoe(); }
        @Override public boolean remove(Object o) { throw uoe(); }
        @Override public boolean removeAll(Collection<?> c) { throw uoe(); }
        @Override public boolean retainAll(Collection<?> c) { throw uoe(); }
        @Override public void    add(int index, Term element) { throw uoe(); }
        @Override public boolean addAll(int index, Collection<? extends Term> c) { throw uoe(); }
        @Override public Term    remove(int index) { throw uoe(); }

		@Override
		public boolean contains(Object o) {
			return indexOf(o) >= 0;
		}

		@Override
		public boolean containsAll(Collection<?> arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Term get(int index) {
			return data[index];
		}

		@Override
		public int indexOf(Object o) {
	           for (int i = 0, s = size(); i < s; i++) {
	                if (get(i).equals(o)) {
	                    return i;
	                }
	            }
	            return -1;
		}

		@Override
		public boolean isEmpty() {
			return size() == 0;
		}

		@Override
		public Iterator<Term> iterator() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int lastIndexOf(Object arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public ListIterator<Term> listIterator() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ListIterator<Term> listIterator(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Term set(int arg0, Term arg1) {
			throw uoe();
		}

		@Override
		public int size() {
			return data.length;
		}

		@Override
		public List<Term> subList(int arg0, int arg1) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object[] toArray() {
			return Arrays.copyOf(data, data.length);
		}

		@Override
		@SuppressWarnings("unchecked")
		public <T> T[] toArray(T[] a) {
			int size = data.length;
            if (a.length < size) {
                // Make a new array of a's runtime type, but my contents:
                return (T[]) Arrays.copyOf(data, size, a.getClass());
            }
            System.arraycopy(data, 0, a, 0, size);
            if (a.length > size) {
                a[size] = null; // null-terminate
            }
            return a;
		}
		

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }

            if (!(o instanceof List)) {
                return false;
            }

            Iterator<?> oit = ((List<?>) o).iterator();
            for (int i = 0, s = size(); i < s; i++) {
                if (!oit.hasNext() || !get(i).equals(oit.next())) {
                    return false;
                }
            }
            return !oit.hasNext();
        }
        
        @Override
        public int hashCode() {
            int hash = 1;
            for (int i = 0, s = size(); i < s; i++) {
                hash = 31 * hash + get(i).hashCode();
            }
            return hash;
        }
	}

	private final List<Term> terms;

	public QueryResultImpl(List<Term> terms) {
		this.terms = terms;
	}

	public static QueryResultImpl fromArray(Term[] terms) {
		return new QueryResultImpl(new ShallowTermList(terms));
	}

	@Override
	public List<Term> getTerms() {
		return this.terms;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((terms == null) ? 0 : terms.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof QueryResult)) {
			return false;
		}
		final QueryResult other = (QueryResult) obj;
		if (this.terms == null) {
			return other.getTerms() == null;
		} else {
			return this.terms.equals(other.getTerms());
		}
	}

	@Override
	public String toString() {
		return "QueryResult [terms=" + this.terms + "]";
	}

}
