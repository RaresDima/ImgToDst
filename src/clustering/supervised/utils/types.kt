package clustering.supervised.utils

import clustering.supervised.Point


typealias Centroid = Point
typealias Cluster  = MutableList<Point>


/**
 * Creates and returns an empty [Cluster].
 *
 * @return An empty [Cluster].
 */
fun emptyCluster() : Cluster = mutableListOf()
