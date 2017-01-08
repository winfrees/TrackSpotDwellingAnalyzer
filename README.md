#TrackSpotDwellingAnalyzer

Background

To quantitate binding/pausing of THP-1 cells on an activated or pretreated endothelial cell monolayer I initially developed a track analyzer for TrackMate (http://imagej.net/TrackMate).  This analyzer is written in Java and takes advantage of the SciJava plugin framework enabled in TrackMate and Fiji.  To use this analyzer to TrackMate, add the TrackSpotDwellingAnalyer-0.0.1-SNAPSHOT.jar to the plugins folder of your Fiji installation.  When TrackMate loads it will automatically install this analyzer.  

Brief description of use

After installing the .jar file, open your image file containing the data to be tracked.  TrackMate’s segmentation strategy work better with positive contrast images (white on black)-invert if necessary (Edit->Invert).  Launch TrackMate and begin by segmenting the THP-1s, a LoG detector is a good place to start for medium size objects.  Blob diameter can be assessed empirically with the preview function.  For poor contrast images and THP-1s at ~20x start at 15 px and a threshold of 250 with the median filter and subpixel localization on.  Use the preview to visualize what objects/THP-1s are included or ignored.



Trackmate will let you then threshold THP-1s by quality, I have yet to begin adjusting this, but it may improve the segmentation in the long run.  View the objects with the “HyperStack Displayer”-takes a few moments to be ready.  The next window includes an interface to filter the spots to further improve the included objects.  This is another area I have not exploited for this application-may be worth trying in the future.  

The Tracking step is next.  I have been using the “Simple LAP Tracker”.  I chose this one as it excludes splitting and joining and the other options are too liberal or have poor perspective (only look between two frames).  I’m hoping to replace this step with a version of the “Simple LAP Tracker” that takes into account two things, tracks cannot be created nor lost mid image( except at t0) and that all tracks must move left to right.  I’ve yet to finish this implementation, but it is where we can make a substantial improvement.   The Linking and Gap-closing max distance are both at 15 px and max frame gap of 0.  As before I don’t apply any filters to the tracks as defined.  



From this window we can extract the number of pauses.  So what defines a pause?  In this analysis it is any point during which the instantaneous velocity (velocity between two frames) is less than 90% of the maximum velocity of the entire track.  In the display window you can follow these tracks by limiting frame depth and set the track color by the “Number of Pauses”.  This is the new measurement the .jar adds.   To extract the number of pauses for all tracks, hit the Analysis button. One of the three tables that opens includes “Track Statistics”.  In this table each row is a track and each track has a column, “TRACK_DWELL_COUNT”.  This is the variable I was plotting for each of the treatment conditions.









