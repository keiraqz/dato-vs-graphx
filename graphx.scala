import org.apache.spark._
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import java.util.Calendar


// triangle counting
val graph = GraphLoader.edgeListFile(sc, "hdfs://ec2-54-215-136-187.us-west-1.compute.amazonaws.com:9000/data/pokec.txt", true).partitionBy(PartitionStrategy.RandomVertexCut)
val triCounts = graph.triangleCount().vertices

Calendar.getInstance().getTime()
val sum = triCounts.map(a => a._2).reduce((a, b) => a + b)
Calendar.getInstance().getTime()


// pagerank
val graph = GraphLoader.edgeListFile(sc, "hdfs://ec2-54-215-136-187.us-west-1.compute.amazonaws.com:9000/data/youtube.txt")
Calendar.getInstance().getTime()
val ranks = graph.pageRank(0.001).vertices
Calendar.getInstance().getTime()


// Connected Components
val graph = GraphLoader.edgeListFile(sc, "hdfs://ec2-54-215-136-187.us-west-1.compute.amazonaws.com:9000/data/lj.txt")
Calendar.getInstance().getTime()
val cc = graph.connectedComponents().vertices
Calendar.getInstance().getTime()
