package io.github.newnc.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.github.newnc.util.DataReloadTimer;
import io.github.newnc.util.JsonObject;
import io.github.newnc.util.TMDBRequester;

/**
 * This class represents an in-memory repository of movies gets from TMDB API.
 * 
 * This class implements the singleton pattern.
 *
 * @see <a href="http://www.oodesign.com/singleton-pattern.html">Singleton Pattern</a>
 */
public class MovieRepository extends AbstractRepository {
	
	/**
	 * This fields represents a instance of this class.
	 */
	private static MovieRepository instance;
	
	/**
	 * Returns an instance of this class.
	 * 
	 * @return an instance of this class.
	 */
	public static MovieRepository getInstance() {
		if (instance == null)
			instance = new MovieRepository();
		return instance;
	}
	
	/**
	 * Default constructor.
	 */
	private MovieRepository() {
		pages = new ArrayList<>();
		
		addObserver(DataReloadTimer.getTimer());
	}
	
	/**
	 * This fields represents a list of pages of the response from TMDB API.
	 */
	private List<MovieResponseAPI> pages;
	
	/**
	 * Returns a list of pages of this <code>MovieRepository</code> instance.
	 * 
	 * @return a list of pages of this <code>MovieRepository</code> instance.
	 */
	public List<MovieResponseAPI> getPages() {
		return pages;
	}
	
	/**
	 * Returns an iterator for the list of <code>pages</code> of this <code>
	 * MovieRepository</code> instance.
	 * 
	 * @return an iterator for the list of <code>pages</code> of this <code>
	 * MovieRepository</code> instance.
	 */
	public Iterator<MovieResponseAPI> getIterator() {
		return pages.iterator();
	}
	
	/**
	 * Returns a specific <code>page</code> of this <code>MovieRepository
	 * </code> instance.
	 * 
	 * @param numPage the number of the <code>page</code>.
	 * @return a specific <code>page</code> of this <code>MovieRepository
	 * </code> instance.
	 */
	public MovieResponseAPI getPage(int numPage) {
		for (MovieResponseAPI page : pages)
			if (page.getPage() == numPage)
				return page;
		return null;
	}
	
	@Override
	protected void update() {
		System.out.println("update " + System.currentTimeMillis());
		
		for (int i = 1; i <= TMDBRequester.MAXREQUEST; i++) {
			String apiResponse = TMDBRequester.requestPage(i);

			JsonObject jsonObjectFactory = new JsonObject();
			MovieResponseAPI[] movieData = jsonObjectFactory.createObject(apiResponse);
			
			pages.add(movieData[0]);
		}
		
		setChanged();
		notifyObservers();
	}
	
	@Override
	public void updateIfNeeded() {
		if (isEmpty())
			update();
	}
	
	@Override
	public void forceUpdate() {
		clear();
		
		update();
	}
	
	@Override
	public void clear() {
		pages.clear();
		
		setChanged();
		notifyObservers();
	}
	
	@Override
	public boolean isEmpty() {
		return pages.isEmpty();
	}

}