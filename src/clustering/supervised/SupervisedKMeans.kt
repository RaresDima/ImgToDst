package clustering.supervised


import clustering.supervised.utils.Centroid
import clustering.supervised.utils.Cluster
import clustering.supervised.utils.emptyCluster
import utils.list.concat
import utils.list.randomChoice
import utils.list.weightedChoice
import utils.misc.then


class SupervisedKMeans(private val points        : List<Point>,
                       private val k             : Int,
                       private val fixedCentroids: List<Point> = listOf()) {

        private val fixedK  = fixedCentroids.count()
        private val mobileK = k - fixedK

        /**
         * The KMeans++ initialization method using some given fixed centroids.
         *
         * Returns a [List] with the remaining number of mobile centroids until k total centroids are created.
         *
         * @return A [List] of mobile centroids containing as many centroids as needed to have a total of k centroids.
         */
        private fun kppInit() : List<Centroid> {
                val mobileCentroids: MutableList<Centroid> = mutableListOf()
                val allCentroids   : MutableList<Centroid> = fixedCentroids.toMutableList()
                val remainingPoints: MutableList<Point>    = points.toMutableList()

                // If there are no fixed centroids select a new mobile centroid at random.
                if (allCentroids.isEmpty()) {
                        val newCentroid = remainingPoints.randomChoice()

                        allCentroids.add(newCentroid)
                        mobileCentroids.add(newCentroid)

                        remainingPoints.remove(newCentroid)
                }

                // Select the remaining centroids using squared euclidean distances of each point to the nearest
                // centroid as weights for the random selection.
                while (allCentroids.count() < k) {
                        // For each point the squared euclidean distance to the nearest centroid
                        val dxSqr = remainingPoints.map { point -> point sqrDistanceTo allCentroids.minBy { it sqrDistanceTo point }!!}



                        val newCentroid = remainingPoints.weightedChoice(weights = dxSqr)

                        allCentroids.add(newCentroid)
                        mobileCentroids.add(newCentroid)

                        remainingPoints.remove(newCentroid)
                }

                return mobileCentroids
        }


        /**
         * Computes the [List] of [Cluster]s for the given [Centroid]s and [Point]s.
         *
         * Centroids should contain both mobile and fixed ones, in that order.
         *
         * @param centroids The [List] of [Centroid]s to clusterize the [Point]s around.
         *
         * @return The [List] of resulting [Cluster]s
         */
        private fun clusterize(centroids: List<Centroid>) : List<Cluster> {
                // Create k empty clusters.
                val clusters: MutableList<Cluster> = MutableList(centroids.count()) { emptyCluster() }

                // For each point append it to the cluster of the nearest centroid
                for (point in points) {
                        val indexOfNearestCentroid = centroids.minBy { it sqrDistanceTo point }!! then centroids::indexOf
                        clusters[indexOfNearestCentroid].add(point)
                }

                return clusters
        }


        /**
         * Recomputes the mobile [Centroid]s
         *
         * This is done by using the number mobile centroids to split the [List] of [Cluster]s into mobile
         * and fixed clusters. The fixed clusters have their centroids computed and returned.
         * The list of clusters should always contain the mobile clusters first.
         *
         * @param clusters The [List] of all [Cluster]s. The mobile clusters should come first.
         *
         * @return The [List] of [Centroid]s for the mobile clusters.
         */
        private fun computeCentroidsOf(clusters: List<Cluster>) : List<Centroid>{
                val mobileClusters : List<Cluster>         = clusters.slice(0 until mobileK)
                val mobileCentroids: MutableList<Centroid> = mutableListOf()

                mobileClusters.forEachIndexed { clusterIndex, cluster ->
                        // Average the i'th coordinate for all coordinates of all points in the cluster
                        val newCentroid =
                                (0 until points.first().dimensions())
                                        .map { dimIndex -> cluster.map { it[dimIndex] }.average().toInt() } then ::Centroid
                        mobileCentroids.add(newCentroid)
                }

                return mobileCentroids
        }


        /**
         * Computes the total J score.
         *
         * The clusters and centroids need to have mobile elements first.
         *
         * @param clusters  The [List] of [Cluster]s.
         * @param centroids The [List] of [Centroid]s.
         *
         * @return The computed J score.
         */
        private fun computeJScore(clusters: List<Cluster>, centroids: List<Centroid>) : Int =
                centroids.zip(clusters).sumBy { (centroid, cluster) -> cluster.sumBy { it sqrDistanceTo centroid } }


        /**
         * The debug.main KMeans algorithm.
         *
         * @param verbose Whether or not to print the iteration number and J score for every iteration.
         *
         * @return The list of final centroids.
         */
        fun run(verbose: Boolean = false) : List<Centroid> {
                // KMeans++ initialization
                var mobileCentroids = kppInit()

                // Initial clusters
                var newClusters = mobileCentroids concat fixedCentroids then ::clusterize
                var oldClusters = listOf<Cluster>()

                // Initial J
                var newJScore = computeJScore(newClusters, mobileCentroids concat fixedCentroids)
                var oldJScore = Int.MAX_VALUE

                if (verbose) println("ITER : INIT \t J SCORE : %8d".format(newJScore))

                var i = 1
                while(true) {

                        // Compute mobile centroids
                        mobileCentroids = computeCentroidsOf(newClusters)


                        // Compute new clusters
                        oldClusters = newClusters.toList()
                        newClusters = mobileCentroids concat fixedCentroids then ::clusterize

                        // Compute J score
                        oldJScore = newJScore
                        newJScore = computeJScore(newClusters, mobileCentroids concat fixedCentroids)

                        if (verbose) println("ITER : %4d \t J SCORE : %8d".format(i++, newJScore))

                        // If clusters remain constant then break
                        if (newJScore == oldJScore)
                                break
                }

                return mobileCentroids concat fixedCentroids
        }
}