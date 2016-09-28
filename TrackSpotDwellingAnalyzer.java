

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.scijava.plugin.Plugin;

import fiji.plugin.trackmate.Dimension;
import fiji.plugin.trackmate.FeatureModel;
import fiji.plugin.trackmate.Model;
import fiji.plugin.trackmate.Spot;
import fiji.plugin.trackmate.TrackMatePlugIn_;
import fiji.plugin.trackmate.features.track.TrackAnalyzer;

@Plugin( type = TrackAnalyzer.class )
public class TrackSpotDwellingAnalyzer implements TrackAnalyzer
{

	private static final String KEY = "TRACK_SPOT_DWELLING";

	public static final String TRACK_DWELL_COUNT = "TRACK_DWELL_COUNT";

	public static final String TRACK_DWELL_MEAN_TIME = "TRACK_DWELL_MEAN_TIME";

	private static final List< String > FEATURES = new ArrayList< String >( 2 );

	private static final Map< String, Boolean > IS_INT = new HashMap< String, Boolean >( 2 );

	private static final Map< String, String > FEATURE_SHORT_NAMES = new HashMap< String, String >( 2 );

	private static final Map< String, String > FEATURE_NAMES = new HashMap< String, String >( 2 );

	private static final Map< String, Dimension > FEATURE_DIMENSIONS = new HashMap< String, Dimension >( 2 );

	static
	{
		FEATURES.add( TRACK_DWELL_COUNT );
		FEATURES.add( TRACK_DWELL_MEAN_TIME );


		IS_INT.put( TRACK_DWELL_COUNT, true );
		IS_INT.put( TRACK_DWELL_MEAN_TIME, false );


		FEATURE_NAMES.put( TRACK_DWELL_COUNT, "Number of Pauses" );
		FEATURE_NAMES.put( TRACK_DWELL_MEAN_TIME, "Pause Duration" );


		FEATURE_SHORT_NAMES.put( TRACK_DWELL_COUNT, "Pauses" );
		FEATURE_SHORT_NAMES.put( TRACK_DWELL_MEAN_TIME, "P Duration" );

		FEATURE_DIMENSIONS.put( TRACK_DWELL_COUNT, Dimension.POSITION);
		FEATURE_DIMENSIONS.put( TRACK_DWELL_MEAN_TIME, Dimension.POSITION );

	}

	private long processingTime;

	/*
	 * TRACKMODULE METHODS
	 */

	@Override
	public String getKey()
	{
		return KEY;
	}

	@Override
	public String getName()
	{
		return "Find object pauses";
	}

	// We do not use info texts for any feature actually.
	@Override
	public String getInfoText()
	{
		return "";
	}

	// The same: we don't use icons for features.
	@Override
	public ImageIcon getIcon()
	{
		return null;
	}

	/*
	 * FEATUREANALYZER METHODS
	 */

	@Override
	public List< String > getFeatures()
	{
		return FEATURES;
	}

	@Override
	public Map< String, String > getFeatureShortNames()
	{
		return FEATURE_SHORT_NAMES;
	}

	@Override
	public Map< String, String > getFeatureNames()
	{
		return FEATURE_NAMES;
	}

	@Override
	public Map< String, Dimension > getFeatureDimensions()
	{
		return FEATURE_DIMENSIONS;
	}

	/*
	 * MULTITHREADED METHODS
	 */

	@Override
	public void setNumThreads()
	{
		// We ignore multithreading for this tutorial.
	}

	@Override
	public void setNumThreads( final int numThreads )
	{
		// We ignore multithreading for this tutorial.
	}

	@Override
	public int getNumThreads()
	{
		// We ignore multithreading for this tutorial.
		return 1;
	}

	/*
	 * BENCHMARK METHOD
	 */

	@Override
	public long getProcessingTime()
	{
		return processingTime;
	}

	/*
	 * TRACKANALYZER METHODS
	 */

	@Override
	public void process( final Collection< Integer > trackIDs, final Model model )
	{
		
		// The feature model where we store the feature values:
		final FeatureModel fm = model.getFeatureModel();
		final int dwellframes = 2;
		double dwellcount = 0;
		
		
		

		// Loop over all the tracks we have to process.
		for ( final Integer trackID : trackIDs )
		{
			// The tracks are indexed by their ID. Here is how to get their
						// content:
						final Set< Spot > spots = model.getTrackModel().trackSpots( trackID );
						
						
						

						// This set is NOT ordered. If we want the first one and last one we
						// have to sort them:
						final Comparator< Spot > comparator = Spot.frameComparator;
						final List< Spot > sorted = new ArrayList< Spot >( spots );
						
						
						Collections.sort( sorted, comparator );
						Iterator<Spot> itr = spots.iterator();
						Spot startspot = sorted.get(0);
						itr.next();
						double lastvelocity = 0;
						final double max_velocity = getMaxVelocity(sorted);
						
						
						while(itr.hasNext()){
							Spot nextspot = itr.next();
								final double velocity = getCartesianDistance(startspot.getDoublePosition(0), startspot.getDoublePosition(1), nextspot.getDoublePosition(0), nextspot.getDoublePosition(1));
								final double acceleration = Math.abs(lastvelocity-velocity);
								//if(acceleration >= (2)*max_velocity){
								//	dwellcount++;
								if(velocity < 0.1*max_velocity){
									dwellcount++;
								}
								
								startspot = nextspot;
								lastvelocity = velocity;
						}	
			fm.putTrackFeature( trackID, TRACK_DWELL_COUNT,  dwellcount);
			fm.putTrackFeature( trackID, TRACK_DWELL_MEAN_TIME,  0.0);
			dwellcount = 0;
		}
	}
	
	private double getCartesianDistance(double x0, double y0, double x1, double y1){
		return Math.sqrt((x0-x1)*(x0-x1)+(y0-y1)*(y0-y1));
	}
	
	private double getMaxVelocity(List<Spot> spots){
		Iterator<Spot> itr = spots.iterator();
		double max_velocity = 0;
		Spot startspot = spots.get(0);
		itr.next();
		while(itr.hasNext()){
			Spot nextspot = itr.next();
			final double velocity = getCartesianDistance(startspot.getDoublePosition(0), startspot.getDoublePosition(1), nextspot.getDoublePosition(0), nextspot.getDoublePosition(1));
			if(velocity > max_velocity){
				max_velocity = velocity;
			}		
			startspot = nextspot;
		}
		return max_velocity;
	}

	@Override
	public boolean isLocal()
	{
		return true;
	}

	/*
	 * MAIN METHOD
	 */

	public static void main( final String[] args )
	{
		ImageJ.main( args );
		new ImagePlus( "../fiji/samples/FakeTracks.tif" ).show();
		new TrackMatePlugIn_().run( "" );
	}

	@Override
	public Map<String, Boolean> getIsIntFeature() {
		return Collections.unmodifiableMap( IS_INT );
	}

	@Override
	public boolean isManualFeature() {
		return false;
	}
}
